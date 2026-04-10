/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.menus

import aki.saki.practice.PracticePlugin
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object MenuYamlSupport {

    private const val MENUS_DIR = "menus"

    fun menusFolder(plugin: PracticePlugin): File = File(plugin.dataFolder, MENUS_DIR)

    fun menuFile(plugin: PracticePlugin, fileName: String): File = File(menusFolder(plugin), fileName)

    fun extractDefaults(plugin: PracticePlugin) {
        val names = listOf("admin.yml", "events.yml", "hotbar.yml", "levels.yml")
        for (name in names) {
            val path = "$MENUS_DIR/$name"
            val out = File(plugin.dataFolder, path)
            if (!out.exists()) {
                out.parentFile.mkdirs()
                plugin.saveResource(path, false)
            }
        }
    }

    fun loadSection(file: File, sectionPath: String?): ConfigurationSection {
        val cfg = YamlConfiguration.loadConfiguration(file)
        if (sectionPath.isNullOrBlank()) return cfg
        return cfg.getConfigurationSection(sectionPath) ?: cfg
    }
}
