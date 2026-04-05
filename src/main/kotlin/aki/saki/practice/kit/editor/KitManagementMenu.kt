/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.kit.editor

import aki.saki.practice.PracticePlugin
import aki.saki.practice.kit.EditedKit
import aki.saki.practice.kit.Kit
import aki.saki.practice.utils.CC
import aki.saki.practice.utils.ItemBuilder
import lombok.AllArgsConstructor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import aki.saki.practice.menu.Button
import aki.saki.practice.menu.Menu
import aki.saki.practice.menu.buttons.BackButton


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/20/2022
 * Project: lPractice
 */

class KitManagementMenu(val kit: Kit): Menu() {

    private val PLACEHOLDER = Button.placeholder(Material.STAINED_GLASS_PANE, 7.toByte(), " ")

    init {
        isPlaceholder = true
        updateAfterClick = false
    }

    override fun getTitle(player: Player): String {
        return "Vendo kits de " + kit.name
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons: MutableMap<Int, Button> = mutableMapOf()

        val profile = PracticePlugin.instance.profileManager.findById(player.uniqueId)!!
        val kitLoadouts: MutableList<EditedKit?>? = profile.getKitStatistic(kit.name)?.editedKits

        var startPos = -1

        for (i in 0..3) {
            startPos += 2

            var kitLoadout: EditedKit? = null

            if (kitLoadouts!!.size > i) {
                kitLoadout = kitLoadouts[i]
            }

            buttons[startPos] = if (kitLoadout == null) CreateKitButton(i) else KitDisplayButton(kitLoadout)
            buttons[startPos + 18] = LoadKitButton(i)
            buttons[startPos + 27] = if (kitLoadout == null) PLACEHOLDER else RenameKitButton(kit, kitLoadout)
            buttons[startPos + 36] = if (kitLoadout == null) PLACEHOLDER else DeleteKitButton(kit, kitLoadout)
        }

        buttons[36] = BackButton(KitEditorSelectKitMenu())

        return buttons
    }

    override fun onClose(player: Player) {
        if (!isClosedByMenu) {
            val profile = PracticePlugin.instance.profileManager.findById(player.uniqueId)!!
            profile.kitEditorData?.kit = null

            profile.save(true)
        }
    }

    private class DeleteKitButton(private val kit: Kit?, private val kitLoadout: EditedKit) : Button() {
        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder(Material.STAINED_CLAY)
                .name("&cApagar")
                .durability(14)
                .lore(
                    listOf(
                        "&cClique aqui para apagar esse kit.",
                        "&cEssa opção &lNÃO &cpode ser",
                        "&cdesfeita de nenhuma forma."
                    )
                )
                .build()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, hotbarButton: Int) {
            val profile = PracticePlugin.instance.profileManager.findById(player.uniqueId)!!

            profile.getKitStatistic(kit!!.name)?.deleteKit(kitLoadout)
            KitManagementMenu(kit).openMenu(player)
        }
    }

    private class CreateKitButton(private val index: Int) : Button() {
        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder(Material.IRON_SWORD)
                .name("${CC.PRIMARY}Criar kit")
                .build()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, hotbarButton: Int) {
            val profile = PracticePlugin.instance.profileManager.findById(player.uniqueId)!!
            val kit = profile.kitEditorData?.kit

            if (kit == null) {
                player.closeInventory()
                return
            }

            val kitLoadout = EditedKit("Kit " + (index + 1))

            kitLoadout.armorContent = kit.armorContent
            kitLoadout.content = kit.content
            kitLoadout.editContents = kit.editorItems

            profile.getKitStatistic(kit.name)?.replaceKit(index, kitLoadout)
            profile.kitEditorData?.selectedKit = kitLoadout

            profile.save(true)

            KitEditorMenu(index).openMenu(player)
        }
    }

    private class RenameKitButton(private val kit: Kit, private val kitLoadout: EditedKit) : Button() {
        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder(Material.SIGN)
                .name("${CC.PRIMARY}Renomear")
                .lore(CC.translate("${CC.PRIMARY}Clique aqui para renomear esse kit."))
                .build()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, hotbarSlot: Int) {
            //currentlyOpenedMenus[player.uniqueId]?.isClosedByMenu = true
            player.updateInventory()
            player.closeInventory()
           // player.sendMessage(Locale.KIT_EDITOR_START_RENAMING.format(kitLoadout.getCustomName()))
            val profile = PracticePlugin.instance.profileManager.findById(player.uniqueId)!!
            profile.kitEditorData?.kit = kit
            profile.kitEditorData?.selectedKit = kitLoadout
            profile.kitEditorData?.active = true
            profile.kitEditorData?.rename = true
        }
    }

    private class LoadKitButton(private val index: Int) : Button() {

        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder(Material.BOOK)
                .name("${CC.PRIMARY}Carregar/Editar")
                .lore(CC.translate("${CC.PRIMARY}Clique aqui para editar esse kit."))
                .build()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, hotbarSlot: Int) {
            val profile = PracticePlugin.instance.profileManager.findById(player.uniqueId)!!

            if (profile.kitEditorData?.kit == null) {
                player.closeInventory()
                return
            }

            var kit = profile.kitEditorData?.kit?.name?.let { profile.getKitStatistic(it) }?.editedKits?.get(index)

            if (kit == null) {
                kit = EditedKit("Kit " + (index + 1))
            }

            if (kit.content == null || kit.armorContent == null) {
                kit.content = profile.kitEditorData?.kit?.content
                kit.armorContent = profile.kitEditorData?.kit?.armorContent
            }

            if (kit.editContents == null) {
                kit.editContents = profile.kitEditorData?.kit?.editorItems
            }

            profile.getKitStatistic(kit.name)?.replaceKit(index, kit)

            profile.kitEditorData?.selectedKit = kit

            profile.save(true)

            KitEditorMenu(index).openMenu(player)
        }
    }

    @AllArgsConstructor
    private class KitDisplayButton(private val kitLoadout: EditedKit) : Button() {
        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder(Material.BOOK)
                .name("${CC.PRIMARY}${kitLoadout.name}")
                .build()
        }
    }
}
