package io.github.andyandreih.overseerauth;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public final class EventListener implements Listener
{

    @EventHandler
    public void onLogin(PlayerLoginEvent event)
    {
        UUIDFetcher fetcher = new UUIDFetcher(Arrays.asList(event.getPlayer().getDisplayName()));
        Map<String, UUID> result = null;
        try
        {
            result = fetcher.call();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        ResultSet userData = OverseerAuth.dbCtrl.getUser(result.get(event.getPlayer().getDisplayName()).toString());
        String userDataName = null;
        try { userDataName = userData.getString("displayName"); } catch (SQLException e) { e.printStackTrace(); }
        if(userDataName != null)
        {
            event.getPlayer().sendMessage("Please log in using the /login [Password] command!");
        }
        else
        {
            event.getPlayer().sendMessage("Please register using the /register [Password] command!");
        }
    }
}
