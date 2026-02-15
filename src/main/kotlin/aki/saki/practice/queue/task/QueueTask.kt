/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.queue.task

import aki.saki.practice.PracticePlugin
import aki.saki.practice.manager.ArenaManager
import aki.saki.practice.manager.MatchManager
import aki.saki.practice.manager.QueueManager
import aki.saki.practice.match.Match
import aki.saki.practice.profile.Profile
import aki.saki.practice.queue.Queue
import aki.saki.practice.queue.QueuePlayer
import aki.saki.practice.queue.QueueType
import aki.saki.practice.utils.CC
import aki.saki.practice.utils.PlayerUtil
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

object QueueTask : BukkitRunnable() {

    init {
        runTaskTimer(PracticePlugin.instance, 20L, 20L)
    }

    override fun run() {
        if (Bukkit.getOnlinePlayers().isEmpty()) return

        try {
            QueueManager.queues.values.forEach { queue ->
                processQueue(queue)
            }
        } catch (ignored: ConcurrentModificationException) {
        }
    }

    private fun processQueue(queue: Queue) {
        if (queue.getQueueingPlayers().isEmpty()) return

        queue.tickAllRanges()

        if (queue.getQueueingPlayers().size < 2) return

        val queuePlayers = queue.getQueueingPlayers().toMutableList()
            .sortedWith(compareByDescending<QueuePlayer> { it.priority }.thenBy { it.started })
        for (firstQueueProfile in queuePlayers) {
            val firstPlayer = Bukkit.getPlayer(firstQueueProfile.uuid) ?: continue

            for (secondQueueProfile in queuePlayers) {
                if (firstQueueProfile.uuid == secondQueueProfile.uuid) continue

                val secondPlayer = Bukkit.getPlayer(secondQueueProfile.uuid) ?: continue

                if (!isValidMatch(firstQueueProfile, secondQueueProfile, firstPlayer, secondPlayer, queue.type)) continue

                val arena = ArenaManager.getFreeArena(queue.kit)

                if (arena == null) {
                    for (i in 0..6) {
                        firstPlayer.sendMessage(CC.translate("&cNo arenas found"))
                        secondPlayer.sendMessage(CC.translate("&cNo arenas found"))
                    }
                    return
                }

                queue.removePlayer(firstQueueProfile)
                queue.removePlayer(secondQueueProfile)

                val profile = PracticePlugin.instance.profileManager.findById(firstPlayer.uniqueId)
                val profile1 = PracticePlugin.instance.profileManager.findById(secondPlayer.uniqueId)

                val match =
                    MatchManager.createMatch(queue.kit, arena, queue.type, false, firstPlayer, secondPlayer) ?: return

                println(match)

                addFollowersAsSpectators(profile, firstPlayer, match)
                addFollowersAsSpectators(profile1, secondPlayer, match, firstPlayer.uniqueId, secondPlayer.uniqueId)
            }
        }

        return
    }

    private fun isValidMatch(firstQueueProfile: QueuePlayer, secondQueueProfile: QueuePlayer, firstPlayer: Player, secondPlayer: Player, queueType: QueueType): Boolean {
        if (secondQueueProfile.pingFactor != 0 && PlayerUtil.getPing(firstPlayer) > secondQueueProfile.pingFactor) return false
        if (firstQueueProfile.pingFactor != 0 && PlayerUtil.getPing(secondPlayer) > firstQueueProfile.pingFactor) return false
        if (queueType == QueueType.RANKED && (!firstQueueProfile.isInRange(secondQueueProfile.elo) || !secondQueueProfile.isInRange(firstQueueProfile.elo))) return false
        if (queueType == QueueType.RANKED && PracticePlugin.instance.settingsFile.getBoolean("QUEUE.ANTI-BOOST-SAME-IP")) {
            val a = PlayerUtil.getAddress(firstPlayer)
            val b = PlayerUtil.getAddress(secondPlayer)
            if (a != null && b != null && a == b) return false
        }
        return true
    }

    private fun addFollowersAsSpectators(profile: Profile?, player: Player, match: Match, vararg excludeUUIDs: UUID) {
        profile?.followers?.filterNot { it in excludeUUIDs }?.forEach { uuid ->
            val followerProfile = PracticePlugin.instance.profileManager.findById(uuid)!!
            followerProfile.silent = true
            match.addSpectator(followerProfile.player)
            followerProfile.player.teleport(player.location)
        }
    }
}
