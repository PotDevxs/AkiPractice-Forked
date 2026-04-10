/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.profile.hotbar

import aki.saki.practice.Locale
import aki.saki.practice.PracticePlugin
import aki.saki.practice.event.EventState
import aki.saki.practice.kit.editor.KitEditorSelectKitMenu
import aki.saki.practice.manager.EventManager
import aki.saki.practice.manager.PartyManager
import aki.saki.practice.manager.QueueManager
import aki.saki.practice.match.Match
import aki.saki.practice.menus.HotbarContext
import aki.saki.practice.menus.HotbarYamlLoader
import aki.saki.practice.menus.MenuActionExecutor
import aki.saki.practice.party.Party
import aki.saki.practice.party.duel.procedure.PartyDuelProcedure
import aki.saki.practice.party.duel.procedure.menu.PartyDuelSelectPartyMenu
import aki.saki.practice.profile.Profile
import aki.saki.practice.profile.ProfileState
import aki.saki.practice.ui.leaderboards.LeaderboardRankedMenu
import aki.saki.practice.ui.party.PartyInformationMenu
import aki.saki.practice.ui.party.event.PartyStartEventMenu
import aki.saki.practice.ui.queue.ranked.RankedQueueMenu
import aki.saki.practice.ui.queue.unranked.UnrankedQueueMenu
import aki.saki.practice.utils.CC
import aki.saki.practice.utils.ItemBuilder
import aki.saki.practice.utils.item.CustomItemStack
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

object Hotbar {

    private data class SlotDef(
        val slot: Int,
        val material: Material,
        val name: String,
        val unbreakable: Boolean = false,
        val durability: Short = 0,
        val action: Consumer<PlayerInteractEvent>
    )

    fun giveHotbar(profile: Profile) {
        val player = Bukkit.getPlayer(profile.uuid) ?: return
        player.inventory.clear()

        when (profile.state) {
            ProfileState.LOBBY -> setupLobbyHotbar(player, profile)
            ProfileState.QUEUE -> applyYamlOrFallback(player, HotbarContext.QUEUE) { listOf(defaultQueueLeave()) }
            ProfileState.EVENT -> setupEventHotbar(player, profile)
            ProfileState.SPECTATING -> applyYamlOrFallback(player, HotbarContext.SPECTATE) { listOf(defaultSpectateLeave()) }
            else -> {}
        }
    }

    private fun setupLobbyHotbar(player: Player, profile: Profile) {
        if (profile.party != null) {
            applyYamlOrFallback(player, HotbarContext.PARTY) { defaultPartySlots(profile) }
        } else {
            applyYamlOrFallback(player, HotbarContext.LOBBY) { defaultLobbySlots(profile) }
        }
    }

    private fun setupEventHotbar(player: Player, profile: Profile) {
        val fromYaml = yamlSlots(HotbarContext.EVENT)
        val items = if (fromYaml.isNotEmpty()) fromYaml.toMutableList() else mutableListOf(defaultEventLeave())
        var addedConfiguredAdmin = false
        HotbarYamlLoader.getEventAdminExtra()?.let { extra ->
            if (extra.enabled && player.hasPermission(extra.permission)) {
                addedConfiguredAdmin = true
                items.add(
                    SlotDef(
                        extra.slot, extra.material, extra.name, false, extra.durability,
                        Consumer { ev ->
                            val p = ev.player as? Player ?: return@Consumer
                            MenuActionExecutor.execute(p, extra.actionType, extra.actionValue, 0, null)
                        }
                    )
                )
            }
        }
        val slotsUsed = items.map { it.slot }.toSet()
        if (!addedConfiguredAdmin && player.hasPermission("lpractice.command.event.forcestart")) {
            EventManager.event?.takeIf { it.state == EventState.ANNOUNCING }?.let {
                val slot = 0
                if (slot !in slotsUsed) {
                    items.add(
                        SlotDef(slot, Material.HOPPER, "&eForce Start", false, 0, Consumer { ev ->
                            (ev.player as? Player)?.chat("/event forcestart")
                        })
                    )
                }
            }
        }
        applySlots(player, items)
    }

    private fun yamlSlots(ctx: HotbarContext): List<SlotDef> {
        return HotbarYamlLoader.getEntries(ctx).map { e ->
            SlotDef(e.slot, e.material, e.name, e.unbreakable, e.durability, Consumer { ev ->
                val p = ev.player as? Player ?: return@Consumer
                MenuActionExecutor.execute(p, e.actionType, e.actionValue, 0, null)
            })
        }
    }

    private fun applyYamlOrFallback(player: Player, ctx: HotbarContext, fallback: () -> List<SlotDef>) {
        val yaml = yamlSlots(ctx)
        applySlots(player, if (yaml.isNotEmpty()) yaml else fallback())
    }

    private fun applySlots(player: Player, items: List<SlotDef>) {
        val bySlot = items.associateBy { it.slot }.values.sortedBy { it.slot }
        player.inventory.apply {
            bySlot.forEach { def ->
                setItem(def.slot, createCustomItem(player, def))
            }
        }
    }

    private fun createCustomItem(player: Player, def: SlotDef): ItemStack {
        val itemStack = ItemBuilder(def.material).durability(def.durability.toInt()).name(def.name).apply {
            if (def.unbreakable) {
                addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                setUnbreakable(true)
            }
        }.build()
        return CustomItemStack(player.uniqueId, itemStack).apply {
            rightClick = true
            clicked = def.action
            create()
        }.itemStack
    }

