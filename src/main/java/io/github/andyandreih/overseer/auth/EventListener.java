package io.github.andyandreih.overseer.auth;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public final class EventListener implements Listener {
    OverseerAuth mainClass = null;

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        UUIDFetcher fetcher = new UUIDFetcher(Arrays.asList(event.getPlayer().getDisplayName()));
        Map<String, UUID> userUUIDs = null;
        try {
            userUUIDs = fetcher.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String userUUID = "";
        for(Map.Entry<String, UUID> entry : userUUIDs.entrySet()) {
            if(entry.getKey().equalsIgnoreCase(event.getPlayer().getDisplayName())) {
                userUUID = entry.getValue().toString();
            }
        }
        Map<String, String> userData = mainClass.dbCtrl.getUser(userUUID);
        if(userData.containsKey("id")) {
            event.getPlayer().sendMessage("Please log in using the /login <password> command!");
        }
        else {
            event.getPlayer().sendMessage("Please register using the /register <password> command!");
        }
    }
}
