/*
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.command.admin

import aki.saki.practice.manager.ReportManager
import aki.saki.practice.utils.CC
import com.jonahseguin.drink.annotation.Command
import com.jonahseguin.drink.annotation.Require
import com.jonahseguin.drink.annotation.Sender
import org.bukkit.command.CommandSender
import java.text.SimpleDateFormat
import java.util.*

class ReportsCommand {

    private val dateFormat = SimpleDateFormat("dd/MM HH:mm")

    @Command(name = "", desc = "List all reports")
    @Require("practice.command.reports")
    fun list(@Sender sender: CommandSender) {
        val list = ReportManager.getReports()
        sender.sendMessage(CC.translate("&7&m--------&r &eReports &7(&f${list.size}&7) &7&m--------"))
        if (list.isEmpty()) {
            sender.sendMessage(CC.translate("&7Nenhum report."))
            return
        }
        list.take(20).forEach { r ->
            val time = dateFormat.format(Date(r.time))
            sender.sendMessage(CC.translate("&7[&f${r.id.toString().take(8)}&7] &c${r.reportedName} &7reportado por &e${r.reporterName} &7- &f${r.reason} &8($time)"))
        }
        if (list.size > 20) sender.sendMessage(CC.translate("&7... e mais ${list.size - 20}. Use &f/reports clear <id>&7 para remover."))
    }

    @Command(name = "clear", desc = "Clear a report by ID or all")
    @Require("practice.command.reports")
    fun clear(@Sender sender: CommandSender, idOrAll: String = "") {
        if (idOrAll.equals("all", ignoreCase = true)) {
            ReportManager.clearAll()
            sender.sendMessage(CC.translate("&aTodos os reports foram removidos."))
            return
        }
        if (idOrAll.isBlank()) {
            sender.sendMessage(CC.translate("&cUse: /reports clear <id> ou /reports clear all"))
            return
        }
        val id = try { UUID.fromString(idOrAll) } catch (_: Exception) {
            val match = ReportManager.getReports().firstOrNull { it.id.toString().startsWith(idOrAll) }
            if (match != null) {
                ReportManager.remove(match.id)
                sender.sendMessage(CC.translate("&aReport removido."))
                return
            }
            sender.sendMessage(CC.translate("&cID inválido."))
            return
        }
        if (ReportManager.remove(id)) sender.sendMessage(CC.translate("&aReport removido."))
        else sender.sendMessage(CC.translate("&cReport não encontrado."))
    }
}
