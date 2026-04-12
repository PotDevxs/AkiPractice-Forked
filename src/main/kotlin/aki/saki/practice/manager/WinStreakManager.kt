package aki.saki.practice.manager

import aki.saki.practice.PracticePlugin
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class WinStreakManager {

    var winstreakEnabled = true
    private val scheduler = Executors.newScheduledThreadPool(1)

    init {
        scheduler.scheduleAtFixedRate({
            if (winstreakEnabled) {
                updateAllWinStreaks()
            }
        }, 0, 30, TimeUnit.MINUTES)
    }

    fun updateAllWinStreaks() {
        PracticePlugin.instance.profileManager.profiles.values.forEach {
            it.updateWinStreak()
        }
    }

    fun toggleWinstreak() {
        winstreakEnabled = !winstreakEnabled

        if (winstreakEnabled) {
        }
    }

    fun isWinstreakEnabled(): Boolean {
        return winstreakEnabled
    }

    fun shutdown() {
        scheduler.shutdown()
    }
}
