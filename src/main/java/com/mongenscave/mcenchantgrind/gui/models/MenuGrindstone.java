package com.mongenscave.mcenchantgrind.gui.models;

import com.mongenscave.mcenchantgrind.McEnchantGrind;
import com.mongenscave.mcenchantgrind.data.MenuController;
import com.mongenscave.mcenchantgrind.gui.Menu;
import com.mongenscave.mcenchantgrind.handler.EnchantHandler;
import com.mongenscave.mcenchantgrind.identifiers.keys.ConfigKeys;
import com.mongenscave.mcenchantgrind.identifiers.keys.ItemKeys;
import com.mongenscave.mcenchantgrind.identifiers.keys.MessageKeys;
import com.mongenscave.mcenchantgrind.item.ItemFactory;
import com.mongenscave.mcenchantgrind.processors.MessageProcessor;
import com.mongenscave.mcenchantgrind.utils.GrindstoneUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("deprecation")
public final class MenuGrindstone extends Menu {
    private static final String MENU_ITEMS_PATH = "menu.items";

    private final EnchantHandler enchantHandler;
    private final int inputSlot;
    private final int outputSlot;
    private final int displaySlot;

    public MenuGrindstone(@NotNull MenuController menuController) {
        super(menuController);
        this.enchantHandler = McEnchantGrind.getInstance().getEnchantHandler();
        this.inputSlot = ConfigKeys.MENU_INPUT_SLOT.getInt();
        this.outputSlot = ConfigKeys.MENU_OUTPUT_SLOT.getInt();
        this.displaySlot = ConfigKeys.MENU_DISPLAY_SLOT.getInt();
    }

    @Override
    public @NotNull String getMenuName() {
        return ConfigKeys.MENU_TITLE.getString();
    }

    @Override
    public int getSlots() {
        return ConfigKeys.MENU_SIZE.getInt();
    }

    @Override
    public int getMenuTick() {
        return 5;
    }

    @Override
    public void handleMenu(final @NotNull InventoryClickEvent event) {
        event.setCancelled(true);

        if (!event.getInventory().equals(getInventory())) return;

        int slot = event.getRawSlot();

        if (slot == inputSlot) handleInputSlot(event);
        else if (slot == outputSlot) handleOutputSlot(event);
        else if (event.isShiftClick() && event.getClickedInventory() == event.getView().getBottomInventory()) handleShiftClick(event);
        else handleEnchantDisplay(event);
    }

    @Override
    public void setMenuItems() {
        ItemFactory.setItemsForMenu(MENU_ITEMS_PATH, getInventory());
        updateEnchantDisplay();
    }

    private void handleInputSlot(final @NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();

        if (!cursor.getType().isAir()) {
            if (GrindstoneUtils.isValidItem(cursor)) {
                setInput(cursor.clone());
                event.setCursor(null);
                GrindstoneUtils.playSound(player);
            }
        }
        else if (current != null && !current.getType().isAir()) {
            if (GrindstoneUtils.isValidItem(current)) {
                event.setCursor(current.clone());
                clearInput();
                GrindstoneUtils.playSound(player);
            }
        }
    }

    private void handleOutputSlot(final @NotNull InventoryClickEvent event) {
        if (event.getClick().isShiftClick()) return;

        ItemStack output = event.getCurrentItem();
        if (output == null || !GrindstoneUtils.isValidItem(output)) return;

        Player player = (Player) event.getWhoClicked();
        int levelsToRemove = calculateLevelsToRemove(output);

        if (!enchantHandler.canAffordLevelReduction(player, levelsToRemove)) {
            player.sendMessage(MessageKeys.NOT_ENOUGH_FUNDS.getMessage());
            return;
        }

        ItemStack inputItem = getInventory().getItem(inputSlot);

        if (inputItem != null && !inputItem.getType().isAir()) {
            ItemStack modifiedInput = processEnchantRemoval(inputItem, output);
            GrindstoneUtils.safeGiveItem(player, modifiedInput);
        }

        event.setCursor(output.clone());

        clearInput();
        getInventory().setItem(outputSlot, null);
        GrindstoneUtils.playSound(player);
    }

    private void handleShiftClick(final @NotNull InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !GrindstoneUtils.isValidItem(clicked)) return;

        ItemStack current = getInventory().getItem(inputSlot);
        if (current != null && !current.getType().isAir()) return;

