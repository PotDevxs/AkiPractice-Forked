/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.nms

import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 * Acesso NMS/CraftBukkit por reflexão, compatível com Spigot/Paper 1.8–1.21+ (pacote versionado v1_XX_RX).
 */
object NmsBridge {

    private var coreInitialized = false
    private var ctorVelocityFromEntity: Constructor<*>? = null
    private var ctorDestroy: Constructor<*>? = null
    private lateinit var ctorMetadata: Constructor<*>
    private lateinit var classArmorStand: Class<*>
    private lateinit var ctorArmorStand: Constructor<*>
    private lateinit var classChatComponent: Class<*>
    private lateinit var ctorChatText: Constructor<*>

    fun ensureLoaded() {
        if (!ServerVersion.isLoaded()) {
            ServerVersion.load()
        }
        if (coreInitialized) return
        val pkg = ServerVersion.nmsPackage
        val velClass = Class.forName("$pkg.PacketPlayOutEntityVelocity")
        ctorVelocityFromEntity = runCatching {
            velClass.getConstructor(Class.forName("$pkg.Entity"))
        }.getOrNull() ?: runCatching {
            velClass.getConstructor(Class.forName("$pkg.EntityLiving"))
        }.getOrNull()

        val destroyClass = Class.forName("$pkg.PacketPlayOutEntityDestroy")
        ctorDestroy = destroyClass.declaredConstructors.find { ctor ->
            ctor.parameterCount == 1 && ctor.parameterTypes[0] == IntArray::class.java
        } ?: destroyClass.declaredConstructors.find { ctor ->
            ctor.parameterCount == 1 && ctor.parameterTypes[0].componentType == Int::class.javaPrimitiveType
        } ?: destroyClass.declaredConstructors.find { ctor ->
            ctor.parameterCount == 1 && ctor.isVarArgs
        }

        ctorMetadata = Class.forName("$pkg.PacketPlayOutEntityMetadata").getConstructor(
            Int::class.javaPrimitiveType,
            Class.forName("$pkg.DataWatcher"),
            Boolean::class.javaPrimitiveType
        )
        classArmorStand = Class.forName("$pkg.EntityArmorStand")
        val worldClass = Class.forName("$pkg.World")
        ctorArmorStand = classArmorStand.getConstructor(
            worldClass,
            Double::class.javaPrimitiveType,
            Double::class.javaPrimitiveType,
            Double::class.javaPrimitiveType
        )
        classChatComponent = Class.forName("$pkg.IChatBaseComponent")
        ctorChatText = Class.forName("$pkg.ChatComponentText").getConstructor(String::class.java)
        coreInitialized = true
    }

    fun getHandle(craftObject: Any): Any =
        craftObject.javaClass.getMethod("getHandle").invoke(craftObject)

    fun getWorldHandle(world: World): Any = getHandle(world)

    fun getEntityHandle(entity: Entity): Any = getHandle(entity)

    /**
     * Resolve entidade Bukkit pelo ID de rede (substitui CraftWorld.handle.getEntity em versões antigas).
     */
    fun getBukkitEntityById(world: World, entityId: Int): Entity? {
        ensureLoaded()
        val wh = getWorldHandle(world)
        for (name in arrayOf("getEntity", "a", "b")) {
            try {
                val m = wh.javaClass.getMethod(name, Int::class.javaPrimitiveType)
                val nms = m.invoke(wh, entityId) ?: continue
                return nms.javaClass.getMethod("getBukkitEntity").invoke(nms) as? Entity
            } catch (_: Throwable) { }
        }
        for (m in wh.javaClass.methods) {
            if (m.parameterCount != 1 || m.returnType == Void.TYPE) continue
            if (m.parameterTypes[0] != Int::class.javaPrimitiveType) continue
            if (!m.returnType.name.contains("entity", ignoreCase = true)) continue
            val nms = runCatching { m.invoke(wh, entityId) }.getOrNull() ?: continue
            val bukkit = runCatching {
                nms.javaClass.getMethod("getBukkitEntity").invoke(nms) as? Entity
            }.getOrNull()
            if (bukkit != null) return bukkit
        }
        return null
    }

