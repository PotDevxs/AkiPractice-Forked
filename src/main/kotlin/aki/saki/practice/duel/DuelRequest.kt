/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.duel

import dev.ryu.core.bukkit.CoreAPI
import aki.saki.practice.Locale
import aki.saki.practice.arena.Arena
import aki.saki.practice.kit.Kit
import aki.saki.practice.PracticePlugin
import aki.saki.practice.utils.CC
import aki.saki.practice.utils.PlayerUtil
import aki.saki.practice.utils.TextBuilder
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import java.util.*


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/28/2022
 * Project: lPractice
 */

class DuelRequest(var uuid: UUID, var target: UUID, var kit: Kit, var arena: Arena) {

    var executedAt = System.currentTimeMillis()

    fun isExpired(): Boolean {
        return System.currentTimeMillis() - executedAt >= 60_000
    }

    fun send() {

        val player = Bukkit.getPlayer(target)
        val sender = Bukkit.getPlayer(uuid)

        val profile = PracticePlugin.instance.profileManager.findById(target)!!
        profile.duelRequests.add(this)

        val message = TextBuilder()             //.replace("<sender>", "${/*CoreAPI.grantSystem.findBestRank(CoreAPI.grantSystem.repository.findAllByPlayer(sender.uniqueId)).color*/}${sender.name}").replace("<kit>", kit.displayName ?: kit.name)))

            .setText(CC.translate(Locale.DUEL_REQUEST.getMessage()
                .replace("<ping>", "${PlayerUtil.getPing(sender)}")
                .replace("<sender>", "${ChatColor.valueOf(CoreAPI.grantSystem.findBestRank(CoreAPI.grantSystem.repository.findAllByPlayer(sender.uniqueId)).color)}${sender.name}").replace("<kit>", kit.displayName ?: kit.name)))
            .then()
            .setText(Locale.DUEL_REQUEST_FOOTER.getMessage().replace("<arena>", arena.name))
            .then()
            .setText(Locale.CLICK_TO_ACCEPT.getMessage())
            .setCommand("/duel accept ${sender.name}")
            .then()
            .build()

        player.spigot().sendMessage(message)
    }
}
