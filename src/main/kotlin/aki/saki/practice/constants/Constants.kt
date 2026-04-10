/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.constants

import aki.saki.practice.PracticePlugin
import aki.saki.practice.utils.Cuboid
import aki.saki.practice.utils.LocationUtil
import org.bukkit.Location

object Constants {

    var SPAWN: Location? = LocationUtil.deserialize(PracticePlugin.instance.settingsFile.getString("SPAWN"))
    var FFA_SPAWN: Location? = LocationUtil.deserialize(PracticePlugin.instance.ffaFile.getString("SPAWN"))
    var MIN: Location? = LocationUtil.deserialize(PracticePlugin.instance.ffaFile.getString("SAFE-ZONE.MIN"))
    var MAX: Location? = LocationUtil.deserialize(PracticePlugin.instance.ffaFile.getString("SAFE-ZONE.MAX"))
    var SAFE_ZONE: Cuboid? = null

    fun load() {
        if (MIN != null && MAX != null) {
            SAFE_ZONE = Cuboid(MIN!!, MAX!!)
        }
    }
}
