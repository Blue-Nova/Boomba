package io.github.bluenova.boomba.commands.subcomands;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.commands.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class DisableEffect implements SubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        boolean success = Boomba.getBoomEffectsManager().disableEffect(args[1]);
        if (success) {
            sender.sendMessage("Effect " + args[1] + " has been disabled.");
        } else {
            sender.sendMessage("Failed to disable effect: " + args[1] + ". It may not exist or is already disabled.");
        }
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        // Get all active effect IDs from the manager
        return Boomba.getBoomEffectsManager().getAllActiveEffectIds(); // Change this based on your tab completion logic
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public String getPermission() {
        return "boomba.admin";
    }
}
