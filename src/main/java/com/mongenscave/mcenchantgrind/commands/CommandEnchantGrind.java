package com.mongenscave.mcenchantgrind.commands;

import com.mongenscave.mcenchantgrind.McEnchantGrind;
import com.mongenscave.mcenchantgrind.identifiers.keys.MessageKeys;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.orphan.OrphanCommand;

public class CommandEnchantGrind implements OrphanCommand {
    private static final McEnchantGrind plugin = McEnchantGrind.getInstance();

    @Subcommand("reload")
    @CommandPermission("mcenchantgrind.reload")
    public void reload(@NotNull CommandSender sender) {
        plugin.getConfiguration().reload();
        plugin.getLanguage().reload();

        sender.sendMessage(MessageKeys.RELOAD.getMessage());
    }
}
