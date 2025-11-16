package com.mongenscave.mcenchantgrind.identifiers.keys;

import com.mongenscave.mcenchantgrind.McEnchantGrind;
import com.mongenscave.mcenchantgrind.config.Config;
import com.mongenscave.mcenchantgrind.processors.MessageProcessor;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

@Getter
public enum ConfigKeys {
    ALIASES("aliases"),

    MENU_TITLE("menu.title"),
    MENU_SIZE("menu.size"),
    MENU_OUTPUT_SLOT("menu.output-slot"),
    MENU_INPUT_SLOT("menu.input-slot"),
    MENU_DISPLAY_SLOT("menu.display-slot"),

    SETTING_CLICK_SOUND("settings.click-sound"),
    SETTING_BEFORE_SYMBOL("settings.before-symbol"),
    SETTING_AFTER_SYMBOL("settings.after-symbol"),
    SETTING_SELECTED_ENCHANT_COLOR("settings.selected-enchantment-color"),
    SETTING_UNSELECTED_ENCHANT_COLOR("settings.unselected-enchantment-color"),
    SETTING_PER_XP_PER_LEVEL("settings.price-xp-per-level");

    private final String path;
    private static final Config config = McEnchantGrind.getInstance().getConfiguration();

    ConfigKeys(@NotNull String path) {
        this.path = path;
    }

    public @NotNull String getString() {
        return MessageProcessor.process(config.getString(path));
    }

    public static @NotNull String getString(@NotNull String path) {
        return config.getString(path);
    }

    public boolean getBoolean() {
        return config.getBoolean(path);
    }

    public int getInt() {
        return config.getInt(path);
    }

    public List<String> getList() {
        return config.getList(path);
    }

    public Section getSection() {
        return config.getSection(path);
    }

    @NotNull
    public Set<String> getKeys() {
        Section section = config.getSection(path);
        return section != null ? section.getRoutesAsStrings(false) : Set.of();
    }
}
