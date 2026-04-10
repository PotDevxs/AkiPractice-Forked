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
import aki.saki.practice.camp.CampInvitation
import aki.saki.practice.manager.CampManager
import aki.saki.practice.utils.CC
import com.jonahseguin.drink.annotation.Command
import com.jonahseguin.drink.annotation.Sender
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class CampCommand {

    @Command(name = "create", desc = "Create a camp (clan)")
    fun create(@Sender player: Player, name: String, tag: String) {
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
        CampManager.saveCamp(camp)
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
        if (CampManager.getInvitation(target.uniqueId, camp.uuid) != null) {
            player.sendMessage(CC.translate("&cVocê já convidou este jogador."))
            return
        }
        val list = CampManager.pendingInvites.getOrPut(target.uniqueId) { mutableListOf() }
        list.removeAll { it.campUUID == camp.uuid }
        list.add(CampInvitation(camp.uuid, player.uniqueId, target.uniqueId))
        player.sendMessage(CC.translate("&aConvite enviado para &e${target.name}&a."))
        target.sendMessage(CC.translate("&eVocê foi convidado para o camp &a${camp.name} &7[${camp.tag}]&e! Use &a/camp accept ${camp.name} &eou &c/camp decline ${camp.name}&e."))
    }

    @Command(name = "accept", desc = "Accept a camp invitation")
    fun accept(@Sender player: Player, campName: String) {
        val camp = CampManager.getByName(campName) ?: run {
            player.sendMessage(CC.translate("&cCamp não encontrado."))
            return
        }
        val inv = CampManager.getInvitation(player.uniqueId, camp.uuid) ?: run {
            player.sendMessage(CC.translate("&cConvite não encontrado ou expirado."))
            return
        }
        CampManager.pendingInvites[player.uniqueId]?.remove(inv)
        if (CampManager.getByPlayer(player.uniqueId) != null) {
            player.sendMessage(CC.translate("&cVocê já está em um camp."))
            return
        }
        camp.members.add(player.uniqueId)
        CampManager.saveCamp(camp)
        player.sendMessage(CC.translate("&aVocê entrou no camp &e${camp.name} &7[${camp.tag}]&a!"))
        camp.sendMessage("&a${player.name} &eentrou no camp.")
    }

    @Command(name = "decline", desc = "Decline a camp invitation")
    fun decline(@Sender player: Player, campName: String) {
        val camp = CampManager.getByName(campName) ?: run {
            player.sendMessage(CC.translate("&cCamp não encontrado."))
            return
        }
        val inv = CampManager.getInvitation(player.uniqueId, camp.uuid) ?: run {
            player.sendMessage(CC.translate("&cConvite não encontrado ou expirado."))
            return
        }
        CampManager.pendingInvites[player.uniqueId]?.remove(inv)
        player.sendMessage(CC.translate("&eVocê recusou o convite do camp &c${camp.name}&e."))
    }

    @Command(name = "top", desc = "Leaderboard of camps by wins")
    fun top(@Sender player: Player) {
        val list = CampManager.camps.values.sortedByDescending { it.totalWins }.take(10)
        player.sendMessage(CC.translate("&7&m--------&r &eTop Camps &7&m--------"))
        if (list.isEmpty()) {
            player.sendMessage(CC.translate("&7Nenhum camp cadastrado."))
            return
        }
        list.forEachIndexed { i, camp ->
            player.sendMessage(CC.translate("&f${i + 1}. &e${camp.name} &7[${camp.tag}] &a${camp.totalWins} vitórias"))
        }
    }
}
