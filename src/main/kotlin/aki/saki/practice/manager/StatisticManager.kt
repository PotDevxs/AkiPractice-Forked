/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.manager

import aki.saki.practice.PracticePlugin
import aki.saki.practice.kit.Kit
import aki.saki.practice.mission.DailyMissionType
import aki.saki.practice.mission.MissionManager
import aki.saki.practice.profile.Profile
import aki.saki.practice.utils.EloUtil

/**
 * This Project is property of Zowpy © 2023
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 6/7/2023
 * Project: lPractice
 */

object StatisticManager {

    fun win(profile: Profile, loserProfile: Profile, kit: Kit, ranked: Boolean) {
        val globalStatistics = profile.globalStatistic

        globalStatistics.apply {
            wins++
            currentWinStreak++
            if (currentWinStreak >= bestWinStreak) {
                bestWinStreak = currentWinStreak
            }
        }

        profile.getKitStatistic(kit.name)?.let { kitStatistic ->
            kitStatistic.wins++

            if (ranked) {

                kitStatistic.rankedStreak++
                if (kitStatistic.rankedStreak >= kitStatistic.rankedBestStreak) {
                    kitStatistic.rankedBestStreak = kitStatistic.rankedStreak
                }
                kitStatistic.rankedDailyStreak++
                kitStatistic.rankedWins++

                val loserKitStatistic = loserProfile.getKitStatistic(kit.name)

                val (newWinnerElo, newLoserElo) = loserKitStatistic?.let { loserStat ->
                    EloUtil.getNewRating(loserStat.elo, kitStatistic.elo, false) to
                            EloUtil.getNewRating(kitStatistic.elo, loserStat.elo, true)
                } ?: (kitStatistic.elo to kitStatistic.elo)

                kitStatistic.elo = newWinnerElo
                loserKitStatistic?.elo = newLoserElo

                if (kitStatistic.elo >= kitStatistic.peakELO) {
                    kitStatistic.peakELO = kitStatistic.elo
                }

                loserProfile.save()
                aki.saki.practice.manager.CampManager.getByPlayer(profile.uuid)?.let { camp ->
                    camp.totalWins++
                    aki.saki.practice.manager.CampManager.saveCamp(camp)
                }
            }

            kitStatistic.apply {
                currentStreak++
                currentDailyStreak++

                if (!ranked) {
                    currentCasualStreak++

                    if (currentCasualStreak >= bestCasualStreak) {
                        bestCasualStreak = currentCasualStreak
                    }
                }

                if (currentStreak >= bestStreak) {
                    bestStreak = currentStreak
                }

                if (currentDailyStreak >= bestDailyStreak) {
                    bestDailyStreak = currentDailyStreak
                }
            }

            // Actualizar el globalElo
            profile.updateGlobalElo()
        }

        val winXp = PracticePlugin.instance.settingsFile.getInt("XP.PER-WIN", 0).toLong()
        if (winXp > 0) profile.addXp(winXp)
        MissionManager.advance(profile, if (ranked) DailyMissionType.WIN_RANKED else DailyMissionType.WIN_CASUAL)
        profile.save(true)
    }

    fun loss(profile: Profile, kit: Kit, ranked: Boolean) {
        val globalStatistics = profile.globalStatistic

        globalStatistics.apply {
            losses++
            currentWinStreak = 0
            dailyWinStreak = 0
        }

        profile.getKitStatistic(kit.name)?.let { kitStatistic ->
            kitStatistic.apply {
                losses++
                currentStreak = 0
                currentDailyStreak = 0
                currentCasualStreak = 0
                bestCasualStreak = 0
                bestDailyStreak = 0

                if (ranked) {
                    rankedLosses++

                    rankedBestStreak = 0
                    rankedStreak = 0
                    rankedDailyStreak = 0
                }
            }

            // Actualizar el globalElo
            profile.updateGlobalElo()
        } ?: println("Loser profile is null for $kit")

        val lossXp = PracticePlugin.instance.settingsFile.getInt("XP.PER-LOSS", 0).toLong()
        if (lossXp > 0) profile.addXp(lossXp)
        profile.save(true)
    }
}
