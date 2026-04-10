/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.command.admin

import com.jonahseguin.drink.annotation.Command
import com.jonahseguin.drink.annotation.Require
import com.jonahseguin.drink.annotation.Sender
import aki.saki.practice.event.map.EventMap
import aki.saki.practice.event.map.impl.TNTRunMap
import aki.saki.practice.event.map.impl.TNTTagMap
import aki.saki.practice.event.map.type.EventMapType
import aki.saki.practice.manager.EventMapManager
import aki.saki.practice.utils.CC
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ lPractice
 * @author yek4h © 2024
 * Date: 17/06/2024
*/

class EventMapCommand {

    @Command(name = "", desc = "Manage event maps")
    @Require("practice.command.eventmap")
    fun help(@Sender sender: CommandSender) {
        sender.sendMessage("""
            ${CC.PRIMARY}Comandos de mapas de evento:
            ${CC.SECONDARY}/eventmap create <name>
            ${CC.SECONDARY}/eventmap delete <name>
            ${CC.SECONDARY}/eventmap spawn <name>
            ${CC.SECONDARY}/eventmap pos1 <name>
            ${CC.SECONDARY}/eventmap pos2 <name>
            ${CC.SECONDARY}/eventmap deadzone <name> <deadzone>
            ${CC.SECONDARY}/eventmap type <name> <type> - você pode escolher entre Sumo e Brackets
        """.trimIndent())
    }

    @Command(name = "create", desc = "Create an event map")
    @Require("practice.command.eventmap.create")
    fun create(@Sender sender: CommandSender, name: String) {
        if (EventMapManager.getByName(name) != null) {
            sender.sendMessage("${CC.RED}Esse mapa de evento já existe!")
            return
        }

        val arena = EventMap(name)
        arena.save()
        EventMapManager.maps.add(arena)
        sender.sendMessage("${CC.PRIMARY}Mapa de evento ${CC.SECONDARY}'$name'${CC.PRIMARY} criado com sucesso!")
    }

    @Command(name = "delete", desc = "Delete an event map")
    @Require("practice.command.eventmap.delete")
    fun delete(@Sender sender: CommandSender, arena: EventMap) {
        arena.delete()
        EventMapManager.maps.remove(arena)
        sender.sendMessage("${CC.PRIMARY}Mapa de evento ${CC.SECONDARY}'${arena.name}'${CC.PRIMARY} removido com sucesso!")
    }

    @Command(name = "spawn", desc = "Set the spawn location of an event map")
    @Require("practice.command.eventmap.spawn")
    fun setSpawn(@Sender sender: CommandSender, arena: EventMap) {
        val player = sender as? Player ?: return
        arena.spawn = player.location
        arena.save()
        sender.sendMessage("${CC.PRIMARY}Spawn do mapa ${CC.SECONDARY}${arena.name}${CC.PRIMARY} definido com sucesso!")
    }

    @Command(name = "deadzone", desc = "Set the deadzone of a TNT Run map")
    @Require("practice.command.eventmap.deadzone")
    fun setDeadzone(@Sender sender: CommandSender, arena: EventMap, deadzone: Int) {
        if (arena.type != EventMapType.TNT_RUN) {
            sender.sendMessage("${CC.RED}Essa opção não é suportada para esse tipo de mapa!")
            return
        }
        (arena as TNTRunMap).deadzone = deadzone
        arena.save()
        sender.sendMessage("${CC.PRIMARY}Deadzone do mapa ${CC.SECONDARY}${arena.name}${CC.PRIMARY} definida com sucesso!")
    }

    @Command(name = "pos1", desc = "Set the first position of an event map", aliases = ["position1", "l1", "location1"])
    @Require("practice.command.eventmap.pos1")
    fun setPos1(@Sender sender: CommandSender, arena: EventMap) {
        val player = sender as? Player ?: return
        if (arena.type in listOf(EventMapType.TNT_TAG, EventMapType.TNT_RUN)) {
            sender.sendMessage("${CC.RED}Essa opção não é suportada para esse tipo de mapa!")
            return
        }
        arena.l1 = player.location
        arena.save()
        sender.sendMessage("${CC.PRIMARY}Localização 1 do mapa ${CC.SECONDARY}${arena.name}${CC.PRIMARY} definida com sucesso!")
    }

    @Command(name = "pos2", desc = "Set the second position of an event map", aliases = ["position2", "l2", "location2"])
    @Require("practice.command.eventmap.pos2")
    fun setPos2(@Sender sender: CommandSender, arena: EventMap) {
        val player = sender as? Player ?: return
        if (arena.type in listOf(EventMapType.TNT_TAG, EventMapType.TNT_RUN)) {
            sender.sendMessage("${CC.RED}Essa opção não é suportada para esse tipo de mapa!")
            return
        }
        arena.l2 = player.location
        arena.save()
        sender.sendMessage("${CC.PRIMARY}Localização 2 do mapa ${CC.SECONDARY}${arena.name}${CC.PRIMARY} definida com sucesso!")
    }

    @Command(name = "type", desc = "Set the type of an event map")
    @Require("practice.command.eventmap.type")
    fun setType(@Sender sender: CommandSender, arena: EventMap, type: EventMapType) {
        arena.type = type
        when (type) {
            EventMapType.TNT_RUN -> {
                val newArena = TNTRunMap(arena.name).apply {
                    spawn = arena.spawn
                }
                EventMapManager.maps.replace(arena.name, newArena)
                newArena.save()
            }
            EventMapType.TNT_TAG -> {
                val newArena = TNTTagMap(arena.name).apply {
                    spawn = arena.spawn
                }
                EventMapManager.maps.replace(arena.name, newArena)
                newArena.save()
            }
            else -> arena.save()
        }
        sender.sendMessage("${CC.PRIMARY}Tipo do mapa ${CC.SECONDARY}${arena.name}${CC.PRIMARY} definido para ${CC.SECONDARY}${type.eventName}${CC.PRIMARY} com sucesso!")
    }
}

// Extension function to replace an item in a mutable list by name
private fun MutableList<EventMap>.replace(name: String, newArena: EventMap) {
    val index = indexOfFirst { it.name.equals(name, ignoreCase = true) }
    if (index != -1) {
        this[index] = newArena
    }
}
