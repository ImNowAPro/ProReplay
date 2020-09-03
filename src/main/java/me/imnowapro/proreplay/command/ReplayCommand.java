package me.imnowapro.proreplay.command;

import me.imnowapro.proreplay.ProReplay;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReplayCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (args.length >= 1) {
      if (args[0].equalsIgnoreCase("save")) {
        if (args.length >= 2) {
          Player target = Bukkit.getPlayer(args[1]);
          if (target != null) {
            if (ProReplay.getInstance().getRecorder().containsKey(target)) {
              ProReplay.getInstance().getRecorder().get(target).stop();
              ProReplay.getInstance().getRecorder().remove(target);
              sender.sendMessage(ProReplay.PREFIX + "§7Recording successfully §astopped§7.");
              return true;
            } else {
              sender.sendMessage(ProReplay.PREFIX + "§7The player isn't recorded.");
            }
          } else {
            sender.sendMessage(ProReplay.PREFIX + "§7The player §cdoesn't exists§7.");
          }
        } else {
          sender.sendMessage(ProReplay.PREFIX + "§7Usage§8: §c/replay save <player>");
        }
      } else {
        sendHelpMessage(sender);
      }
    } else {
      sendHelpMessage(sender);
    }
    return false;
  }

  public void sendHelpMessage(CommandSender sender) {
    sender.sendMessage(ProReplay.PREFIX + "§7Command overview§8:");
    sender.sendMessage(ProReplay.PREFIX + "§9/replay save §8- §7Save a replay.");
  }
}
