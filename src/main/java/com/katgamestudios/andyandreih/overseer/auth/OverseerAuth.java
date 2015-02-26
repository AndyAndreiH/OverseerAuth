package com.katgamestudios.andyandreih.overseer.auth;

import com.katgamestudios.andyandreih.overseer.main.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class OverseerAuth extends JavaPlugin {
    public static EventListener eventListen = new EventListener();
    public static LocalCommandListener cmdExec = new LocalCommandListener();

    Map<String, Boolean> playerLogin = new HashMap<String, Boolean>();

    @Override
    public void onEnable() {
        eventListen.mainClass = this;
        cmdExec.mainClass = this;

        getServer().getPluginManager().registerEvents(eventListen, this);

        CommandListener.registerSubCommand("simulate", new AuthCommandListener());
        getCommand("login").setExecutor(cmdExec);
        getCommand("register").setExecutor(cmdExec);

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
