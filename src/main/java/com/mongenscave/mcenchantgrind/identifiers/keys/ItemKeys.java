package com.mongenscave.mcenchantgrind.identifiers.keys;

import com.mongenscave.mcenchantgrind.McEnchantGrind;
import com.mongenscave.mcenchantgrind.item.ItemFactory;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public enum ItemKeys {
    FILLER_GLASS("menu.items.filler-glass-item"),
    ENCHANTMENT_BOOK("menu.items.enchantment-book-item");

    private final String path;

    ItemKeys(@NotNull final String path) {
        this.path = path;
    }

    public ItemStack getItem() {
        return ItemFactory.createItemFromString(path, McEnchantGrind.getInstance().getConfiguration()).orElse(null);
    }

    public List<String> getList() {
        return McEnchantGrind.getInstance().getConfiguration().getList(path);
    }

    public int getSlot() {
        return McEnchantGrind.getInstance().getConfiguration().getInt(path + ".slot");
    }
}