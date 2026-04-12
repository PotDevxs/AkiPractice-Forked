/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.menus

import aki.saki.practice.Locale
import aki.saki.practice.PracticePlugin
import aki.saki.practice.kit.editor.KitEditorSelectKitMenu
import aki.saki.practice.manager.EventManager
import aki.saki.practice.manager.PartyManager
import aki.saki.practice.manager.QueueManager
import aki.saki.practice.match.Match
import aki.saki.practice.menu.MenuManager
import aki.saki.practice.party.Party
import aki.saki.practice.party.duel.procedure.PartyDuelProcedure
import aki.saki.practice.party.duel.procedure.menu.PartyDuelSelectPartyMenu
import aki.saki.practice.profile.ProfileState
import aki.saki.practice.ui.SettingsMenu
import aki.saki.practice.ui.ThemeMenu
import aki.saki.practice.ui.events.EventHostMenu
import aki.saki.practice.ui.ffa.FFAChoosingMenu
import aki.saki.practice.ui.kit.KitMenu
import aki.saki.practice.ui.leaderboards.LeaderboardRankedMenu
import aki.saki.practice.ui.party.PartyInformationMenu
import aki.saki.practice.ui.party.event.PartyStartEventMenu
import aki.saki.practice.ui.queue.ranked.RankedQueueMenu
import aki.saki.practice.ui.queue.unranked.UnrankedQueueMenu
import aki.saki.practice.mission.MissionManager
import aki.saki.practice.profile.hotbar.Hotbar
import aki.saki.practice.utils.CC
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

object MenuActionExecutor {

    fun runFromActionSection(player: Player, action: ConfigurationSection?) {
        if (action == null) return
        val type = action.getString("type") ?: return
        val value = action.getString("value") ?: ""
        val amount = action.getInt("amount", 0)
        val style = action.getString("style")
        execute(player, type, value, amount, style)
    }

