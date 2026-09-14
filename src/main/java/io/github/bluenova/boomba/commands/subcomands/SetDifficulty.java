package io.github.bluenova.boomba.commands.subcomands;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SetDifficulty implements SubCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage("Usage: " + getUsage());
            return true;
        }
        int difficulty;
        try {
            difficulty = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(args[1] + " is not a valid number!");
            return true;
        }
        if (difficulty < 1) {
            sender.sendMessage("Difficulty must be at least 1.");
            return true;
        }
        Boomba.getBoomEffectsManager().setDifficulty(difficulty, player, false);

        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        ArrayList<String> suggestions = new ArrayList<>();
        suggestions.add("<number>");
        return suggestions;
    }

    @Override
    public String getUsage() {
        return "/boomba setdifficulty <number>";
    }

    @Override
    public String getPermission() {
        return "boomba.admin";
    }

}
