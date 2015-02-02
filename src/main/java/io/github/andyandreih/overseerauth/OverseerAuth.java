package io.github.andyandreih.overseerauth;

import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class OverseerAuth extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getLogger().info("Event listeners registered.");
    }

    @Override
    public void onDisable()
    {

    }
}
