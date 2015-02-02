package io.github.andyandreih.overseerauth;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandListener implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if((sender instanceof Player) && !(sender.isOp()))
        {
            sender.sendMessage("[ERROR] Commands can only be accessed by Ops!");
            return true;
        }
        if(cmd.getName().equalsIgnoreCase("overseer"))
        {
            if(args.length == 0)
            {
                displayHelp(sender);
                return true;
            }
            else if(args.length == 1)
            {
                if(args[0].equalsIgnoreCase("help"))
                {
                    displayHelp(sender);
                    return true;
                }
            }
        }
        return false;
    }

    private void displayHelp(CommandSender sender)
    {
        sender.sendMessage("------------- Overseer Help -------------");
        sender.sendMessage(" /overseer - Displays help");
        sender.sendMessage(" /overseer help - Displays help");
        sender.sendMessage("-----------------------------------------");
    }
}
