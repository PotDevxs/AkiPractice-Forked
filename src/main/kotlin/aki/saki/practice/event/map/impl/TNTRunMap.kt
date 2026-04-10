/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.event.map.impl

import aki.saki.practice.PracticePlugin
import aki.saki.practice.event.map.EventMap
import aki.saki.practice.event.map.type.EventMapType
import aki.saki.practice.utils.LocationUtil


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 3/26/2022
 * Project: lPractice
 */

class TNTRunMap(name: String) : EventMap(name) {

    var deadzone = 2
    override var type = EventMapType.TNT_RUN

    override fun save() {
        val configFile = PracticePlugin.instance.eventsFile
        val section = configFile.createSection("maps.${name}")

        section.set("deadzone", deadzone)
        section.set("type", type.name)
        section.set("spawn", LocationUtil.serialize(spawn))

        configFile.save()
    }
}
