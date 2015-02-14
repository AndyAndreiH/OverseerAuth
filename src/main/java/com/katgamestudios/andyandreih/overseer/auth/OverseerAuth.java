package com.katgamestudios.andyandreih.overseer.auth;

import com.katgamestudios.andyandreih.overseer.main.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class OverseerAuth extends JavaPlugin {
    public static DatabaseController dbCtrl = new DatabaseController();
    public static EventListener eventListen = new EventListener();

    Map<String, Boolean> playerLogin = new HashMap<String, Boolean>();

    @Override
    public void onEnable() {
        eventListen.mainClass = this;

        getServer().getPluginManager().registerEvents(eventListen, this);
        getLogger().info("Event listeners registered.");

        CommandListener.registerSubCommand("simulate", new AuthCommandListener());
        getLogger().info("Commands registered.");

        dbCtrl.initDb(OverseerMain.dataFolder);
        dbCtrl.openDb();
        getLogger().info("Connected to database.");

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
