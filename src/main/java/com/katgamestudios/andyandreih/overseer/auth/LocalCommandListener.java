package com.katgamestudios.andyandreih.overseer.auth;

import com.katgamestudios.andyandreih.overseer.main.UUIDFetcher;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;

public class LocalCommandListener implements CommandExecutor {
    OverseerAuth mainClass;

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    private final String authPrefix = ChatColor.DARK_GRAY + "[AUTH] " + ChatColor.GRAY;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("login")) {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                if(args.length == 1) {
                    if(mainClass.playerLogin.get(player.getDisplayName()) == false) {
                        UUID userUUID = null;
                        try {
                            userUUID = UUIDFetcher.getUUIDOf(player.getDisplayName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Map<String, String> userData = mainClass.dbCtrl.getUser(userUUID.toString());
                        if (userData.containsKey("id")) {
                            String processedPass = args[0] + ":" + userData.get("salt");
                            MessageDigest md = null;
                            try {
                                md = MessageDigest.getInstance("SHA-256");
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            }
                            byte[] passBytes = new byte[0];
                            try {
                                passBytes = processedPass.getBytes("UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            byte[] securePassBytes = md.digest(passBytes);
                            String securePass = bytesToHex(securePassBytes);
                            if (userData.get("password").equalsIgnoreCase(securePass)) {
                                mainClass.playerLogin.replace(((Player) sender).getPlayer().getDisplayName(), true);
                                player.sendMessage(authPrefix + ChatColor.GREEN + "Logged in successfully!");
                                LoginEvent loginEvent = new LoginEvent((Player) sender);
                                mainClass.getServer().getPluginManager().callEvent(loginEvent);
                                return true;
                            } else {
                                player.sendMessage(authPrefix + ChatColor.RED + "The password is incorrect!");
                                FailedLoginEvent failedLoginEvent = new FailedLoginEvent((Player) sender);
                                mainClass.getServer().getPluginManager().callEvent(failedLoginEvent);
                                return true;
                            }
                        }else {
                            player.sendMessage(authPrefix + "Please register using the /register <password> command!");
                            return true;
                        }
                    } else {
                        player.sendMessage(authPrefix + "You are already logged in!");
                        return true;
                    }
                } else {
                    player.sendMessage(authPrefix + ChatColor.RED + "SYNTAX: /login <password>");
                    return true;
                }
            } else {
                sender.sendMessage(authPrefix + ChatColor.RED + "Only players can use this command!");
                return true;
            }
        }
        else if(cmd.getName().equalsIgnoreCase("register")) {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length == 1) {
                    if (mainClass.playerLogin.get(player.getDisplayName()) == false) {
                        UUID userUUID = null;
                        try {
                            userUUID = UUIDFetcher.getUUIDOf(player.getDisplayName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Map<String, String> userData = mainClass.dbCtrl.getUser(userUUID.toString());
                        if (userData.containsKey("id")) {
                            player.sendMessage(authPrefix + ChatColor.RED + "Please log in using the /login <password> command!");
                            return true;
                        } else {
                            try {
                                mainClass.dbCtrl.registerUser(player.getDisplayName(), args[0]);
                                player.sendMessage(authPrefix + ChatColor.GREEN + "Successfully registered!");
                                player.sendMessage(authPrefix + "Please log in using the /login <password> command!");
                                RegisterEvent registerEvent = new RegisterEvent(player);
                                mainClass.getServer().getPluginManager().callEvent(registerEvent);
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            return true;
                        }
                    }
                } else {
                    player.sendMessage(authPrefix + ChatColor.RED + "SYNTAX: /register <password>");
                    return true;
                }
            } else {
                sender.sendMessage(authPrefix + ChatColor.RED + "Only players can use this command!");
                return true;
            }
        }
        return false;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