    fun getPlayerConnection(entityPlayer: Any): Any {
        val c = entityPlayer.javaClass
        for (name in arrayOf("playerConnection", "connection", "c", "b")) {
            try {
                val f = c.getDeclaredField(name)
                f.isAccessible = true
                val v = f.get(entityPlayer)
                if (v != null) return v
            } catch (_: NoSuchFieldException) {
                try {
                    val m = c.methods.find { it.name == name && it.parameterCount == 0 }
                    if (m != null) {
                        val v = m.invoke(entityPlayer)
                        if (v != null) return v
                    }
                } catch (_: Exception) { }
            }
        }
        for (f in c.declaredFields) {
            if (!f.type.name.contains("Connection")) continue
            f.isAccessible = true
            val v = f.get(entityPlayer)
            if (v != null) return v
        }
        throw NoSuchFieldException("playerConnection/connection em ${c.name}")
    }

    fun sendPacket(connection: Any, packet: Any) {
        val packetClass = packet.javaClass
        for (m in connection.javaClass.methods) {
            if (m.parameterCount != 1) continue
            val p = m.parameterTypes[0]
            if (p.isAssignableFrom(packetClass) || p == Any::class.java) {
                if (m.name == "sendPacket" || m.name == "a" || m.name == "send") {
                    m.invoke(connection, packet)
                    return
                }
            }
        }
        for (m in connection.javaClass.methods) {
            if (m.name != "sendPacket" || m.parameterCount != 1) continue
            if (m.parameterTypes[0].isAssignableFrom(packetClass)) {
                m.invoke(connection, packet)
                return
            }
        }
        throw NoSuchMethodException("sendPacket em ${connection.javaClass.name} para ${packetClass.name}")
    }

    fun sendPacket(player: Player, packet: Any) {
        ensureLoaded()
        val ep = getHandle(player)
        val conn = getPlayerConnection(ep)
        sendPacket(conn, packet)
    }

    fun newPacketEntityDestroy(vararg ids: Int): Any {
        ensureLoaded()
        val arr = ids.toIntArray()
        ctorDestroy?.let { return it.newInstance(arr) }
        val destroyClass = Class.forName("${ServerVersion.nmsPackage}.PacketPlayOutEntityDestroy")
        for (ctor in destroyClass.declaredConstructors) {
            if (ctor.parameterCount != 1) continue
            val t = ctor.parameterTypes[0]
            if (t == IntArray::class.java) return ctor.newInstance(arr)
        }
        throw IllegalStateException("PacketPlayOutEntityDestroy sem construtor compatível")
    }

    fun newPacketEntityMetadata(entityId: Int, dataWatcher: Any, allMeta: Boolean): Any {
        ensureLoaded()
        return ctorMetadata.newInstance(entityId, dataWatcher, allMeta)
    }

    fun newPacketSpawnEntityLiving(entityLiving: Any): Any {
        ensureLoaded()
        val pkg = ServerVersion.nmsPackage
        val candidates = listOf("PacketPlayOutSpawnEntityLiving", "PacketPlayOutSpawnEntity")
        for (simple in candidates) {
            runCatching {
                val c = Class.forName("$pkg.$simple")
                for (ctor in c.declaredConstructors) {
                    if (ctor.parameterCount == 1 && ctor.parameterTypes[0].isAssignableFrom(entityLiving.javaClass)) {
                        return ctor.newInstance(entityLiving)
                    }
                }
            }
        }
        throw IllegalStateException("Pacote spawn de entidade viva não encontrado para ${entityLiving.javaClass.name}")
    }

    fun newEntityArmorStand(world: World, x: Double, y: Double, z: Double): Any {
        ensureLoaded()
        val wh = getWorldHandle(world)
        return ctorArmorStand.newInstance(wh, x, y, z)
    }

    fun armorStandSetCustomName(stand: Any, line: String) {
        ensureLoaded()
        val voidLine = line.equals("<void>", ignoreCase = true)
        val display = if (voidLine) "" else line
        if (ServerVersion.minecraftMinor >= 13) {
            val component = ctorChatText.newInstance(display)
            val m = stand.javaClass.methods.find {
                it.name == "setCustomName" && it.parameterTypes.size == 1 &&
                    classChatComponent.isAssignableFrom(it.parameterTypes[0])
            } ?: stand.javaClass.getMethod("setCustomName", classChatComponent)
            m.invoke(stand, component)
        } else {
            stand.javaClass.getMethod("setCustomName", String::class.java).invoke(stand, display)
        }
    }

    fun armorStandSetCustomNameVisible(stand: Any, visible: Boolean) {
        invokeBooleanSetter(stand, visible, "setCustomNameVisible")
    }

