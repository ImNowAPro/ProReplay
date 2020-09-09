package me.imnowapro.proreplay.command;

import java.text.SimpleDateFormat;
import java.util.Date;
import me.imnowapro.proreplay.ProReplay;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReplayCommand implements CommandExecutor {

  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (args.length >= 1) {
      if (args[0].equalsIgnoreCase("save")) {
        if (args.length >= 2) {
          Player target = Bukkit.getPlayerExact(args[1]);
          if (target != null) {
            if (ProReplay.getInstance().getRecorders().containsKey(target)) {
              ProReplay.getInstance().getRecorders().get(target).stopAndSave();
              ProReplay.getInstance().getRecorders().remove(target);
              sender.sendMessage(ProReplay.PREFIX + "§7Recording successfully §astopped§7.");
              return true;
            } else {
              sender.sendMessage(ProReplay.PREFIX + "§7The player isn't recorded.");
              return false;
            }
          } else {
            sender.sendMessage(ProReplay.PREFIX + "§7The player §cdoesn't exists§7.");
            return false;
          }
        } else {
          sender.sendMessage(ProReplay.PREFIX + "§7Usage§8: §c/replay save <player>");
          return false;
        }
      } else if (args[0].equalsIgnoreCase("list")) {
        sender.sendMessage(ProReplay.PREFIX + "§7List of replays§8:");
        ProReplay.getInstance().getReplays().forEach(replay ->
            sender.sendMessage("§9" + replay.getName()
                + " §8- §7" + DATE_FORMAT.format(new Date(replay.getDate()))
                + " §8- §7" + (replay.getDuration() / 1000) + "s"));
        sender.sendMessage("");
        return true;
      }
    }
    sendHelpMessage(sender);
    return false;
  }

  public void sendHelpMessage(CommandSender sender) {
    sender.sendMessage(ProReplay.PREFIX + "§7Command overview§8:");
    sender.sendMessage("§9/replay save §8- §7Save a replay.");
    sender.sendMessage("§9/replay list §8- §7Lists all replays.");
  }
}
