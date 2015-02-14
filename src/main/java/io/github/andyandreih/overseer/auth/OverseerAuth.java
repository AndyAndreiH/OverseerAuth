package io.github.andyandreih.overseer.auth;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class OverseerAuth extends JavaPlugin {
    public static CommandListener cmdExec = new CommandListener();
    public static DatabaseController dbCtrl = new DatabaseController();
    public static EventListener eventListen = new EventListener();

    Map<String, Boolean> playerLogin = new HashMap<String, Boolean>();

    @Override
    public void onEnable() {
        cmdExec.mainClass = this;
        dbCtrl.mainClass = this;
        eventListen.mainClass = this;

        if(!getDataFolder().exists()) {
            getDataFolder().mkdir();
            getLogger().info("Created plugin folder.");
        }

        getServer().getPluginManager().registerEvents(eventListen, this);
        getLogger().info("Event listeners registered.");

        getCommand("overseer").setExecutor(cmdExec);
        getLogger().info("Commands registered.");

        dbCtrl.initDb(getDataFolder().getAbsolutePath());
        if(dbCtrl.openDb()) {
            dbCtrl.generateTable();
        }
        getLogger().info("Generated local database.");

        for(Player player : this.getServer().getOnlinePlayers()) {
            UUID playerUUID = null;
            try {
                playerUUID = UUIDFetcher.getUUIDOf(player.getDisplayName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            playerLogin.put(playerUUID.toString(), Boolean.FALSE);
        }
    }
}
