package io.github.bluenova.boomba.commands.subcomands;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.commands.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class SkipWave implements SubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Boomba.getWaveManager().setCurrentWaveRemainingTime(1);
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
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
