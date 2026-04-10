/*
 * Stub para compilação sem katto.jar.
 * Redireciona para o HologramManager do AkiPractice.
 * Se você tiver o JAR original do Katto, coloque em libs/katto.jar
 * e remova este arquivo para usar a API real.
 */
package rip.katz.api

import org.bukkit.Location
import org.bukkit.Bukkit

object Katto {
    private val instance by lazy { KattoStubInstance() }
    fun get(): KattoStubInstance = instance
}

class KattoStubInstance {
    val hologramManager: KattoHologramManagerAdapter by lazy { KattoHologramManagerAdapter() }
}

class KattoHologramManagerAdapter {
    private val pluginManager: aki.saki.practice.holograms.HologramManager?
        get() = try {
            Bukkit.getPluginManager().getPlugin("AkiPractice")?.let { plugin ->
                (plugin as? aki.saki.practice.PracticePlugin)?.hologramManager
            }
        } catch (_: Throwable) { null }

    fun hologramDestroy(name: String) {
        pluginManager?.hologramDestroy(name)
    }

    fun hologramCreation(
        location: Location,
        updateTime: Int,
        name: String,
        lines: MutableList<String>,
        isUpdatable: Boolean,
        distance: Double,
        distanceCalcByListener: Boolean
    ) {
        val manager = pluginManager ?: return
        manager.hologramCreation(
            location,
            updateTime,
            updateTime,
            name,
            lines,
            isUpdatable,
            distance,
            distanceCalcByListener
        ) {}
    }
}