    fun armorStandSetInvisible(stand: Any, invisible: Boolean) {
        invokeBooleanSetter(stand, invisible, "setInvisible")
    }

    fun armorStandSetSmall(stand: Any, small: Boolean) {
        invokeBooleanSetter(stand, small, "setSmall")
    }

    private fun invokeBooleanSetter(target: Any, value: Boolean, primary: String) {
        val c = target.javaClass
        val m = c.methods.find {
            it.name == primary && it.parameterCount == 1 && it.parameterTypes[0] == Boolean::class.javaPrimitiveType
        } ?: throw NoSuchMethodException("$primary em ${c.name}")
        m.invoke(target, value)
    }

    fun getEntityId(nmsEntity: Any): Int {
        val m = nmsEntity.javaClass.methods.find { it.name == "getId" && it.parameterCount == 0 }
            ?: throw NoSuchMethodException("getId em ${nmsEntity.javaClass.name}")
        return m.invoke(nmsEntity) as Int
    }

    fun getDataWatcher(nmsEntity: Any): Any {
        ensureLoaded()
        val dwClass = Class.forName("${ServerVersion.nmsPackage}.DataWatcher")
        nmsEntity.javaClass.methods.find { m ->
            m.parameterCount == 0 && dwClass.isAssignableFrom(m.returnType)
        }?.let { return it.invoke(nmsEntity) }
        throw NoSuchMethodException("DataWatcher em ${nmsEntity.javaClass.name}")
    }

    fun setEntityMot(entityPlayer: Any, x: Double, y: Double, z: Double) {
        val c = entityPlayer.javaClass
        try {
            c.getDeclaredField("motX").apply { isAccessible = true }.setDouble(entityPlayer, x)
            c.getDeclaredField("motY").apply { isAccessible = true }.setDouble(entityPlayer, y)
            c.getDeclaredField("motZ").apply { isAccessible = true }.setDouble(entityPlayer, z)
            return
        } catch (_: NoSuchFieldException) { }
        val vec3 = resolveVec3(x, y, z)
        for (name in arrayOf("setDeltaMovement", "setMot", "g")) {
            try {
                val m = c.methods.find { it.name == name && it.parameterCount == 1 }
                if (m != null) {
                    m.invoke(entityPlayer, vec3)
                    return
                }
            } catch (_: Exception) { }
        }
        throw NoSuchMethodException("mot / deltaMovement em ${c.name}")
    }

    private fun resolveVec3(x: Double, y: Double, z: Double): Any {
        val cl = ServerVersion.nmsPackage
        val candidates = listOf(
            "$cl.Vec3D",
            "$cl.Vec3",
            "net.minecraft.world.phys.Vec3",
            "net.minecraft.world.phys.Vec3D"
        )
        for (cn in candidates) {
            runCatching {
                val vc = Class.forName(cn)
                for (ctor in vc.constructors) {
                    if (ctor.parameterCount == 3 &&
                        ctor.parameterTypes[0] == Double::class.javaPrimitiveType
                    ) {
                        return ctor.newInstance(x, y, z)
                    }
                }
            }
        }
        throw IllegalStateException("Vec3 não resolvido")
    }

    fun sendPacketEntityVelocity(entityPlayer: Any) {
        ensureLoaded()
        val ctor = ctorVelocityFromEntity
            ?: throw IllegalStateException("PacketPlayOutEntityVelocity(Entity) indisponível")
        val packet = ctor.newInstance(entityPlayer)
        val conn = getPlayerConnection(entityPlayer)
        sendPacket(conn, packet)
    }

    fun setVelocityChanged(entityPlayer: Any, value: Boolean) {
        val c = entityPlayer.javaClass
        for (name in arrayOf("velocityChanged", "hurtMarked")) {
            try {
                c.getDeclaredField(name).apply { isAccessible = true }.setBoolean(entityPlayer, value)
                return
            } catch (_: NoSuchFieldException) { }
        }
    }

    fun getPing(player: Player): Int {
        runCatching {
            val m: Method = Player::class.java.getMethod("getPing")
            return m.invoke(player) as Int
        }
        ensureLoaded()
        val ep = getHandle(player)
        val f = ep.javaClass.getDeclaredField("ping")
        f.isAccessible = true
        return f.getInt(ep)
    }
}

private fun IntArray.toIntArray() {
    TODO("Not yet implemented")
}
