package me.zhanshi123.vipsystem.command.sub;

import me.zhanshi123.vipsystem.Main;
import me.zhanshi123.vipsystem.api.VipSystemAPI;
import me.zhanshi123.vipsystem.api.vip.VipData;
import me.zhanshi123.vipsystem.command.SubCommand;
import me.zhanshi123.vipsystem.command.tab.CommandTab;
import me.zhanshi123.vipsystem.command.tab.TabCompletable;
import me.zhanshi123.vipsystem.command.type.PermissionCommand;
import me.zhanshi123.vipsystem.manager.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GiveCommand extends SubCommand implements PermissionCommand, TabCompletable {
    public GiveCommand() {
        super("give", MessageManager.getString("Command.give.usage"), MessageManager.getString("Command.give.desc"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String playerName = args[1];
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            sender.sendMessage(MessageManager.getString("playerNotFound"));
            return true;
        }
        long temp = VipSystemAPI.getInstance().getTimeMillis(args[3]);
        if (temp == 0 || (temp >= 1 && temp < 60000)) {
            sender.sendMessage(MessageManager.getString("Command.give.invalidTime"));
            return true;
        }
        VipData vipData = VipSystemAPI.getInstance().getVipManager().getVipData(player);
        if (vipData != null) {
            if (!vipData.getVip().equalsIgnoreCase(args[2])) {
                if (vipData.getDuration() != -1) {
//                    玩家已经有拥有别的vip 需要将原来的vip存储
                    VipSystemAPI.getInstance().getVipStorageManager().store(player);
                    vipData = new VipData(player, args[2], temp);
                    VipSystemAPI.getInstance().getVipManager().addVip(player, vipData);
                    sender.sendMessage(MessageManager.getString("Command.give.success"));
                    return true;
                }
                sender.sendMessage(MessageManager.getString("Command.give.alreadyHaveVip"));
                return true;
            }
            VipSystemAPI.getInstance().getVipManager().renewVip(player, temp);
        } else {
            vipData = new VipData(player, args[2], temp);
            VipSystemAPI.getInstance().getVipManager().addVip(player, vipData);
        }
        sender.sendMessage(MessageManager.getString("Command.give.success"));
        return true;
    }

    @Override
    public List<CommandTab> getArguments() {
        return Arrays.asList(new CommandTab[]{
                () -> VipSystemAPI.getInstance().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()),
                () -> Arrays.asList(Main.getPermission().getGroups()),
                () -> Arrays.asList("7d", "30d", "180d", "-1")
        });
    }
}
