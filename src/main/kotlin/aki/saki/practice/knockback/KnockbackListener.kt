package aki.saki.practice.knockback

import aki.saki.practice.nms.NmsBridge
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerToggleSprintEvent
import org.bukkit.event.player.PlayerVelocityEvent

object KnockbackListener : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onVelocity(event: PlayerVelocityEvent) {
        val player = event.player
        val pendingHit = KnockbackService.consumePendingHit(player.uniqueId) ?: return
        val computedVelocity = KnockbackEngine.compute(pendingHit, event.velocity)

        if (!KnockbackService.packetModeEnabled) {
            event.velocity = computedVelocity
            player.fallDistance = 0.0f
            return
        }

        NmsBridge.ensureLoaded()
        val handle = NmsBridge.getHandle(player)
        NmsBridge.setEntityMot(handle, computedVelocity.x, computedVelocity.y, computedVelocity.z)
        NmsBridge.sendPacketEntityVelocity(handle)
        NmsBridge.setVelocityChanged(handle, false)

        if (KnockbackService.restoreServerMotion) {
            NmsBridge.setEntityMot(
                handle,
                pendingHit.victimVelocity.x,
                pendingHit.victimVelocity.y,
                pendingHit.victimVelocity.z
            )
        }

        event.isCancelled = true
        player.fallDistance = 0.0f
    }

    @EventHandler
    fun onSprintToggle(event: PlayerToggleSprintEvent) {
        KnockbackService.setServerSprinting(event.player.uniqueId, event.isSprinting)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        KnockbackService.setServerSprinting(event.player.uniqueId, event.player.isSprinting)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        KnockbackService.clear(event.player.uniqueId)
    }
}
