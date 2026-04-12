/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.match.ffa

import aki.saki.practice.Locale
import aki.saki.practice.PracticePlugin
import aki.saki.practice.constants.Constants
import aki.saki.practice.kit.Kit
import aki.saki.practice.profile.ProfileState
import aki.saki.practice.profile.hotbar.Hotbar
import aki.saki.practice.utils.CC
import aki.saki.practice.utils.PlayerUtil
import aki.saki.practice.nms.NmsBridge
import aki.saki.practice.utils.wrapper.WrapperPlayServerSpawnEntity
import org.bukkit.Bukkit
import org.bukkit.entity.Item
import org.bukkit.event.player.PlayerDropItemEvent
import java.util.*

/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/26/2022
 * Project: lPractice
 */

class FFA(val kit: Kit) {

    val uuid: UUID = UUID.randomUUID()

    val players: MutableList<FFAPlayer> = mutableListOf()
    val droppedItems: MutableList<Item> = mutableListOf()

    fun handleDeath(ffaPlayer: FFAPlayer, killer: FFAPlayer?) {
        ffaPlayer.death++
        ffaPlayer.killStreak = 0

        if (killer != null) {
            killer.kills++
            killer.killStreak++

            sendMessage(Locale.PLAYED_KILLED.getMessage().replace("<player>", ffaPlayer.name).replace("<killer>", killer.name))
        }else {
            sendMessage(Locale.PLAYER_DIED.getMessage().replace("<player>", ffaPlayer.name))
        }

        setup(ffaPlayer)
    }

    fun firstSetup(ffaPlayer: FFAPlayer) {
        if (ffaPlayer.player == null) return

        for (item in droppedItems) {

            val packet = WrapperPlayServerSpawnEntity()

            packet.entityID = item.entityId
            packet.x = item.location.x
            packet.y = item.location.y
            packet.z = item.location.z
            packet.pitch = item.location.pitch
            packet.yaw = item.location.pitch
            packet.type = 2
            packet.objectData = 2

            packet.sendPacket(ffaPlayer.player)

            val nmsEntity = NmsBridge.getEntityHandle(item)
            val dataWatcher = NmsBridge.getDataWatcher(nmsEntity)

            val metadata = NmsBridge.newPacketEntityMetadata(item.entityId, dataWatcher, true)
            NmsBridge.sendPacket(ffaPlayer.player, metadata)
        }
    }

    fun setup(ffaPlayer: FFAPlayer) {
        val player = ffaPlayer.player
        val profile = PracticePlugin.instance.profileManager.findById(player.uniqueId)!!

        if (Constants.FFA_SPAWN != null) {
            player.teleport(Constants.FFA_SPAWN)
        }

        PlayerUtil.reset(player)

        profile?.getKitStatistic(kit.name)?.generateBooks(player)

        players.stream().map { it.player }
            .forEach {
                player.showPlayer(it)
                it.showPlayer(player)
            }
    }

    fun handleLeave(ffaPlayer: FFAPlayer, offline: Boolean) {
        players.remove(ffaPlayer)

        if (!offline) {
            val player = ffaPlayer.player
            val profile = PracticePlugin.instance.profileManager.findById(ffaPlayer.uuid)
            for (item in droppedItems) {
                NmsBridge.sendPacket(player, NmsBridge.newPacketEntityDestroy(item.entityId))
            }

            PlayerUtil.reset(player)

            profile!!.state = ProfileState.LOBBY
            profile.ffa = null

            if (profile.enderPearlCooldown != null) {
                profile.enderPearlCooldown!!.cancel()
                profile.enderPearlCooldown = null
            }

            if (Constants.SPAWN != null) {
                player.teleport(Constants.SPAWN)
            }

            players.filter { it.player != null }.map { it.player }
                .forEach {
                    player.hidePlayer(it)
                    it.hidePlayer(player)
                }

            Hotbar.giveHotbar(profile)
        }
    }

    fun handleDrop(event: PlayerDropItemEvent) {
        val item = event.itemDrop

        droppedItems.add(item)

        Bukkit.getScheduler().runTaskLater(PracticePlugin.instance, {
            for (player in Bukkit.getOnlinePlayers()) {
                if (inFFA(player.uniqueId)) continue

                NmsBridge.sendPacket(player, NmsBridge.newPacketEntityDestroy(item.entityId))
            }
        }, 1L)
    }

    fun getFFAPlayer(uuid: UUID): FFAPlayer {
        return players.first { it.uuid == uuid }
    }

    fun inFFA(uuid: UUID): Boolean {
        return players.any { it.uuid == uuid }
    }

    fun sendMessage(message: String) {
        players.stream().map { it.player }
            .forEach { it.sendMessage(CC.translate(message)) }
    }
}