    private fun defaultQueueLeave(): SlotDef = SlotDef(8, Material.INK_SACK, "&cSair da fila", false, 1, Consumer { ev ->
        val p = ev.player as? Player ?: return@Consumer
        val prof = PracticePlugin.instance.profileManager.findById(p.uniqueId) ?: return@Consumer
        prof.state = ProfileState.LOBBY
        prof.queuePlayer = null
        QueueManager.getQueue(prof.uuid)?.queuePlayers?.removeIf { it.uuid == p.uniqueId }
        giveHotbar(prof)
    })

    private fun defaultSpectateLeave(): SlotDef = SlotDef(8, Material.INK_SACK, "&cSair do espectador", false, 1, Consumer { ev ->
        val p = ev.player as? Player ?: return@Consumer
        val prof = PracticePlugin.instance.profileManager.findById(p.uniqueId) ?: return@Consumer
        prof.state = ProfileState.LOBBY
        Match.getSpectator(p.uniqueId)?.removeSpectator(p)
        giveHotbar(prof)
    })

    private fun defaultEventLeave(): SlotDef = SlotDef(8, Material.INK_SACK, "&cSair do evento", false, 1, Consumer { ev ->
        val p = ev.player as? Player ?: return@Consumer
        EventManager.event?.removePlayer(p)
        EventManager.event?.let { e ->
            Bukkit.broadcastMessage("${CC.GREEN}${p.name}${CC.YELLOW} saiu do evento. ${CC.GRAY}(${e.players.size}/${e.requiredPlayers})")
        }
    })

    private fun defaultPartySlots(profile: Profile): List<SlotDef> = listOf(
        SlotDef(0, Material.NETHER_STAR, "&eInformações da party", false, 0, Consumer {
            PartyManager.getByUUID(profile.party!!)?.let { PartyInformationMenu(it).openMenu(Bukkit.getPlayer(profile.uuid)) }
        }),
        SlotDef(4, Material.GOLD_AXE, "&eEvento da party", true, 0, Consumer {
            PartyStartEventMenu().openMenu(Bukkit.getPlayer(profile.uuid))
        }),
        SlotDef(5, Material.DIAMOND_AXE, "&eDuelo de party", true, 0, Consumer {
            PartyDuelProcedure(profile.uuid).apply { PartyDuelProcedure.duelProcedures.add(this) }
            PartyDuelSelectPartyMenu().openMenu(Bukkit.getPlayer(profile.uuid))
        }),
        SlotDef(8, Material.INK_SACK, "&cSair da party", false, 1, Consumer {
            val party = PartyManager.getByUUID(profile.party!!)!!
            if (party.leader == profile.uuid) {
                party.players.forEach {
                    val memberProfile = PracticePlugin.instance.profileManager.findById(it)!!
                    memberProfile.party = null
                    memberProfile.player.sendMessage(Locale.DISBANDED_PARTY.getMessage())
                    giveHotbar(memberProfile)
                }
                PartyManager.parties.remove(party)
            } else {
                party.players.remove(profile.uuid)
                profile.party = null
                giveHotbar(profile)
                party.sendMessage(Locale.LEFT_PARTY.getMessage())
            }
        })
    )

    private fun defaultLobbySlots(profile: Profile): List<SlotDef> = listOf(
        SlotDef(0, Material.IRON_SWORD, "&aJogar Casual &7(Clique direito)", true, 0, Consumer {
            if (profile.state == ProfileState.LOBBY) UnrankedQueueMenu().openMenu(Bukkit.getPlayer(profile.uuid))
        }),
        SlotDef(1, Material.DIAMOND_SWORD, "&cJogar ranqueado &7(Clique direito)", true, 0, Consumer {
            if (profile.state == ProfileState.LOBBY) RankedQueueMenu().openMenu(Bukkit.getPlayer(profile.uuid))
        }),
        SlotDef(2, Material.BOOK, "&6Editar kit &7(Clique direito)", false, 0, Consumer {
            if (profile.state == ProfileState.LOBBY) KitEditorSelectKitMenu().openMenu(Bukkit.getPlayer(profile.uuid))
        }),
        SlotDef(4, Material.NETHER_STAR, "&dCriar party &7(Clique direito)", false, 0, Consumer {
            if (profile.state == ProfileState.LOBBY) {
                val party = Party(profile.uuid)
                party.players.add(profile.uuid)
                PartyManager.parties.add(party)
                profile.party = party.uuid
                giveHotbar(profile)
                Bukkit.getPlayer(profile.uuid)?.sendMessage(Locale.CREATED_PARTY.getMessage())
            }
        }),
        SlotDef(6, Material.ITEM_FRAME, "&eLeaderboards &7(Clique direito)", false, 0, Consumer {
            LeaderboardRankedMenu(PracticePlugin.instance).openMenu(profile.player)
        }),
        SlotDef(7, Material.EYE_OF_ENDER, "&bEventos &7(Clique direito)", false, 0, Consumer {
            if (profile.state == ProfileState.LOBBY) Bukkit.getPlayer(profile.uuid)?.chat("/event host")
        }),
        SlotDef(8, Material.REDSTONE_COMPARATOR, "&bConfigurações &7(Clique direito)", false, 0, Consumer {
            if (profile.state == ProfileState.LOBBY) Bukkit.getPlayer(profile.uuid)?.chat("/settings")
        })
    )
}
