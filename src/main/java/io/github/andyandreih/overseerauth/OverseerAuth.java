package io.github.andyandreih.overseerauth;

import org.bukkit.plugin.java.JavaPlugin;

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
