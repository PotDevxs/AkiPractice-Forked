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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;

@Getter
public class Countdown extends BukkitRunnable implements ICountdown {

    private final Player player;
    private final String message;
    private final Consumer<Boolean> consumer;
    private int seconds;

    public Countdown(Player player, String message, int seconds, Consumer<Boolean> consumer) {
        this.player = player;
        this.message = message;
        this.seconds = seconds;
        this.consumer = consumer;

        Plugin plugin = Bukkit.getPluginManager().getPlugin("AkiPractice");
        if (plugin != null && plugin.isEnabled()) {
            this.runTaskTimer(plugin, 0L, 20L);
        }
    }

    public void run() {
        --this.seconds;
        if (this.seconds != 0) {
            this.player.sendMessage(CC.translate(this.message.replace("<seconds>", this.seconds + "")));
        }

        if (this.seconds <= 0) {
            this.consumer.accept(true);
            this.cancel();
        }
    }
}
