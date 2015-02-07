package io.github.andyandreih.overseerauth;

import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandExecutor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class OverseerAuth extends JavaPlugin
{
    CommandListener cmdExec = new CommandListener();
    DatabaseController dbCtrl;

    @Override
    public void onEnable()
    {
        if(!getDataFolder().exists())
        {
            getDataFolder().mkdir();
            getLogger().info("Created plugin folder.");
        }

        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getLogger().info("Event listeners registered.");

        getCommand("overseer").setExecutor(cmdExec);
        getLogger().info("Commands registered.");

        dbCtrl = new DatabaseController();
        dbCtrl.initDb(getDataFolder().getAbsolutePath());
        if(dbCtrl.openDb())
            dbCtrl.generateTable();
        getLogger().info("Generated local database.");
    }
}
