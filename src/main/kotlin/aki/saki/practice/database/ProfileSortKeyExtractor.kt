package aki.saki.practice.database

import aki.saki.practice.PracticePlugin
import aki.saki.practice.profile.statistics.KitStatistic
import aki.saki.practice.profile.statistics.global.GlobalStatistics
import org.bson.Document

/**
 * Extrai valor numérico de um [Document] de perfil para ordenação no modo flat-file
 * (espelha campos usados nos leaderboards / sorts do Mongo).
 */
object ProfileSortKeyExtractor {

    fun asDouble(document: Document, path: String): Double {
        return when {
            path == "xp" -> (document["xp"] as? Number)?.toDouble() ?: 0.0

            path.startsWith("kitStatistics.") -> {
                val without = path.removePrefix("kitStatistics.")
                val dot = without.indexOf('.')
                val kitName = if (dot >= 0) without.substring(0, dot) else without
                val field = if (dot >= 0) without.substring(dot + 1) else "elo"
                kitStatisticField(document, kitName, field)
            }

            path.startsWith("globalStatistics.") -> {
                val field = path.removePrefix("globalStatistics.")
                val gs = parseGlobalStatistics(document) ?: return 0.0
                globalField(gs, field)
            }

            path.startsWith("globalStatistic.") -> {
                val field = path.removePrefix("globalStatistic.")
                val gs = parseGlobalStatistics(document) ?: return 0.0
                globalField(gs, field)
            }

            else -> 0.0
        }
    }

    private fun parseGlobalStatistics(document: Document): GlobalStatistics? {
        return when (val g = document["globalStatistics"]) {
            is String -> try {
                PracticePlugin.GSON.fromJson(g, GlobalStatistics::class.java)
            } catch (_: Exception) {
                null
            }
            is Document -> try {
                PracticePlugin.GSON.fromJson(g.toJson(), GlobalStatistics::class.java)
            } catch (_: Exception) {
                null
            }
            else -> null
        }
    }

    private fun globalField(gs: GlobalStatistics, field: String): Double {
        return when (field) {
            "elo" -> gs.elo.toDouble()
            "dailyWinStreak" -> gs.dailyWinStreak.toDouble()
            "wins" -> gs.wins.toDouble()
            "losses" -> gs.losses.toDouble()
            "bestWinStreak" -> gs.bestWinStreak.toDouble()
            "currentWinStreak" -> gs.currentWinStreak.toDouble()
            else -> 0.0
        }
    }

    private fun kitStatisticField(document: Document, kitName: String, field: String): Double {
        val list = document.getList("kitStatistics", String::class.java) ?: return 0.0
        for (json in list) {
            val ks = try {
                PracticePlugin.GSON.fromJson(json, KitStatistic::class.java)
            } catch (_: Exception) {
                continue
            }
            if (!ks.kit.equals(kitName, ignoreCase = true)) continue
            return when (field) {
                "elo" -> ks.elo.toDouble()
                "peakELO" -> ks.peakELO.toDouble()
                "wins" -> ks.wins.toDouble()
                "losses" -> ks.losses.toDouble()
                "rankedWins" -> ks.rankedWins.toDouble()
                "rankedLosses" -> ks.rankedLosses.toDouble()
                "rankedBestStreak" -> ks.rankedBestStreak.toDouble()
                "bestCasualStreak" -> ks.bestCasualStreak.toDouble()
                "bestStreak" -> ks.bestStreak.toDouble()
                "currentStreak" -> ks.currentStreak.toDouble()
                else -> 0.0
            }
        }
        return 0.0
    }
}
