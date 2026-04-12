/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.nms

import org.bukkit.entity.Player

/**
 * Títulos na action bar / tela: Spigot API quando existir; senão pacotes NMS.
 */
object NmsTitles {

    @JvmStatic
    fun sendTitleBar(player: Player, title: String?, subtitle: String?, fadeIn: Int, stay: Int, fadeOut: Int) {
        runCatching {
            val spigot = player.spigot()
            val m = spigot.javaClass.getMethod(
                "sendTitle",
                String::class.java,
                String::class.java,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            )
            m.invoke(spigot, title ?: "", subtitle ?: "", fadeIn, stay, fadeOut)
            return
        }
        sendTitleBarNms(player, title, subtitle, fadeIn, stay, fadeOut)
    }

    private fun sendTitleBarNms(player: Player, title: String?, subtitle: String?, fadeIn: Int, stay: Int, fadeOut: Int) {
        NmsBridge.ensureLoaded()
        val pkg = ServerVersion.nmsPackage
        val ep = NmsBridge.getHandle(player)
        val conn = NmsBridge.getPlayerConnection(ep)

        val titlePacketClass = Class.forName("$pkg.PacketPlayOutTitle")
        val enumClass = Class.forName("$pkg.PacketPlayOutTitle\$EnumTitleAction")
        @Suppress("UNCHECKED_CAST")
        fun enumConst(name: String): Enum<*> =
            (enumClass.enumConstants as Array<Enum<*>>).first { it.name == name }
        val resetConst = enumConst("RESET")
        val titleConst = enumConst("TITLE")
        val subtitleConst = enumConst("SUBTITLE")

        val ctorTitleAction = titlePacketClass.getConstructor(enumClass, Class.forName("$pkg.IChatBaseComponent"))
        val ctorTimes = titlePacketClass.getConstructor(
            Int::class.javaPrimitiveType,
            Int::class.javaPrimitiveType,
            Int::class.javaPrimitiveType
        )

        val chatTextClass = Class.forName("$pkg.ChatComponentText")

        val resetPacket = ctorTitleAction.newInstance(resetConst, null)
        NmsBridge.sendPacket(conn, resetPacket)

        val timesPacket = ctorTimes.newInstance(fadeIn, stay, fadeOut)
        NmsBridge.sendPacket(conn, timesPacket)

        if (title != null) {
            val comp = chatTextClass.getConstructor(String::class.java).newInstance(title)
            val p = ctorTitleAction.newInstance(titleConst, comp)
            NmsBridge.sendPacket(conn, p)
        }
        if (subtitle != null) {
            val comp = chatTextClass.getConstructor(String::class.java).newInstance(subtitle)
            val p = ctorTitleAction.newInstance(subtitleConst, comp)
            NmsBridge.sendPacket(conn, p)
        }
    }
}
