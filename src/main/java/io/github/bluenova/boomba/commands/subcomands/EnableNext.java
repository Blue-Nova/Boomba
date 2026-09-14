package io.github.bluenova.boomba.commands.subcomands;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class EnableNext implements SubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return false;
        boolean success = Boomba.getBoomEffectsManager().enableNextEffect(player);
        if (success) {
            sender.sendMessage("The next effect has been enabled.");
        } else {
            sender.sendMessage("Failed to enable the next effect. There may be no more effects to enable.");
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList(); // No tab completion needed for this command
    }

    @Override
    public String getUsage() {
        return "/boomba enable next";
    }

    @Override
    public String getPermission() {
        return "boomba.admin";
    }
}
