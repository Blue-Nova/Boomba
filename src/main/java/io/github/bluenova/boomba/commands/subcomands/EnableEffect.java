package io.github.bluenova.boomba.commands.subcomands;


import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.commands.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class EnableEffect implements SubCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        boolean success = Boomba.getBoomEffectsManager().enableEffect(args[1]);
        if (success) {
            sender.sendMessage("Effect " + args[1] + " has been enabled.");
        } else {
            sender.sendMessage("Failed to enable effect: " + args[1] + ". It may not exist or is already enabled.");
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Boomba.getBoomEffectsManager().getAllDisabledEffectIds(); // Change this based on your tab completion logic
    }

    @Override
    public String getUsage() {
        return "/boomba enable <effect>";
    }

    @Override
    public String getPermission() {
        return "boomba.admin";
    }
}
