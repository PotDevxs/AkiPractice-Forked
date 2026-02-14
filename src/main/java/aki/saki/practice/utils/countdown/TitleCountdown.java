/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.utils.countdown;

import lombok.Getter;
import aki.saki.practice.utils.CC;
import aki.saki.practice.utils.title.TitleBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;

@Getter
public class TitleCountdown extends BukkitRunnable implements ICountdown {

    private final Player player;
    private final String message, title, subtitle;
    private final Consumer<Boolean> consumer;
    private int seconds;

    public TitleCountdown(Player player, String message, String title, String subtitle, int seconds, Consumer<Boolean> consumer) {
        this.player = player;
        this.message = message;
        this.title = title;
        this.subtitle = subtitle;
        this.seconds = seconds;
        this.consumer = consumer;

        Plugin plugin = Bukkit.getPluginManager().getPlugin("AkiPractice");
        if (plugin != null && plugin.isEnabled()) {
            this.runTaskTimer(plugin, 0L, 20L);
        }
    }

    public void run() {
        --this.seconds;
        if (seconds != 0) {
            if (player == null) {
                cancel();
                return;
            }

            player.sendMessage(CC.translate(message.replace("<seconds>", seconds + "")));

            TitleBar.sendTitleBar(
                    player,
                    CC.translate(title.replace("<seconds>", seconds + "")),
                    subtitle == null ? null : CC.translate(subtitle.replace("<seconds>", seconds + "")), 0, 20, 0);


            /*TitleBar titleBar = new TitleBar(CC.translate(title.replace("<seconds>", seconds + "")), false);
            titleBar.sendPacket(player);

            if (subtitle != null && !subtitle.isEmpty()) {
                TitleBar subtitleBar = new TitleBar(CC.translate(subtitle.replace("<seconds>", seconds + "")), true);
                subtitleBar.sendPacket(player);
            } */
        }

        if (this.seconds <= 0) {
            this.consumer.accept(true);
            this.cancel();
        }
    }
}
