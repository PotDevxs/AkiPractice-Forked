package aki.saki.practice.knockback

import net.minecraft.server.v1_8_R3.PacketPlayOutEntityVelocity
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
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

        val handle = (player as CraftPlayer).handle
        handle.motX = computedVelocity.x
        handle.motY = computedVelocity.y
        handle.motZ = computedVelocity.z
        handle.playerConnection.sendPacket(PacketPlayOutEntityVelocity(handle))
        handle.velocityChanged = false

        if (KnockbackService.restoreServerMotion) {
            handle.motX = pendingHit.victimVelocity.x
            handle.motY = pendingHit.victimVelocity.y
            handle.motZ = pendingHit.victimVelocity.z
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
