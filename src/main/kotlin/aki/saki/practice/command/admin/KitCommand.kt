/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.command.admin

import com.jonahseguin.drink.annotation.Command
import com.jonahseguin.drink.annotation.Require
import com.jonahseguin.drink.annotation.Sender
import aki.saki.practice.kit.Kit
import aki.saki.practice.kit.admin.AdminKitManageMenu
import aki.saki.practice.manager.QueueManager
import aki.saki.practice.knockback.KnockbackService
import aki.saki.practice.PracticePlugin
import aki.saki.practice.profile.Profile
import aki.saki.practice.profile.statistics.KitStatistic
import aki.saki.practice.ui.kit.KitMenu
import aki.saki.practice.utils.CC
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture

/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ lPractice
 * @author yek4h © 2024
 * Date: 17/06/2024
*/

class KitCommand {

    @Command(name = "", desc = "Kit setup commands")
    @Require("practice.command.kit.setup")
    fun help(@Sender sender: CommandSender) {
        sender.sendMessage("""
            ${CC.PRIMARY}Comandos de kit:
            ${CC.SECONDARY}/create <name> - Cria um kit
            ${CC.SECONDARY}/content <kit> - Define o conteúdo de um kit
            ${CC.SECONDARY}/items <kit> - Recebe os itens de um kit
            ${CC.SECONDARY}/icon <kit> - Define o ícone de um kit
            ${CC.SECONDARY}/displayname <kit> <name> - Define o nome de exibição de um kit
            ${CC.SECONDARY}/admin <kit> - Abre o menu de gerenciamento do kit
            ${CC.SECONDARY}/manage - Abre o menu geral de gerenciamento
        """.trimIndent())
    }

    @Command(name = "create", desc = "Create a Kit")
    @Require("practice.command.kit.create")
    fun create(@Sender sender: CommandSender, name: String) {
        if (PracticePlugin.instance.kitManager.getKit(name) != null) {
            sender.sendMessage(CC.RED + "Esse kit já existe!")
            return
        }

        PracticePlugin.instance.kitManager.createKit(name)

        CompletableFuture.runAsync {
            for (document in PracticePlugin.instance.database.profileListAll()) {
                val profile = Profile(UUID.fromString(document.getString("_id")), null)
                profile.load(document)

                profile.kitStatistics.add(KitStatistic(name))
                profile.save()
            }
        }

        sender.sendMessage("${CC.PRIMARY}Kit ${CC.SECONDARY}$name${CC.PRIMARY} criado com sucesso!")
    }

    @Command(name = "content", desc = "Set the content of a kit")
    @Require("practice.command.kit.content")
    fun setContent(@Sender sender: CommandSender, kit: Kit) {
        val player = sender as? Player ?: return

        if (player.gameMode != GameMode.SURVIVAL) {
            player.sendMessage("${CC.RED}Você precisa estar no modo sobrevivência para definir o conteúdo do inventário!")
            return
        }

        kit.content = player.inventory.contents.clone()
        kit.armorContent = player.inventory.armorContents.clone()
        PracticePlugin.instance.kitManager.save()

        CompletableFuture.runAsync {
            for (document in PracticePlugin.instance.database.profileListAll()) {
                val profile = Profile(UUID.fromString(document.getString("_id")), document.getString("name"))
                profile.load(document)

                val editedKits = profile.getKitStatistic(kit.name)?.editedKits ?: continue

                editedKits.fill(null)

                profile.save()
            }
        }

        player.sendMessage("${CC.PRIMARY}Conteúdo de itens do kit ${CC.SECONDARY}${kit.name}${CC.PRIMARY} definido com sucesso!")
    }

    @Command(name = "items", desc = "Receive the item of a Kit")
    @Require("practice.command.kit.items")
    fun getItems(@Sender sender: CommandSender, kit: Kit) {
        val player = sender as? Player ?: return

        player.inventory.contents = kit.content
        player.inventory.armorContents = kit.armorContent
        player.sendMessage("${CC.PRIMARY}Conteúdo de itens do kit ${CC.SECONDARY}${kit.name}${CC.PRIMARY} carregado com sucesso!")
    }

    @Command(name = "kb", desc = "Receive the item of a Kit")
    @Require("practice.command.kit.kb")
    fun setKb(@Sender sender: CommandSender, kit: Kit, string: String) {
        val player = sender as? Player ?: return

        if (!KnockbackService.hasProfile(string)) {
            player.sendMessage("${CC.RED}Perfil de KB inválido. Perfis disponíveis: ${CC.SECONDARY}${KnockbackService.getProfileNames().joinToString("${CC.GRAY}, ${CC.SECONDARY}")}")
            return
        }

        kit.knockbackProfile = KnockbackService.getProfile(string).name
        PracticePlugin.instance.kitManager.save()
        player.sendMessage("${CC.PRIMARY}KB do kit ${CC.SECONDARY}${kit.name}${CC.PRIMARY} definido para ${CC.SECONDARY}${kit.knockbackProfile}${CC.PRIMARY} com sucesso!")
    }

    @Command(name = "icon", desc = "Set the icon of a Kit")
    @Require("practice.command.kit.icon")
    fun setIcon(@Sender sender: CommandSender, kit: Kit) {
        val player = sender as? Player ?: return

        if (player.itemInHand == null || player.itemInHand.type == Material.AIR) {
            player.sendMessage("${CC.RED}Você não está segurando nenhum item!")
            return
        }
        kit.displayItem = player.itemInHand
        PracticePlugin.instance.kitManager.save()

        QueueManager.queues.filter { it.key.first.name.equals(kit.name, ignoreCase = true) }.forEach { (_, queue) ->
            queue.kit.displayItem = player.itemInHand
        }

        player.sendMessage("${CC.PRIMARY}Item de exibição do kit ${CC.SECONDARY}${kit.name}${CC.PRIMARY} definido com sucesso!")
    }

    @Command(name = "admin", desc = "Open the kit management menu")
    @Require("practice.command.kit.admin")
    fun edit(@Sender sender: CommandSender, kit: Kit) {
        val player = sender as? Player ?: return
        AdminKitManageMenu(kit).openMenu(player)
    }

    @Command(name = "displayname", desc = "Set the name of a Kit")
    @Require("practice.command.kit.displayname")
    fun setDisplayName(@Sender sender: CommandSender, kit: Kit, name: String) {
        val player = sender as? Player ?: return

        kit.displayName = name
        player.sendMessage("${CC.YELLOW}Você atualizou ${CC.AQUA}${kit.name}${CC.YELLOW} para exibir como ${CC.GREEN}${kit.displayName}")
    }

    @Command(name = "manage", desc = "Open the general management menu")
    @Require("practice.command.kit.manage")
    fun manage(@Sender sender: CommandSender) {
        val player = sender as? Player ?: return
        KitMenu().openMenu(player)
    }
}
