package aki.saki.practice.menu.buttons

import aki.saki.practice.menu.Button
import aki.saki.practice.menu.Menu
import aki.saki.practice.utils.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

class BackButton(private val backMenu: Menu?) : Button() {

    override fun getButtonItem(player: Player): ItemStack {
        return ItemBuilder(Material.ARROW)
            .name("&bVoltar")
            .lore("&7Clique para voltar ao menu anterior.")
            .build()
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, hotbarButton: Int) {
        val menu = backMenu ?: return
        playNeutral(player)
        menu.openMenu(player)
    }
}
