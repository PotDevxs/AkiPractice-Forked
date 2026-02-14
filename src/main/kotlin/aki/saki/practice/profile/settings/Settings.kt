/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.profile.settings


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 3/5/2022
 * Project: lPractice
 */

class Settings {

    var scoreboard = true
    var duels = true
    var spectators = true
    var mapRating = true
    var pingRestriction = 0

    /** Nome do ChatColor para cor primária do tema (ex: "AQUA"). null = usar padrão do servidor. */
    var themePrimary: String? = null
    /** Nome do ChatColor para cor secundária do tema (ex: "GRAY"). null = usar padrão do servidor. */
    var themeSecondary: String? = null
}