    fun execute(player: Player, type: String, value: String, amount: Int = 0, style: String? = null) {
        when (type.lowercase()) {
            "internal" -> runInternal(player, value, amount, style)
            "player_command" -> player.performCommand(value.trim().removePrefix("/"))
            "console_command" -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), replacePlaceholders(player, value))
            "message" -> player.sendMessage(CC.translate(replacePlaceholders(player, value)))
            "open_menu" -> openMenuPath(player, value)
        }
    }

    private fun replacePlaceholders(player: Player, s: String): String =
        s.replace("<player>", player.name).replace("<uuid>", player.uniqueId.toString())

    private fun openMenuPath(player: Player, path: String) {
        val parts = path.split(":").map { it.trim() }.filter { it.isNotEmpty() }
        val fileName = parts[0]
        val section = parts.getOrNull(1)
        YamlConfigurableMenu.open(player, fileName, section)
    }

    private fun runInternal(player: Player, key: String, amount: Int, style: String?) {
        val plugin = PracticePlugin.instance
        val profile = plugin.profileManager.findById(player.uniqueId) ?: return
        when (key.uppercase()) {
            "CLOSE_MENU" -> player.closeInventory()
            "UNRANKED_QUEUE" -> if (profile.state == ProfileState.LOBBY) UnrankedQueueMenu().openMenu(player)
            "RANKED_QUEUE" -> if (profile.state == ProfileState.LOBBY) RankedQueueMenu().openMenu(player)
            "KIT_EDITOR" -> if (profile.state == ProfileState.LOBBY) KitEditorSelectKitMenu().openMenu(player)
            "PARTY_CREATE" -> if (profile.state == ProfileState.LOBBY) {
                val party = Party(profile.uuid)
                party.players.add(profile.uuid)
                PartyManager.parties.add(party)
                profile.party = party.uuid
                Hotbar.giveHotbar(profile)
                player.sendMessage(Locale.CREATED_PARTY.getMessage())
            }
            "PARTY_INFO" -> profile.party?.let { PartyManager.getByUUID(it)?.let { p -> PartyInformationMenu(p).openMenu(player) } }
            "PARTY_EVENT" -> PartyStartEventMenu().openMenu(player)
            "PARTY_DUEL" -> {
                PartyDuelProcedure(profile.uuid).apply { PartyDuelProcedure.duelProcedures.add(this) }
                PartyDuelSelectPartyMenu().openMenu(player)
            }
            "PARTY_LEAVE" -> leaveParty(player, profile)
            "QUEUE_LEAVE" -> {
                profile.state = ProfileState.LOBBY
                profile.queuePlayer = null
                QueueManager.getQueue(profile.uuid)?.queuePlayers?.removeIf { it.uuid == player.uniqueId }
                Hotbar.giveHotbar(profile)
            }
            "EVENT_LEAVE" -> {
                EventManager.event?.removePlayer(player)
                EventManager.event?.let { ev ->
                    Bukkit.broadcastMessage("${CC.GREEN}${player.name}${CC.YELLOW} saiu do evento. ${CC.GRAY}(${ev.players.size}/${ev.requiredPlayers})")
                }
            }
            "SPECTATE_LEAVE" -> {
                profile.state = ProfileState.LOBBY
                Match.getSpectator(player.uniqueId)?.removeSpectator(player)
                Hotbar.giveHotbar(profile)
            }
            "LEADERBOARD" -> LeaderboardRankedMenu(plugin).openMenu(player)
            "SETTINGS" -> if (profile.state == ProfileState.LOBBY) SettingsMenu().openMenu(player)
            "THEME" -> if (profile.state == ProfileState.LOBBY) ThemeMenu().openMenu(player)
            "FFA_CHOOSE" -> if (profile.state == ProfileState.LOBBY) FFAChoosingMenu().openMenu(player)
            "KIT_MENU_ADMIN" -> KitMenu().openMenu(player)
            "EVENT_HOST_MENU" -> EventHostMenu().openMenu(player)
            "HOTBAR_RELOAD" -> {
                HotbarYamlLoader.reload(plugin)
                Hotbar.giveHotbar(profile)
                player.sendMessage(CC.translate("&aHotbar recarregada a partir de &fmenus/hotbar.yml&a."))
            }
            "LEVEL_ADD_XP" -> {
                if (amount <= 0) return
                if (!player.hasPermission("practice.levels.grant_self")) {
                    player.sendMessage(CC.translate("&cSem permissão."))
                    return
                }
                profile.addXp(amount.toLong())
                profile.save(true)
                player.sendMessage(CC.translate("&a+$amount XP. Nível atual: &f${profile.level}"))
                MenuManager.refresh(player)
            }
            "LEVEL_STYLE_SET" -> {
                val st = style ?: return
                profile.settings.levelChatStyle = if (st.equals("DEFAULT", ignoreCase = true)) null else st.uppercase()
                profile.save(true)
                player.sendMessage(CC.translate("&aEstilo de nível no chat atualizado."))
                MenuManager.refresh(player)
            }
            "LEVEL_ADMIN_PLAYERS" -> {
                if (!player.hasPermission("practice.levels.admin")) {
                    player.sendMessage(CC.translate("&cSem permissão."))
                    return
                }
                OnlinePlayersLevelMenu().openMenu(player)
            }
            "DAILY_MISSION" -> MissionManager.sendMissionBookMessage(player)
        }
    }

    private fun leaveParty(player: Player, profile: aki.saki.practice.profile.Profile) {
        val partyUuid = profile.party ?: return
        val party = PartyManager.getByUUID(partyUuid) ?: return
        if (party.leader == profile.uuid) {
            party.players.forEach {
                val memberProfile = PracticePlugin.instance.profileManager.findById(it)!!
                memberProfile.party = null
                memberProfile.player.sendMessage(Locale.DISBANDED_PARTY.getMessage())
                Hotbar.giveHotbar(memberProfile)
            }
            PartyManager.parties.remove(party)
        } else {
            party.players.remove(profile.uuid)
            profile.party = null
            Hotbar.giveHotbar(profile)
            party.sendMessage(Locale.LEFT_PARTY.getMessage())
        }
    }
}
