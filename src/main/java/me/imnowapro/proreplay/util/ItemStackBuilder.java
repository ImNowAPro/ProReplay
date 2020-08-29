package me.imnowapro.proreplay.util;

import java.util.Arrays;
import java.util.List;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackBuilder {

  private final ItemStack itemStack;
  private ItemMeta itemMeta;

  public ItemStackBuilder(Material material) {
    this(material, 1);
  }

  public ItemStackBuilder(Material material, int amount) {
    this.itemStack = new ItemStack(material, amount);
    this.itemMeta = this.itemStack.getItemMeta();
  }

  public ItemStackBuilder(Material material, int amount, String displayName) {
    this(material, amount);
    this.setDisplayName(displayName);
  }

  public ItemStackBuilder setType(Material material) {
    this.itemStack.setType(material);
    this.itemMeta = this.itemStack.getItemMeta();
    return this;
  }

  public ItemStackBuilder setAmount(int amount) {
    this.itemStack.setAmount(amount);
    return this;
  }

  public ItemStackBuilder setDisplayName(String displayName) {
    this.itemMeta.setDisplayName(displayName);
    return this;
  }

  public ItemStackBuilder setDyeColor(DyeColor color) {
    this.itemStack.setDurability(color.getData());
    return this;
  }

  public ItemStackBuilder setLore(String... lore) {
    this.itemMeta.setLore(Arrays.asList(lore));
    return this;
  }

  public ItemStackBuilder removeLore() {
    this.itemMeta.setLore(null);
    return this;
  }

  public ItemStackBuilder addLoreLine(int line, String loreLine) {
    if (this.itemMeta.hasLore() && line < this.itemMeta.getLore().size()) {
      List<String> lore = this.itemMeta.getLore();
      lore.add(line, loreLine);
      this.itemMeta.setLore(lore);
    } else {
      this.setLore(loreLine);
    }
    return this;
  }

  public ItemStackBuilder removeLoreLine(int line) {
    if (this.itemMeta.hasLore() && line < this.itemMeta.getLore().size()) {
      List<String> lore = this.itemMeta.getLore();
      lore.remove(line);
      this.itemMeta.setLore(lore);
    }
    return this;
  }

  public ItemStackBuilder addEnchantment(Enchantment enchantment, int level) {
    if (this.itemMeta instanceof EnchantmentStorageMeta) {
      ((EnchantmentStorageMeta) this.itemMeta).addStoredEnchant(enchantment, level, true);
    } else {
      this.itemMeta.addEnchant(enchantment, level, true);
    }
    return this;
  }

  public ItemStackBuilder removeEnchantment(Enchantment enchantment) {
    if (this.itemMeta instanceof EnchantmentStorageMeta) {
      ((EnchantmentStorageMeta) this.itemMeta).removeStoredEnchant(enchantment);
    } else {
      this.itemMeta.removeEnchant(enchantment);
    }
    return this;
  }

  public ItemStack build() {
    ItemStack built = itemStack.clone();
    built.setItemMeta(this.itemMeta);
    return built;
  }
}