/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.utils;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {
    private static final ItemFlag[] ALL_FLAGS = ItemFlag.values();

    private final ItemStack is;

    public ItemBuilder(Material mat) {
        this.is = new ItemStack(mat);
    }

    public ItemBuilder(ItemStack is) {
        this.is = is;
    }

    public ItemBuilder amount(int amount) {
        this.is.setAmount(amount);
        return this;
    }

    public ItemBuilder name(String name) {
        ItemMeta meta = this.is.getItemMeta();
        meta.setDisplayName(CC.translate(name));
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(String line) {
        ItemMeta meta = this.is.getItemMeta();
        List<String> lore = meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        lore.add(CC.translate(line));
        meta.setLore(lore);
        this.is.setItemMeta(meta);
        return this;
    }

    /** Adiciona várias linhas de lore de uma vez. */
    public ItemBuilder lore(String... lines) {
        if (lines == null || lines.length == 0) return this;
        ItemMeta meta = this.is.getItemMeta();
        List<String> lore = meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        for (String line : lines) lore.add(CC.translate(line));
        meta.setLore(lore);
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        if (lore == null) return this;
        ItemMeta meta = this.is.getItemMeta();
        List<String> toSet = new ArrayList<>();
        for (String line : lore) toSet.add(CC.translate(line));
        meta.setLore(toSet);
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder durability(int durability) {
        this.is.setDurability((short)durability);
        return this;
    }

    /** @deprecated */
    @Deprecated
    public ItemBuilder data(int data) {
        this.is.setData(new MaterialData(this.is.getType(), (byte)data));
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        this.is.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment) {
        this.is.addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    public ItemBuilder type(Material material) {
        this.is.setType(material);
        return this;
    }

    public ItemBuilder clearLore() {
        ItemMeta meta = this.is.getItemMeta();
        meta.setLore(new ArrayList<>());
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder clearEnchantments() {
        for (Enchantment e : this.is.getEnchantments().keySet()) {
            this.is.removeEnchantment(e);
        }
        return this;
    }

    public ItemBuilder color(Color color) {
        if (this.is.getType() != Material.LEATHER_BOOTS && this.is.getType() != Material.LEATHER_CHESTPLATE
                && this.is.getType() != Material.LEATHER_HELMET && this.is.getType() != Material.LEATHER_LEGGINGS) {
            throw new IllegalArgumentException("color() only applicable for leather armor!");
        }
        LeatherArmorMeta meta = (LeatherArmorMeta) this.is.getItemMeta();
        meta.setColor(color);
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addFlags(ItemFlag... itemFlags) {
        if (itemFlags == null || itemFlags.length == 0) return this;
        ItemMeta meta = this.is.getItemMeta();
        try {
            meta.getClass().getMethod("addItemFlags", ItemFlag[].class).invoke(meta, (Object) itemFlags);
        } catch (Exception ignored) { }
        this.is.setItemMeta(meta);
        return this;
    }

    /** Esconde todos os flags (encantamento, durabilidade, etc.) — item “limpo”. */
    public ItemBuilder hideAllFlags() {
        return addFlags(ALL_FLAGS);
    }

    /** Deixa o item com brilho de encantamento (sem mostrar encanto real). */
    public ItemBuilder glow() {
        this.is.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        return hideAllFlags();
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta meta = this.is.getItemMeta();
        try {
            Object spigot = meta.getClass().getMethod("spigot").invoke(meta);
            if (spigot != null) {
                spigot.getClass().getMethod("setUnbreakable", boolean.class).invoke(spigot, unbreakable);
            }
        } catch (Exception ignored) { }
        this.is.setItemMeta(meta);
        return this;
    }

    public SkullBuilder skullBuilder() {
        return new SkullBuilder(this);
    }

    public ItemStack build() {
        return this.is;
    }
}
