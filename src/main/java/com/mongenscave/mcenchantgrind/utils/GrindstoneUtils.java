package com.mongenscave.mcenchantgrind.utils;

import com.mongenscave.mcenchantgrind.identifiers.keys.ConfigKeys;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@UtilityClass
public class GrindstoneUtils {
    public void playSound(@NotNull Player player) {
        player.playSound(player.getLocation(), Sound.valueOf(ConfigKeys.SETTING_CLICK_SOUND.getString()), 0.8f, 1.0f);
    }

    public boolean isValidItem(@Nullable ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        return !item.getEnchantments().isEmpty() || (item.getItemMeta() instanceof EnchantmentStorageMeta meta && !meta.getStoredEnchants().isEmpty());
    }

    public void safeGiveItem(@NotNull Player player, @NotNull ItemStack item) {
        if (item.getType() == Material.AIR) return;

        ItemStack clone = item.clone();
        Map<Integer, ItemStack> leftovers = player.getInventory().addItem(clone);

        leftovers
                .values()
                .forEach(leftover -> player.getWorld().dropItemNaturally(player.getLocation(), leftover));
    }
}
