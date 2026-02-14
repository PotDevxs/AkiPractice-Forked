/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.profile.editor

import aki.saki.practice.kit.EditedKit
import aki.saki.practice.kit.Kit


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/20/2022
 * Project: lPractice
 */

class KitEditorData {

    var kit: Kit? = null
    var selectedKit: EditedKit? = null
    var active = false
    var rename = false

    fun isRenaming(): Boolean {
        return active && rename && selectedKit != null
    }
}
