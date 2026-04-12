/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.utils.title;

import aki.saki.practice.nms.NmsTitles;
import org.bukkit.entity.Player;

public class TitleBar {

    public static void sendTitleBar(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        NmsTitles.sendTitleBar(player, title, subtitle, fadeIn, stay, fadeOut);
    }
}
