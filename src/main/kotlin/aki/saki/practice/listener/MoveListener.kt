/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.listener

import aki.saki.practice.constants.Constants
import aki.saki.practice.event.EventState
import aki.saki.practice.event.EventType
import aki.saki.practice.manager.EventManager
import aki.saki.practice.match.Match
import aki.saki.practice.match.MatchState
import aki.saki.practice.PracticePlugin
import aki.saki.practice.profile.Profile
import aki.saki.practice.profile.ProfileState
import aki.saki.practice.utils.PlayerUtil
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

object MoveListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onMove(event: PlayerMoveEvent) {
        if (event.from.blockX == event.to.blockX &&
            event.from.blockY == event.to.blockY &&
            event.from.blockZ == event.to.blockZ) return

        val player = event.player

        if (PlayerUtil.isMovementDenied(player.uniqueId)) {
            return
        }

        val profile = PracticePlugin.instance.profileManager.findById(player.uniqueId) ?: return

        when (profile.state) {
            ProfileState.LOBBY, ProfileState.QUEUE -> {
                if (event.to.y <= 2 && Constants.SPAWN != null) {
                    player.teleport(Constants.SPAWN)
                }
                return
            }
            ProfileState.MATCH -> handleMatchMove(event, profile)
            ProfileState.EVENT -> handleEventMove(event, profile)
            else -> {
                // Handle other states if necessary
            }
        }
    }

    private fun handleMatchMove(event: PlayerMoveEvent, profile: Profile) {
        val match = Match.getByUUID(profile.match!!) ?: return
        val player = event.player
        val matchPlayer = match.getMatchPlayer(player.uniqueId) ?: return

        if (event.to.y <= match.arena.deadzone) {

            if (matchPlayer.respawning) {
                player.teleport(match.arena.bounds.center)
                return
            }

            if (!matchPlayer.dead && match.matchState == MatchState.FIGHTING) {
                matchPlayer.lastDamager = null
                match.handleDeath(matchPlayer)
            } else {
                player.teleport(match.arena.bounds.center)
            }
        }

        if (event.to.block.type == Material.WATER || event.to.block.type == Material.STATIONARY_WATER) {
            if (!matchPlayer.dead && match.matchState == MatchState.FIGHTING && match.kit.sumo) {
                match.handleDeath(matchPlayer)
            }
        }
    }

    private fun handleEventMove(event: PlayerMoveEvent, profile: Profile) {
        val player = event.player
        val currentEvent = EventManager.event ?: return

        if (currentEvent.state != EventState.FIGHTING) return

        if (currentEvent.type == EventType.SUMO) {
            if (event.to.block.type == Material.WATER || event.to.block.type == Material.STATIONARY_WATER) {
                val eventPlayer = currentEvent.getPlayer(player.uniqueId) ?: return

                if (currentEvent.playingPlayers.none { it.uuid == player.uniqueId }) return

                eventPlayer.dead = true
                currentEvent.endRound(currentEvent.getOpponent(eventPlayer))
            }
        }
    }
}
