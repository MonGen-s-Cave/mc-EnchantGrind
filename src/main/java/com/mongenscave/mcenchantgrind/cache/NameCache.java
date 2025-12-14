package com.mongenscave.mcenchantgrind.cache;

import org.bukkit.enchantments.Enchantment;

import java.util.WeakHashMap;

public final class NameCache {
    private final WeakHashMap<Enchantment, String> cache = new WeakHashMap<>();

    public String get(Enchantment enchant) {
        return cache.computeIfAbsent(enchant, enchantment -> {
            String name = enchantment.getKey().getKey()
                    .replace('_', ' ')
                    .toLowerCase();

            String[] words = name.split(" ");
            StringBuilder result = new StringBuilder();

            for (String word : words) {
                if (!word.isEmpty()) {
                    result.append(Character.toUpperCase(word.charAt(0)))
                            .append(word.substring(1))
                            .append(" ");
                }
            }

            return result.toString().trim();
        });
    }
}