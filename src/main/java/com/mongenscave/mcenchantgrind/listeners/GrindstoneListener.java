package com.mongenscave.mcenchantgrind.listeners;

import com.mongenscave.mcenchantgrind.data.MenuController;
import com.mongenscave.mcenchantgrind.gui.models.MenuGrindstone;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.jetbrains.annotations.NotNull;

public class GrindstoneListener implements Listener {
    @EventHandler
    public void onGrindstoneOpen(final @NotNull InventoryOpenEvent event) {
        if (event.getInventory().getType() == InventoryType.GRINDSTONE && event.getPlayer() instanceof Player player && player.isSneaking()) {
            event.setCancelled(true);
            new MenuGrindstone(MenuController.getMenuUtils(player)).open();
        }
    }
}
