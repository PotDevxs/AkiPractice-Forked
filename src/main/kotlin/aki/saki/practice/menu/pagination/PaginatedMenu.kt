package aki.saki.practice.menu.pagination

import aki.saki.practice.menu.Button
import aki.saki.practice.menu.Menu
import aki.saki.practice.menu.MenuManager
import aki.saki.practice.utils.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

abstract class PaginatedMenu : Menu() {

    private val pageByPlayer = ConcurrentHashMap<UUID, Int>()

    open fun getPrePaginatedTitle(player: Player): String {
        return super.getTitle(player)
    }

    open fun getAllPagesButtons(player: Player): MutableMap<Int, Button> {
        return linkedMapOf()
    }

    open fun getGlobalButtons(player: Player): MutableMap<Int, Button> {
        return mutableMapOf()
    }

    override fun getTitle(player: Player): String {
        return getPrePaginatedTitle(player)
    }

    override fun getSize(): Int {
        return 36
    }

    override fun getButtons(player: Player): MutableMap<Int, Button> {
        val buttons = mutableMapOf<Int, Button>()
        val pageSlots = getPageSlots(getSize())
        val allPageButtons = getAllPagesButtons(player).toSortedMap()
        val orderedButtons = allPageButtons.values.toList()

        val maxItemsPerPage = maxOf(1, pageSlots.size)
        val totalPages = maxOf(1, Math.ceil(orderedButtons.size / maxItemsPerPage.toDouble()).toInt())
        val currentPage = pageByPlayer[player.uniqueId]
            ?.coerceAtLeast(1)
            ?.coerceAtMost(totalPages)
            ?: 1

        pageByPlayer[player.uniqueId] = currentPage

        val startIndex = (currentPage - 1) * maxItemsPerPage
        for (slotIndex in 0 until maxItemsPerPage) {
            val buttonIndex = startIndex + slotIndex
            if (buttonIndex >= orderedButtons.size) {
                break
            }

            buttons[pageSlots[slotIndex]] = orderedButtons[buttonIndex]
        }

        buttons.putAll(getGlobalButtons(player))

        if (totalPages > 1) {
            buttons[getSize() - 6] = PageChangeButton(this, false)
            buttons[getSize() - 5] = PageInfoButton(currentPage, totalPages)
            buttons[getSize() - 4] = PageChangeButton(this, true)
        }

        return buttons
    }

    override fun onClose(player: Player) {
        pageByPlayer.remove(player.uniqueId)
    }

    protected fun getPage(player: Player): Int {
        return pageByPlayer[player.uniqueId] ?: 1
    }

    protected fun setPage(player: Player, page: Int) {
        pageByPlayer[player.uniqueId] = maxOf(1, page)
    }

    fun clearPage(player: Player) {
        pageByPlayer.remove(player.uniqueId)
    }

    private fun getPageSlots(size: Int): List<Int> {
        if (size <= 9) {
            return (0 until size).toList()
        }

        return (0 until (size - 9)).toList()
    }

    private class PageInfoButton(
        private val currentPage: Int,
        private val totalPages: Int
    ) : Button() {
        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder(Material.PAPER)
                .name("&bPágina $currentPage/$totalPages")
                .build()
        }
    }

    private class PageChangeButton(
        private val menu: PaginatedMenu,
        private val next: Boolean
    ) : Button() {
        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder(Material.ARROW)
                .name(if (next) "&aPróxima página" else "&cPágina anterior")
                .build()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, hotbarButton: Int) {
            val currentPage = menu.getPage(player)
            menu.setPage(player, if (next) currentPage + 1 else maxOf(1, currentPage - 1))
            MenuManager.refresh(player)
        }
    }
}
