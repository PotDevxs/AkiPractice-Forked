/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.ui.queue

import rip.katz.api.menu.Button
import aki.saki.practice.manager.QueueManager
import aki.saki.practice.PracticePlugin
import aki.saki.practice.profile.ProfileState
import aki.saki.practice.profile.hotbar.Hotbar
import aki.saki.practice.queue.Queue
import aki.saki.practice.utils.CC
import aki.saki.practice.utils.ItemBuilder
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class QueueButton(private val queue: Queue) : Button() {

    override fun getButtonItem(player: Player?): ItemStack {
        val playing = QueueManager.getPlayingCount(queue.kit, queue.type)

        return ItemBuilder(queue.kit.displayItem.clone())
            .amount(if (playing <= 0) 1 else playing)
            .name(queue.kit.displayName)
            .addFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS)
            .lore(
                listOf(
                    "${CC.PRIMARY}Playing: ${CC.SECONDARY}$playing",
                    "${CC.PRIMARY}Queuing: ${CC.GREEN}${queue.getPlayerCount()}",
                    "",
                    "${CC.PRIMARY}Click to play!"
                )
            ).build()
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType?, hotbarButton: Int) {
        if (clickType?.isLeftClick == true) {
            val profile = PracticePlugin.instance.profileManager.findById(player.uniqueId)!!

            if (profile.state == ProfileState.QUEUE) {
                player.sendMessage("${CC.RED}You are already in a queue!")
                return
            }

            QueueManager.addToQueue(player, queue.kit, queue.type)

            player.sendMessage(" ")
            player.sendMessage("${CC.PRIMARY}${CC.BOLD}${queue.type.name}")
            player.sendMessage("${CC.PRIMARY} ⚫ Ping Range: ${CC.SECONDARY}[${if (profile.settings.pingRestriction == 0) "Unrestricted" else profile.settings.pingRestriction}]")
            player.sendMessage("${CC.GRAY}${CC.ITALIC} Searching for match...")
            player.sendMessage(" ")

            Hotbar.giveHotbar(profile)
            player.closeInventory()
        }
    }
}
