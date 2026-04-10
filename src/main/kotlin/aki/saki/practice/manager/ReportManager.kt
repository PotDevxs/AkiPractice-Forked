/*
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.manager

import aki.saki.practice.PracticePlugin
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*

data class Report(
    val id: UUID,
    val reporter: UUID,
    val reported: UUID,
    val reporterName: String,
    val reportedName: String,
    val reason: String,
    val time: Long
)

object ReportManager {

    private val reports = mutableListOf<Report>()
    private val file: File get() = File(PracticePlugin.instance.dataFolder, "reports.yml")

    fun load() {
        reports.clear()
        if (!file.exists()) return
        val config = YamlConfiguration.loadConfiguration(file)
        config.getKeys(false).forEach { key ->
            val id = try { UUID.fromString(key) } catch (_: Exception) { return@forEach }
            val reporter = try { UUID.fromString(config.getString("$key.reporter") ?: "") } catch (_: Exception) { return@forEach }
            val reported = try { UUID.fromString(config.getString("$key.reported") ?: "") } catch (_: Exception) { return@forEach }
            reports.add(Report(
                id = id,
                reporter = reporter,
                reported = reported,
                reporterName = config.getString("$key.reporterName") ?: "",
                reportedName = config.getString("$key.reportedName") ?: "",
                reason = config.getString("$key.reason") ?: "",
                time = config.getLong("$key.time", 0L)
            ))
        }
    }

    fun save() {
        file.parentFile?.mkdirs()
        val config = YamlConfiguration()
        reports.forEach { r ->
            config.set("${r.id}.reporter", r.reporter.toString())
            config.set("${r.id}.reported", r.reported.toString())
            config.set("${r.id}.reporterName", r.reporterName)
            config.set("${r.id}.reportedName", r.reportedName)
            config.set("${r.id}.reason", r.reason)
            config.set("${r.id}.time", r.time)
        }
        config.save(file)
    }

    fun add(reporter: UUID, reported: UUID, reporterName: String, reportedName: String, reason: String) {
        reports.add(Report(UUID.randomUUID(), reporter, reported, reporterName, reportedName, reason, System.currentTimeMillis()))
        save()
    }

    fun remove(id: UUID): Boolean {
        val ok = reports.removeAll { it.id == id }
        if (ok) save()
        return ok
    }

    fun clearAll() {
        reports.clear()
        save()
    }

    fun getReports(): List<Report> = reports.toList()
    fun getReport(id: UUID): Report? = reports.firstOrNull { it.id == id }
}
