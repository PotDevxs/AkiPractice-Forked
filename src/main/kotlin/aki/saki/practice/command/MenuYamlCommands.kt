/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.command

import com.jonahseguin.drink.annotation.Command
import com.jonahseguin.drink.annotation.Require
import com.jonahseguin.drink.annotation.Sender
import aki.saki.practice.menus.YamlConfigurableMenu
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class AdminYamlMenuCommand {
    @Command(name = "", desc = "Painel administrativo (menus/admin.yml)")
    @Require("practice.menu.admin")
    fun open(@Sender sender: CommandSender) {
        YamlConfigurableMenu.open(sender as Player, "admin.yml", null)
    }
}

class EventYamlMenuCommand {
    @Command(name = "", desc = "Menu de eventos (menus/events.yml)")
    @Require("practice.menu.events")
    fun open(@Sender sender: CommandSender) {
        YamlConfigurableMenu.open(sender as Player, "events.yml", null)
    }
}

class LevelsYamlMenuCommand {
    @Command(name = "", desc = "Níveis e XP (menus/levels.yml)")
    fun open(@Sender sender: CommandSender) {
        YamlConfigurableMenu.open(sender as Player, "levels.yml", "main")
    }
}

class HotbarYamlMenuCommand {
    @Command(name = "", desc = "Editor da hotbar YAML (menus/hotbar.yml)")
    @Require("practice.menu.hotbar")
    fun open(@Sender sender: CommandSender) {
        YamlConfigurableMenu.open(sender as Player, "hotbar.yml", "editor_menu")
    }
}
