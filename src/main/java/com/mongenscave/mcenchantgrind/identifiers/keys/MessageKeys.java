package com.mongenscave.mcenchantgrind.identifiers.keys;

import com.mongenscave.mcenchantgrind.McEnchantGrind;
import com.mongenscave.mcenchantgrind.config.Config;
import com.mongenscave.mcenchantgrind.processors.MessageProcessor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public enum MessageKeys {
    RELOAD("messages.reload"),
    NO_PERMISSION("messages.no-permission"),

    NOT_ENOUGH_FUNDS("messages.not-enough-funds");

    private final String path;
    private static final Config config = McEnchantGrind.getInstance().getLanguage();

    MessageKeys(@NotNull String path) {
        this.path = path;
    }

    public @NotNull String getMessage() {
        return MessageProcessor.process(config.getString(path))
                .replace("%prefix%", MessageProcessor.process(config.getString("prefix")));
    }

    public List<String> getMessages() {
        return config.getStringList(path)
                .stream()
                .toList();
    }
}
