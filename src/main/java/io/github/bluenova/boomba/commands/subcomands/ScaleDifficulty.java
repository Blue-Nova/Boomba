package io.github.bluenova.boomba.commands.subcomands;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.UI.UIManager;
import io.github.bluenova.boomba.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ScaleDifficulty implements SubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        int scale = 1;
        try {
            if (args.length > 2) scale = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(UIManager.mini("<red>"+ scale +" is not a valid number!"));
            return true;
        }
        Boomba.getBoomEffectsManager().scaleDifficulty(scale, player, false);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList(); // No tab completion for this command
    }

    @Override
    public String getUsage() {
        return "/boomba scale <number>";
    }

    @Override
    public String getPermission() {
        return "boomba.admin";
    }
}
