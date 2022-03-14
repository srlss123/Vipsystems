package me.zhanshi123.vipsystem.command;

import me.zhanshi123.vipsystem.Main;
import me.zhanshi123.vipsystem.command.sub.*;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;

public class CommandHandler {

    private final Set<SubCommand> commands = new HashSet<>();

    public CommandHandler(String name) {
        Bukkit.getPluginCommand(name).setExecutor(new CommandsExecutor());
        commands.add(new MeCommand());
        commands.add(new RemoveCommand());
        commands.add(new GiveCommand());
        commands.add(new ClaimCommand());
        commands.add(new ReloadCommand());
        commands.add(new LookCommand());
        commands.add(new ListCommand());
        if (Main.isEnableCustomFunction()) {
            commands.add(new CustomsCommand());
            commands.add(new RunCommand());
        }
        try {
            commands.add(new ChangeVipCommand());
        } catch (NoClassDefFoundError e) {
            Bukkit.getConsoleSender().sendMessage("§c[VipSystem] Cannot initialize command ChangeVip, skipping registration. Reason: " + e.getMessage());
        }
    }

    public SubCommand getSubCommand(String cmd) {
        for (SubCommand command : commands) {
            if (command.getName().equalsIgnoreCase(cmd)) {
                return command;
            }
        }
        return null;
    }

    public Set<SubCommand> getCommands() {
        return commands;
    }
}
