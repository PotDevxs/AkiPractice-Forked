/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.nms

import org.bukkit.Bukkit

/**
 * Detecta revisão craft/NMS (ex.: v1_8_R3 … v1_21_R1) e versão numérica do Minecraft para ramificações de API.
 */
object ServerVersion {

    /** Ex.: v1_8_R3 */
    lateinit var nmsRevision: String
        private set

    /** Pacote craftbukkit com revisão, ex.: org.bukkit.craftbukkit.v1_8_R3 */
    lateinit var craftPackage: String
        private set

    /** Pacote net.minecraft.server com revisão (Spigot/Paper “versioned” NMS). */
    lateinit var nmsPackage: String
        private set

    /** Segundo número da versão do Minecraft (1.**21**.x → 21). */
    var minecraftMinor: Int = 0
        private set

    var patch: Int = 0
        private set

    @JvmStatic
    fun isLoaded(): Boolean = ::nmsRevision.isInitialized

    fun load() {
        val rev = detectRevision()
        nmsRevision = rev
        craftPackage = "org.bukkit.craftbukkit.$rev"
        nmsPackage = "net.minecraft.server.$rev"
        val (minor, p) = parseBukkitVersion(Bukkit.getBukkitVersion())
        minecraftMinor = minor
        patch = p
    }

    private fun detectRevision(): String {
        val regex = Regex("v1_\\d+_R\\d+")
        val serverName = Bukkit.getServer().javaClass.name
        regex.find(serverName)?.value?.let { return it }
        for (world in Bukkit.getWorlds()) {
            regex.find(world.javaClass.name)?.value?.let { return it }
        }
        throw IllegalStateException(
            "AkiPractice: não foi possível detectar revisão NMS (ex.: v1_21_R1). " +
                "Classe do servidor: $serverName"
        )
    }

    private fun parseBukkitVersion(raw: String): Pair<Int, Int> {
        val base = raw.substringBefore('-')
        val parts = base.split('.')
        val minor = parts.getOrNull(1)?.toIntOrNull() ?: 0
        val patch = parts.getOrNull(2)?.toIntOrNull() ?: 0
        return minor to patch
    }
}
