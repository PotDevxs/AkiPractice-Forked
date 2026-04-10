/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.utils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;

@EqualsAndHashCode(callSuper = true)
@Data
public class Cooldown extends BukkitRunnable {

    private final long startedAt = System.currentTimeMillis();
    private int seconds;
    private Consumer<Boolean> consumer;

    public Cooldown(int seconds, Consumer<Boolean> consumer) {
        this.seconds = seconds;
        this.consumer = consumer;

        Plugin plugin = Bukkit.getPluginManager().getPlugin("AkiPractice");
        if (plugin != null && plugin.isEnabled()) {
            this.runTaskLater(plugin, seconds * 20L);
        }
    }

    public boolean hasExpired() {
        return startedAt + (seconds * 1000L) <= System.currentTimeMillis();
    }

    public long getTimeRemaining() {
        return this.startedAt + (long)this.seconds * 1000L - System.currentTimeMillis();
    }

    @Override
    public void run() {
        consumer.accept(true);
    }
}
