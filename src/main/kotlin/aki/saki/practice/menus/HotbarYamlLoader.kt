/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.menus

import aki.saki.practice.PracticePlugin
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.logging.Level

enum class HotbarContext {
    LOBBY, PARTY, QUEUE, EVENT, SPECTATE
}

data class HotbarYamlEntry(
    val slot: Int,
    val material: Material,
    val durability: Short,
    val name: String,
    val unbreakable: Boolean,
    val actionType: String,
    val actionValue: String
)

data class HotbarEventAdminExtra(
    val enabled: Boolean,
    val permission: String,
    val slot: Int,
    val material: Material,
    val durability: Short,
    val name: String,
    val actionType: String,
    val actionValue: String
)

object HotbarYamlLoader {

    @Volatile
    private var entriesByContext: Map<HotbarContext, List<HotbarYamlEntry>> = emptyMap()

    @Volatile
    private var eventAdmin: HotbarEventAdminExtra? = null

    fun reload(plugin: PracticePlugin) {
        val file = MenuYamlSupport.menuFile(plugin, "hotbar.yml")
        if (!file.exists()) {
            entriesByContext = emptyMap()
            eventAdmin = null
            return
        }
        try {
            val cfg = YamlConfiguration.loadConfiguration(file)
            val hotbarRoot = cfg.getConfigurationSection("hotbar") ?: run {
                entriesByContext = emptyMap()
                eventAdmin = null
                return
            }
            val map = mutableMapOf<HotbarContext, List<HotbarYamlEntry>>()
            for (ctx in HotbarContext.values()) {
                val sec = hotbarRoot.getConfigurationSection(ctx.name.lowercase()) ?: continue
                val list = sec.getList("items") ?: continue
                val parsed = list.mapNotNull { elem -> parseEntry(elem) }
                if (parsed.isNotEmpty()) map[ctx] = parsed
            }
            entriesByContext = map
            eventAdmin = parseEventAdmin(cfg.getConfigurationSection("event_admin_item"))
        } catch (e: Exception) {
            plugin.logger.log(Level.WARNING, "Falha ao ler menus/hotbar.yml", e)
        }
    }

    fun getEntries(context: HotbarContext): List<HotbarYamlEntry> =
        entriesByContext[context] ?: emptyList()

    fun getEventAdminExtra(): HotbarEventAdminExtra? = eventAdmin

    private fun parseEventAdmin(sec: ConfigurationSection?): HotbarEventAdminExtra? {
        if (sec == null || !sec.getBoolean("enabled")) return null
        val perm = sec.getString("permission") ?: return null
        val slot = sec.getInt("slot", 0)
        val mat = parseMaterial(sec.getString("material") ?: "HOPPER")
        val dur = sec.getInt("durability", 0).toShort()
        val name = sec.getString("name") ?: "&eAdmin"
        val act = sec.getConfigurationSection("action") ?: return null
        val type = act.getString("type") ?: "player_command"
        val value = act.getString("value") ?: ""
        return HotbarEventAdminExtra(true, perm, slot, mat, dur, name, type, value)
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseEntry(elem: Any?): HotbarYamlEntry? {
        val map = elem as? Map<String, Any?> ?: return null
        val slot = (map["slot"] as? Number)?.toInt() ?: return null
        if (slot !in 0..8) return null
        val mat = parseMaterial(map["material"]?.toString() ?: "STONE")
        val dur = (map["durability"] as? Number)?.toInt()?.toShort() ?: 0
        val name = map["name"]?.toString() ?: "&fItem"
        val unbreakable = map["unbreakable"] == true
        val actMap = map["action"] as? Map<String, Any?> ?: return null
        val type = actMap["type"]?.toString() ?: "internal"
        val value = actMap["value"]?.toString() ?: ""
        return HotbarYamlEntry(slot, mat, dur, name, unbreakable, type, value)
    }

    private fun parseMaterial(name: String): Material {
        return try {
            Material.valueOf(name.uppercase())
        } catch (_: Exception) {
            Material.STONE
        }
    }
}
