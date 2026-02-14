/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.utils;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.spigotmc.AsyncCatcher;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.UUID;

public class PlayerUtil {

    private static void hologramHide(Player player) {
        try {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("AkiPractice");
            if (plugin != null) {
                Object hm = plugin.getClass().getMethod("getHologramManager").invoke(plugin);
                if (hm != null) {
                    hm.getClass().getMethod("hide", Player.class).invoke(hm, player);
                }
            }
        } catch (Exception ignored) { }
    }

    private static void hologramShow(Player player) {
        try {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("AkiPractice");
            if (plugin != null) {
                Object hm = plugin.getClass().getMethod("getHologramManager").invoke(plugin);
                if (hm != null) {
                    hm.getClass().getMethod("show", Player.class).invoke(hm, player);
                }
            }
        } catch (Exception ignored) { }
    }

    private static final java.util.Set<UUID> denyMovement = new java.util.HashSet<>();

    public static boolean isMovementDenied(UUID uuid) {
        return denyMovement.contains(uuid);
    }

    public static boolean isMovementDenied(Player player) {
        return player != null && denyMovement.contains(player.getUniqueId());
    }

    public static int getPing(Player player) {
        try {
            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Field pingField = entityPlayer.getClass().getDeclaredField("ping");

            return pingField.getInt(entityPlayer);
        } catch (NoSuchFieldException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static void resetBase(Player player) {
        player.getActivePotionEffects().clear();
        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.setLevel(0);
        player.setExp(0f);
        player.setFireTicks(0);
        player.setMaximumNoDamageTicks(20);
        player.setNoDamageTicks(20);
        player.setSaturation(20);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getInventory().setContents(new ItemStack[36]);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.updateInventory();
        hologramHide(player);
        hologramShow(player);
    }

    public static void reset(Player player) {
        AsyncCatcher.enabled = false;
        resetBase(player);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            player.showPlayer(onlinePlayer);
            onlinePlayer.showPlayer(player);
        }
    }

    public static void resetSpectator(Player player) {
        AsyncCatcher.enabled = false;
        resetBase(player);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            player.hidePlayer(onlinePlayer);
            onlinePlayer.hidePlayer(player);
        }
    }

    public static void reset(UUID uuid) {
        Player player = Arrays.stream(Bukkit.getOnlinePlayers())
                .filter(p -> p.getUniqueId().equals(uuid))
                .findFirst()
                .orElse(null);
        if (player == null) return;
        AsyncCatcher.enabled = false;
        resetBase(player);
    }

    public static UUID lastAttacker(Player player) {
        Object entityPlayer = null;
        try {
            entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Field lastDamagerField = entityPlayer.getClass().getDeclaredField("lastDamager");
            Field uuidField = lastDamagerField.getDeclaringClass().getDeclaredField("uniqueID");

            return (UUID) uuidField.get(entityPlayer);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

    }

    public static void denyMovement(Player player) {
        /*AsyncCatcher.enabled = false;

        player.setWalkSpeed(0.0F);
        player.setFlySpeed(0.0F);
        player.setFoodLevel(0);
        player.setSprinting(false);
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200));*/

        denyMovement.add(player.getUniqueId());
    }

    public static void allowMovement(Player player) {
        /*AsyncCatcher.enabled = false;

        player.setWalkSpeed(0.2F);
        player.setFlySpeed(0.2F);
        player.setFoodLevel(20);
        player.setSprinting(true);
        player.removePotionEffect(PotionEffectType.JUMP); */
        denyMovement.remove(player.getUniqueId());
    }
}
