package com.mongenscave.mcenchantgrind;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import com.mongenscave.mcenchantgrind.cache.NameCache;
import com.mongenscave.mcenchantgrind.config.Config;
import com.mongenscave.mcenchantgrind.handler.EnchantHandler;
import com.mongenscave.mcenchantgrind.listeners.GrindstoneListener;
import com.mongenscave.mcenchantgrind.listeners.MenuListener;
import com.mongenscave.mcenchantgrind.utils.LoggerUtils;
import com.mongenscave.mcenchantgrind.utils.RegisterUtils;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import revxrsal.zapper.ZapperJavaPlugin;

import java.io.File;

public final class McEnchantGrind extends ZapperJavaPlugin {
    @Getter private static McEnchantGrind instance;
    @Getter private TaskScheduler scheduler;
    @Getter private Config language;
    @Getter private NameCache nameCache;
    @Getter private EnchantHandler enchantHandler;
    private Config config;

    @Override
    public void onLoad() {
        instance = this;
        scheduler = UniversalScheduler.getScheduler(this);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        initializeComponents();

        enchantHandler = new EnchantHandler();
        nameCache = new NameCache();

        //RegisterUtils.registerCommands();

        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new GrindstoneListener(), this);

        LoggerUtils.printStartup();
    }

    @Override
    public void onDisable() {
        if (scheduler != null) scheduler.cancelTasks();
    }

    public Config getConfiguration() {
        return config;
    }

    private void initializeComponents() {
        final GeneralSettings generalSettings = GeneralSettings.builder()
                .setUseDefaults(false)
                .build();

        final LoaderSettings loaderSettings = LoaderSettings.builder()
                .setAutoUpdate(true)
                .build();

        final UpdaterSettings updaterSettings = UpdaterSettings.builder()
                .setKeepAll(true)
                .build();

        config = loadConfig("config.yml", generalSettings, loaderSettings, updaterSettings);
        language = loadConfig("messages.yml", generalSettings, loaderSettings, updaterSettings);
    }

    @NotNull
    @Contract("_, _, _, _ -> new")
    private Config loadConfig(@NotNull String fileName, @NotNull GeneralSettings generalSettings, @NotNull LoaderSettings loaderSettings, @NotNull UpdaterSettings updaterSettings) {
        return new Config(
                new File(getDataFolder(), fileName),
                getResource(fileName),
                generalSettings,
                loaderSettings,
                DumperSettings.DEFAULT,
                updaterSettings
        );
    }
}