        setInput(clicked.clone());
        event.setCurrentItem(null);
        GrindstoneUtils.playSound((Player) event.getWhoClicked());
    }

    private void handleEnchantDisplay(final @NotNull InventoryClickEvent event) {
        if (enchantHandler.isEmpty()) return;

        ClickType click = event.getClick();
        Player player = (Player) event.getWhoClicked();

        switch (click) {
            case DROP -> enchantHandler.toggleAllEnchants();
            case SHIFT_LEFT -> enchantHandler.currentEnchantment().ifPresent(enchant -> enchantHandler.adjustEnchantLevel(enchant, true));
            case SHIFT_RIGHT -> enchantHandler.currentEnchantment().ifPresent(enchant -> enchantHandler.adjustEnchantLevel(enchant, false));
            case LEFT -> enchantHandler.navigate(true);
            case RIGHT -> enchantHandler.navigate(false);
            default -> {
                return;
            }
        }

        GrindstoneUtils.playSound(player);
        updateEnchantDisplay();
    }

    private void setInput(@NotNull ItemStack item) {
        enchantHandler.clear();
        getInventory().setItem(inputSlot, item);
        enchantHandler.extractEnchantments(item);
        updateEnchantDisplay();
    }

    private void clearInput() {
        enchantHandler.clear();
        getInventory().setItem(inputSlot, null);
        getInventory().setItem(outputSlot, null);
        updateEnchantDisplay();
    }

    private void updateEnchantDisplay() {
        if (enchantHandler.isEmpty()) {
            getInventory().setItem(displaySlot, ItemKeys.ENCHANTMENT_BOOK.getItem());
            getInventory().setItem(outputSlot, null);
            return;
        }

        ItemStack displayBook = ItemKeys.ENCHANTMENT_BOOK.getItem();
        if (displayBook == null) return;

        ItemMeta meta = displayBook.getItemMeta();
        if (meta == null) return;

        List<String> baseLore = ItemKeys.ENCHANTMENT_BOOK.getList();
        List<String> fullLore = new ArrayList<>(baseLore);

        enchantHandler.getTotalEnchants().forEach((enchant, level) ->
                fullLore.add(formatEnchantLine(enchant, level))
        );

        meta.setLore(fullLore);
        displayBook.setItemMeta(meta);
        getInventory().setItem(displaySlot, displayBook);

        updateOutputBook();
    }

    private @NotNull String formatEnchantLine(@NotNull Enchantment enchant, int totalLevel) {
        String color = enchantHandler.currentEnchantment()
                .filter(current -> current.equals(enchant))
                .map(selected -> ConfigKeys.SETTING_SELECTED_ENCHANT_COLOR.getString())
                .orElse(ConfigKeys.SETTING_UNSELECTED_ENCHANT_COLOR.getString());

        int selectedLevel = enchantHandler.getSelectedLevel(enchant);
        String formattedName = enchantHandler.getFormattedName(enchant);

        return MessageProcessor.process(
                "%s%s%s %s&8%s&7%s".formatted(
                        color,
                        ConfigKeys.SETTING_BEFORE_SYMBOL.getString(),
                        formattedName,
                        totalLevel,
                        ConfigKeys.SETTING_AFTER_SYMBOL.getString(),
                        selectedLevel
                )
        );
    }

    private void updateOutputBook() {
        if (!enchantHandler.hasSelectedEnchants()) {
            getInventory().setItem(outputSlot, null);
            return;
        }

        ItemStack outputBook = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) outputBook.getItemMeta();
        if (meta == null) return;

        enchantHandler.getSelectedEnchants().forEach((enchant, level) -> meta.addStoredEnchant(enchant, level, true));

        outputBook.setItemMeta(meta);
        getInventory().setItem(outputSlot, outputBook);
    }

    private @NotNull ItemStack processEnchantRemoval(@NotNull ItemStack input, @NotNull ItemStack output) {
        ItemStack modifiedInput = input.clone();
        ItemMeta inputMeta = modifiedInput.getItemMeta();
        if (inputMeta == null) return modifiedInput;

        EnchantmentStorageMeta outputMeta = (EnchantmentStorageMeta) output.getItemMeta();
        if (outputMeta == null) return modifiedInput;

        outputMeta.getStoredEnchants().forEach((enchant, level) ->
                updateEnchantLevel(inputMeta, enchant, level)
        );

        modifiedInput.setItemMeta(inputMeta);
        return modifiedInput;
    }

    private void updateEnchantLevel(@NotNull ItemMeta meta, @NotNull Enchantment enchant, int removedLevel) {
        int totalLevel = enchantHandler.getTotalLevel(enchant);
        int remainingLevel = totalLevel - removedLevel;

        if (meta instanceof EnchantmentStorageMeta storageMeta) {
            storageMeta.removeStoredEnchant(enchant);
            if (remainingLevel > 0) storageMeta.addStoredEnchant(enchant, remainingLevel, true);
        } else {
            meta.removeEnchant(enchant);
            if (remainingLevel > 0) meta.addEnchant(enchant, remainingLevel, true);
        }
    }

    private int calculateLevelsToRemove(@NotNull ItemStack output) {
        if (!(output.getItemMeta() instanceof EnchantmentStorageMeta meta)) return 0;

        return meta.getStoredEnchants().values().stream()
                .mapToInt(Integer::intValue)
                .sum();
    }
}