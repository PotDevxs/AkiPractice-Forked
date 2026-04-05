package aki.saki.practice.menu

import aki.saki.practice.PracticePlugin
import aki.saki.practice.menu.pagination.PaginatedMenu
import aki.saki.practice.utils.CC
import aki.saki.practice.utils.ItemBuilder
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object MenuManager {

    private val defaultPlaceholder: ItemStack = ItemBuilder(Material.STAINED_GLASS_PANE)
        .durability(15)
        .name(" ")
        .build()

    private val sessionsByInventory = ConcurrentHashMap<Inventory, MenuSession>()
    private val sessionsByPlayer = ConcurrentHashMap<UUID, MenuSession>()

    fun openMenu(player: Player?, menu: Menu?): Inventory? {
        if (player == null || menu == null) {
            return null
        }

        sessionsByPlayer[player.uniqueId]?.let { previous ->
            previous.menu.isClosedByMenu = true
            stopAutoUpdate(previous)
        }

        val buttons = menu.getButtons(player).toMutableMap()
        val size = normalizeSize(menu.size(buttons))
        val inventory = Bukkit.createInventory(player, size, colorTitle(menu.getTitle(player)))

        val session = MenuSession(
            playerId = player.uniqueId,
            menu = menu,
            inventory = inventory,
            buttons = buttons
        )

        sessionsByInventory[inventory] = session
        sessionsByPlayer[player.uniqueId] = session

        render(session, player)
        player.openInventory(inventory)
        menu.onOpen(player)
        startAutoUpdate(session)
        return inventory
    }

    fun refresh(player: Player?) {
        if (player == null) {
            return
        }

        val session = sessionsByPlayer[player.uniqueId] ?: return
        refresh(session, player)
    }

    fun getSession(inventory: Inventory): MenuSession? {
        return sessionsByInventory[inventory]
    }

    fun getCurrentSession(playerId: UUID): MenuSession? {
        return sessionsByPlayer[playerId]
    }

    fun handleClose(player: Player, inventory: Inventory) {
        val session = sessionsByInventory.remove(inventory) ?: return

        if (sessionsByPlayer[player.uniqueId] === session) {
            sessionsByPlayer.remove(player.uniqueId)
        }

        stopAutoUpdate(session)
        if (session.menu is PaginatedMenu) {
            session.menu.clearPage(player)
        }
        session.menu.onClose(player)
        session.menu.isClosedByMenu = false
    }

    private fun refresh(session: MenuSession, player: Player) {
        val buttons = session.menu.getButtons(player).toMutableMap()
        val expectedSize = normalizeSize(session.menu.size(buttons))
        val expectedTitle = colorTitle(session.menu.getTitle(player))

        if (session.inventory.size != expectedSize || session.inventory.title != expectedTitle) {
            openMenu(player, session.menu)
            return
        }

        session.buttons = buttons
        render(session, player)
        player.updateInventory()
    }

    private fun render(session: MenuSession, player: Player) {
        val inventory = session.inventory
        inventory.clear()

        if (session.menu.isPlaceholder) {
            for (slot in 0 until inventory.size) {
                inventory.setItem(slot, defaultPlaceholder.clone())
            }
        }

        session.buttons.forEach { (slot, button) ->
            if (slot in 0 until inventory.size) {
                inventory.setItem(slot, button.getButtonItem(player))
            }
        }
    }

    private fun startAutoUpdate(session: MenuSession) {
        if (!session.menu.isAutoUpdate) {
            return
        }

        session.autoUpdateTask = Bukkit.getScheduler().runTaskTimer(
            PracticePlugin.instance,
            Runnable {
                val player = Bukkit.getPlayer(session.playerId)
                if (player == null) {
                    stopAutoUpdate(session)
                    return@Runnable
                }

                val current = sessionsByPlayer[session.playerId]
                if (current !== session || player.openInventory.topInventory != session.inventory) {
                    stopAutoUpdate(session)
                    return@Runnable
                }

                refresh(session, player)
            },
            session.menu.autoUpdateTicks,
            session.menu.autoUpdateTicks
        )
    }

    private fun stopAutoUpdate(session: MenuSession) {
        session.autoUpdateTask?.cancel()
        session.autoUpdateTask = null
    }

    private fun normalizeSize(size: Int): Int {
        var normalized = maxOf(9, size)
        if (normalized % 9 != 0) {
            normalized += 9 - (normalized % 9)
        }
        return minOf(54, normalized)
    }

    private fun colorTitle(title: String?): String {
        val colored = CC.translate(title ?: "Menu")
        return if (colored.length > 32) colored.substring(0, 32) else colored
    }

    data class MenuSession(
        val playerId: UUID,
        val menu: Menu,
        val inventory: Inventory,
        var buttons: MutableMap<Int, Button>,
        var autoUpdateTask: BukkitTask? = null
    )
}
