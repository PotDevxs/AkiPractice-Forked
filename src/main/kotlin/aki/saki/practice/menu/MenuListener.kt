package aki.saki.practice.menu

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent

class MenuListener : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInventoryClick(event: InventoryClickEvent) {
        val session = MenuManager.getSession(event.view.topInventory) ?: return
        val player = event.whoClicked as? Player ?: return
        val topSize = event.view.topInventory.size

        if (event.rawSlot >= topSize) {
            if (event.isShiftClick) {
                event.isCancelled = true
            }
            return
        }

        event.isCancelled = true

        val button = session.buttons[event.rawSlot] ?: return
        button.clicked(player, event.rawSlot, event.click, event.hotbarButton)

        val currentSession = MenuManager.getCurrentSession(player.uniqueId)
        if (currentSession !== session || player.openInventory.topInventory != session.inventory) {
            return
        }

        if (session.menu.isUpdateAfterClick() || button.shouldUpdate(player, event.rawSlot, event.click)) {
            MenuManager.refresh(player)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInventoryDrag(event: InventoryDragEvent) {
        val session = MenuManager.getSession(event.view.topInventory) ?: return
        val topSize = event.view.topInventory.size
        val affectsTop = event.rawSlots.any { it < topSize }
        if (affectsTop && session.inventory == event.view.topInventory) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return
        MenuManager.handleClose(player, event.inventory)
    }
}
