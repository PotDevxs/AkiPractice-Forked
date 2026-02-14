/*
 * This project can be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.command

import aki.saki.practice.PracticePlugin
import aki.saki.practice.camp.Camp
import aki.saki.practice.manager.CampManager
import aki.saki.practice.utils.CC
import com.jonahseguin.drink.annotation.Command
import com.jonahseguin.drink.annotation.Sender
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class CampCommand {

    @Command(name = "create", desc = "Create a camp (clan)")
    fun create(@Sender player: Player, name: String, tag: String) {
        val profile = PracticePlugin.instance.profileManager.findById(player.uniqueId) ?: return
        if (CampManager.getByPlayer(player.uniqueId) != null) {
            player.sendMessage(CC.translate("&cVocê já está em um camp! Use /camp leave primeiro."))
            return
        }
        val camp = CampManager.create(name, tag, player.uniqueId)
        if (camp == null) {
            player.sendMessage(CC.translate("&cNome ou tag já em uso."))
            return
        }
        player.sendMessage(CC.translate("&aCamp &e${camp.name} &7[${camp.tag}] &acriado!"))
    }

    @Command(name = "leave", desc = "Leave your camp")
    fun leave(@Sender player: Player) {
        val camp = CampManager.getByPlayer(player.uniqueId) ?: run {
            player.sendMessage(CC.translate("&cVocê não está em um camp."))
            return
        }
        if (camp.isLeader(player.uniqueId)) {
            player.sendMessage(CC.translate("&cLíderes devem usar /camp disband para dissolver o camp."))
            return
        }
        camp.members.remove(player.uniqueId)
        player.sendMessage(CC.translate("&eVocê saiu do camp &a${camp.name}&e."))
        camp.sendMessage("&7${player.name} &esaiu do camp.")
    }

    @Command(name = "disband", desc = "Disband your camp (leader only)")
    fun disband(@Sender player: Player) {
        val camp = CampManager.getByPlayer(player.uniqueId) ?: run {
            player.sendMessage(CC.translate("&cVocê não está em um camp."))
            return
        }
        if (!camp.isLeader(player.uniqueId)) {
            player.sendMessage(CC.translate("&cApenas o líder pode dissolver o camp."))
            return
        }
        camp.sendMessage("&cO camp foi dissolvido por ${player.name}.")
        CampManager.remove(camp)
        player.sendMessage(CC.translate("&aCamp dissolvido."))
    }

    @Command(name = "info", desc = "Show your camp info")
    fun info(@Sender player: Player) {
        val camp = CampManager.getByPlayer(player.uniqueId) ?: run {
            player.sendMessage(CC.translate("&cVocê não está em um camp."))
            return
        }
        val leaderName = Bukkit.getOfflinePlayer(camp.leader).name ?: "?"
        player.sendMessage(CC.translate("&7&m--------&r &e${camp.name} &7[${camp.tag}] &7&m--------"))
        player.sendMessage(CC.translate("&fLíder: &a$leaderName"))
        player.sendMessage(CC.translate("&fMembros: &a${camp.members.size}"))
        camp.members.take(10).forEach { uuid ->
            val n = Bukkit.getOfflinePlayer(uuid).name ?: uuid.toString()
            player.sendMessage(CC.translate("&7- &f$n"))
        }
        if (camp.members.size > 10) player.sendMessage(CC.translate("&7... e mais ${camp.members.size - 10}"))
    }

    @Command(name = "invite", desc = "Invite a player to your camp")
    fun invite(@Sender player: Player, target: Player) {
        val camp = CampManager.getByPlayer(player.uniqueId) ?: run {
            player.sendMessage(CC.translate("&cVocê não está em um camp."))
            return
        }
        if (!camp.isLeader(player.uniqueId)) {
            player.sendMessage(CC.translate("&cApenas o líder pode convidar."))
            return
        }
        if (camp.isMember(target.uniqueId)) {
            player.sendMessage(CC.translate("&cEste jogador já está no camp."))
            return
        }
        // TODO: CampInvitation system (like PartyInvitation) para aceitar/recusar
        camp.members.add(target.uniqueId)
        player.sendMessage(CC.translate("&a${target.name} foi adicionado ao camp."))
        target.sendMessage(CC.translate("&eVocê entrou no camp &a${camp.name} &7[${camp.tag}]&e!"))
    }
}
