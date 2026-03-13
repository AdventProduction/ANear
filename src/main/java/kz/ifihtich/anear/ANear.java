package kz.ifihtich.anear;

import org.bukkit.plugin.java.JavaPlugin;

public final class ANear extends JavaPlugin {

    private static ANear instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getCommand("near").setExecutor(new NearCommand());
        Utils.logo();

    }

    @Override
    public void onDisable() {
    }

    public static ANear getInstance(){
        return instance;
    }
}
