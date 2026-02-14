/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.kit.editor.listener

import aki.saki.practice.event.player.EventPlayerState
import aki.saki.practice.kit.editor.KitManagementMenu
import aki.saki.practice.manager.EventManager
import aki.saki.practice.PracticePlugin
import aki.saki.practice.profile.ProfileState
import aki.saki.practice.utils.CC
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.inventory.CraftingInventory


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/21/2022
 * Project: lPractice
 */

object KitEditorListener: Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onAsyncPlayerChatEvent(event: AsyncPlayerChatEvent) {
        val profile = PracticePlugin.instance.profileManager.findById(event.player.uniqueId)!!
        if (profile.kitEditorData?.isRenaming()!!) {
            event.isCancelled = true
            if (event.message.length > 16) {
                event.player.sendMessage(CC.RED + "The kit name must be under 16 characters!")
                return
            }
            val previousName = profile.kitEditorData?.selectedKit?.name
            val newName = CC.translate(event.message)
            event.player.sendMessage("${CC.PRIMARY}Successfully changed kit loadout name from ${CC.SECONDARY}${previousName}${CC.PRIMARY} to ${CC.SECONDARY}${newName}")
            val selectedKit = profile.kitEditorData?.kit
            profile.kitEditorData?.kit = null
            profile.kitEditorData?.selectedKit?.name = newName
            profile.kitEditorData?.active = false
            profile.kitEditorData?.rename = false
            if (profile.state != ProfileState.MATCH) {
                if (selectedKit != null) {
                    KitManagementMenu(selectedKit).openMenu(event.player)
                }
            }
        }
    }

    @EventHandler
    fun onInventoryClickEvent(event: InventoryClickEvent) {
        if (event.whoClicked is Player) {
            val player: Player = event.whoClicked as Player
            if (event.clickedInventory != null && event.clickedInventory is CraftingInventory) {
                if (player.gameMode != GameMode.CREATIVE) {
                    event.isCancelled = true
                    return
                }
            }
            val profile = PracticePlugin.instance.profileManager.findById(player.uniqueId)!!

            if (profile.state == ProfileState.EVENT) {
                val currentEvent = EventManager.event
                val eventPlayer = currentEvent?.getPlayer(player.uniqueId)

                if (eventPlayer?.state == EventPlayerState.FIGHTING) {
                    return
                }

                val clicked = event.clickedInventory

                if (clicked != null && clicked == player.inventory) {
                    event.isCancelled = true
                }

                return
            }

            if (profile.state != ProfileState.MATCH && profile.state != ProfileState.FFA && player.gameMode == GameMode.SURVIVAL) {
                val clicked = event.clickedInventory
                if (profile.kitEditorData?.active!!) {
                    if (clicked == player.openInventory.topInventory) {
                        if (event.cursor.type != Material.AIR &&
                            event.currentItem.type == Material.AIR ||
                            event.cursor.type != Material.AIR &&
                            event.currentItem.type != Material.AIR
                        ) {
                            event.isCancelled = true
                            event.cursor = null
                            player.updateInventory()
                        }
                    }
                } else {
                    if (clicked != null && clicked == player.inventory) {
                        event.isCancelled = true
                    }
                }
            }
        }
    }
}
