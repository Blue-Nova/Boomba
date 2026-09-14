package io.github.bluenova.boomba.commands;

import io.github.bluenova.boomba.commands.subcomands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainCommand implements CommandExecutor, TabCompleter {

    private final Map<String, SubCommand> commandGroups = new HashMap<>();

    public MainCommand() {
        registerCommands();
    }

    private void registerCommands() {
        commandGroups.put("enable", new EnableEffect());
        commandGroups.put("disable", new DisableEffect());
        commandGroups.put("enable_next", new EnableNext());
        commandGroups.put("scale", new ScaleDifficulty());
        commandGroups.put("set_difficulty", new SetDifficulty());
        commandGroups.put("start", new StartGame());
        commandGroups.put("skip_wave", new SkipWave());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length == 1) {
            return commandGroups.keySet().stream().filter(name -> name.startsWith(args[0])).toList();
        }
        if (args.length > 1) {
            SubCommand subCommand = commandGroups.get(args[0]);
            if (subCommand != null) {
                return subCommand.tabComplete(sender, args);
            }
        }
        return commandGroups.keySet().stream().toList();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (strings.length == 0) {
            commandSender.sendMessage("Usage: /boomba <subcommand>");
            return true;
        }

        String subCommandKey = strings[0].toLowerCase();
        SubCommand subCommand = commandGroups.get(subCommandKey);
        if (!commandSender.hasPermission(subCommand.getPermission())) {
            commandSender.sendMessage("You don't have permission to do that!");
            return true;
        }
        if (subCommand != null) {
            return subCommand.execute(commandSender, strings);
        } else {
            commandSender.sendMessage("Unknown subcommand: " + subCommandKey);
            return false;
        }
    }
}
