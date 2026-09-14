package io.github.bluenova.boomba.commands.subcomands;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class StartGame implements SubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can start this game");
            return false;
        }

        Boomba.getWaveManager().start();

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public String getUsage() {
        return "/boomba start";
    }

    @Override
    public String getPermission() {
        return "boomba.admin";
    }
}
