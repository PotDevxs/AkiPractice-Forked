/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.menus

import aki.saki.practice.PracticePlugin
import aki.saki.practice.menu.Button
import aki.saki.practice.menu.Menu
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import java.io.File

class YamlConfigurableMenu(
    private val plugin: PracticePlugin,
    private val file: File,
    private val sectionPath: String?
) : Menu() {

    private val root: ConfigurationSection = MenuYamlSupport.loadSection(file, sectionPath)

    init {
        isPlaceholder = root.getBoolean("placeholder", true)
    }

    override fun getTitle(player: Player): String =
        root.getString("title") ?: "Menu"

    override fun getSize(): Int = root.getInt("size", 27).coerceIn(9, 54)

    override fun getButtons(player: Player): Map<Int, Button> {
        val items = root.getConfigurationSection("items") ?: return emptyMap()
        val map = linkedMapOf<Int, Button>()
        for (key in items.getKeys(false)) {
            val slot = key.toIntOrNull() ?: continue
            val sec = items.getConfigurationSection(key) ?: continue
            map[slot] = YamlSlotButton(plugin, sec)
        }
        return map
    }

    companion object {
        fun open(player: Player, fileName: String, section: String?) {
            val plugin = PracticePlugin.instance
            val file = MenuYamlSupport.menuFile(plugin, fileName)
            if (!file.exists()) {
                player.sendMessage(aki.saki.practice.utils.CC.translate("&cArquivo de menu não encontrado: &f$fileName"))
                return
            }
            YamlConfigurableMenu(plugin, file, section).openMenu(player)
        }
    }
}

private class YamlSlotButton(
    private val plugin: PracticePlugin,
    private val item: ConfigurationSection
) : Button() {

    override fun getButtonItem(player: Player): ItemStack {
        val perm = item.getString("permission")
        if (perm != null && !player.hasPermission(perm)) {
            return aki.saki.practice.utils.ItemBuilder(org.bukkit.Material.BARRIER)
                .name("&cSem permissão")
                .lore("&7$perm")
                .build()
        }
        val special = item.getString("special")
        if ("PLAYER_LEVEL_INFO".equals(special, ignoreCase = true)) {
            val profile = plugin.profileManager.findById(player.uniqueId) ?: return fallback()
            val xpIn = (profile.xp % 100).toInt()
            return aki.saki.practice.utils.ItemBuilder(org.bukkit.Material.EXPERIENCE_BOTTLE)
                .name("&eNível &f${profile.level}")
                .lore(
                    "&7XP total: &f${profile.xp}",
                    "&7Progresso no nível: &f$xpIn&7/&f100",
                    "&7Estilo no chat: &f${profile.settings.levelChatStyle ?: "DEFAULT"}"
                )
                .build()
        }
        return buildItemStack(player)
    }

    private fun fallback(): ItemStack =
        aki.saki.practice.utils.ItemBuilder(org.bukkit.Material.STONE).name(" ").build()

    private fun buildItemStack(player: Player): ItemStack {
        val matName = item.getString("material") ?: "STONE"
        val mat = try {
            org.bukkit.Material.valueOf(matName.uppercase())
        } catch (_: Exception) {
            org.bukkit.Material.STONE
        }
        val durability = item.getInt("durability", 0).toShort()
        val name = item.getString("name") ?: "&f"
        val lore = item.getStringList("lore").filter { it.isNotBlank() }
        val unbreakable = item.getBoolean("unbreakable", false)
        val b = aki.saki.practice.utils.ItemBuilder(mat).durability(durability.toInt()).name(name)
        if (lore.isNotEmpty()) b.lore(*lore.toTypedArray())
        if (unbreakable) {
            b.addFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES, org.bukkit.inventory.ItemFlag.HIDE_UNBREAKABLE)
            b.setUnbreakable(true)
        }
        return b.build()
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, hotbarButton: Int) {
        val perm = item.getString("permission")
        if (perm != null && !player.hasPermission(perm)) {
            playNeutral(player)
            return
        }
        if (item.getString("special") != null && item.getConfigurationSection("action") == null) {
            return
        }
        val action = item.getConfigurationSection("action") ?: return
        MenuActionExecutor.runFromActionSection(player, action)
    }
}
