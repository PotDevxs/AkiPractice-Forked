/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.utils;

import org.bukkit.potion.PotionEffectType;

public class PotionUtil {

    /**
     * Retorna um nome legível para o efeito (ex: "FIRE_RESISTANCE" → "Fire Resistance").
     * Se o efeito for null, retorna "Nenhum".
     */
    public static String getName(PotionEffectType potionEffectType) {
        if (potionEffectType == null) return "Nenhum";
        String name = potionEffectType.getName();
        if (name == null || name.isEmpty()) return "Desconhecido";
        StringBuilder sb = new StringBuilder();
        for (String part : name.split("_")) {
            if (part.isEmpty()) continue;
            if (sb.length() > 0) sb.append(" ");
            sb.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) sb.append(part.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    /**
     * Retorna a duração formatada (ticks → "Xm Ys" ou "Permanente").
     */
    public static String formatDuration(int ticks) {
        if (ticks <= 0 || ticks >= 20 * 60 * 60 * 24) return "Permanente";
        int seconds = ticks / 20;
        int min = seconds / 60;
        int sec = seconds % 60;
        if (min > 0) return min + "m " + sec + "s";
        return sec + "s";
    }
}
