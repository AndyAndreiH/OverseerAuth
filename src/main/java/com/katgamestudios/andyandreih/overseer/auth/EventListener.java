package com.katgamestudios.andyandreih.overseer.auth;

import com.katgamestudios.andyandreih.overseer.main.UUIDFetcher;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public final class EventListener implements Listener {
    OverseerAuth mainClass = null;

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        UUID userUUID = null;
        try {
            userUUID = UUIDFetcher.getUUIDOf(event.getPlayer().getDisplayName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> userData = mainClass.dbCtrl.getUser(userUUID.toString());
        if(userData.containsKey("id")) {
            event.getPlayer().sendMessage("Please log in using the /login <password> command!");
        }
        else {
            event.getPlayer().sendMessage("Please register using the /register <password> command!");
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(mainClass.playerLogin.get(event.getPlayer().getDisplayName()) == false) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(mainClass.playerLogin.get(event.getPlayer().getDisplayName()) == false) {
            event.setCancelled(true);
        }
    }
}
