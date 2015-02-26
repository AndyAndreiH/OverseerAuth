package com.katgamestudios.andyandreih.overseer.auth;

import com.katgamestudios.andyandreih.overseer.main.OverseerMain;
import com.katgamestudios.andyandreih.overseer.main.UUIDFetcher;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.junit.internal.runners.statements.Fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class EventListener implements Listener {
    OverseerAuth mainClass = null;

    HashMap<Player, Location> initPos = new HashMap<Player, Location>();

    private final String authPrefix = ChatColor.DARK_GRAY + "[AUTH] " + ChatColor.GRAY;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        String playerName = event.getPlayer().getPlayerListName();
        mainClass.playerLogin.put(playerName, false);
        UUID userUUID = null;
        try {
            userUUID = UUIDFetcher.getUUIDOf(playerName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> userData = OverseerMain.dbCtrl.getUser(userUUID.toString());
        if(userData.containsKey("id")) {
            mainClass.getLogger().info(authPrefix + ChatColor.WHITE + playerName + " is registered.");
            mainClass.getServer().broadcastMessage(authPrefix + ChatColor.GOLD + playerName + ChatColor.GRAY + " joined the server.");
            event.getPlayer().sendMessage(authPrefix + "Welcome, " + ChatColor.GOLD + playerName);
            event.getPlayer().sendMessage(authPrefix + "Please log in using the " + ChatColor.BLUE + "/login <password>" + ChatColor.GRAY + " command!");
        }
        else {
            mainClass.getLogger().info(authPrefix + ChatColor.WHITE + playerName + " is not registered.");
            mainClass.getServer().broadcastMessage(authPrefix + ChatColor.GOLD + playerName + ChatColor.GRAY + " joined the server.");
            mainClass.getServer().broadcastMessage(authPrefix + "Everyone welcome " + ChatColor.GOLD + playerName + ChatColor.GRAY + " to the server!");
            event.getPlayer().sendMessage(authPrefix + "Welcome, " + ChatColor.GOLD + playerName);
            event.getPlayer().sendMessage(authPrefix + "Please register using the " + ChatColor.BLUE + "/register <password>" + ChatColor.GRAY + " command!");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        String playerName = event.getPlayer().getPlayerListName();
        mainClass.playerLogin.remove(playerName);
        if(initPos.containsKey(playerName)) {
            initPos.remove(playerName);
        }
        mainClass.getServer().broadcastMessage(authPrefix + ChatColor.GOLD + playerName + ChatColor.GRAY + " left the server.");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(mainClass.playerLogin.get(event.getPlayer().getDisplayName()) == false) {
            if(!initPos.containsKey(event.getPlayer())) {
                initPos.put(event.getPlayer(), event.getPlayer().getLocation());
            }
            event.getPlayer().teleport(initPos.get(event.getPlayer()));
        } else {
            if(initPos.containsKey(event.getPlayer())) {
                initPos.remove(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(mainClass.playerLogin.get(event.getPlayer().getDisplayName()) == false) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerLogin(LoginEvent event) {
        for(Player player : mainClass.getServer().getOnlinePlayers()) {
            if(player.isOp()) {
                player.sendMessage(ChatColor.DARK_RED + "[OP] " + authPrefix + ChatColor.DARK_RED +
                        event.getPlayer().getDisplayName() + " has logged in.");
            }
        }
    }

    @EventHandler
    public void onPlayerRegister(RegisterEvent event) {
        for(Player player : mainClass.getServer().getOnlinePlayers()) {
            if(player.isOp()) {
                player.sendMessage(ChatColor.DARK_RED + "[OP] " + authPrefix + ChatColor.DARK_RED +
                        event.getPlayer().getDisplayName() + " has registered a new account.");
            }
        }
    }

    @EventHandler
    public void onPlayerFailLogin(FailedLoginEvent event) {
        for(Player player : mainClass.getServer().getOnlinePlayers()) {
            if(player.isOp()) {
                player.sendMessage(ChatColor.DARK_RED + "[OP] " + authPrefix + ChatColor.DARK_RED +
                        event.getPlayer().getDisplayName() + " has attempted to log in but failed.");
            }
        }
    }
}
