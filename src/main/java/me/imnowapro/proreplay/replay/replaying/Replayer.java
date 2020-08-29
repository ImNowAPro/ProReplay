package me.imnowapro.proreplay.replay.replaying;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import me.imnowapro.proreplay.ProReplay;
import me.imnowapro.proreplay.replay.PacketData;
import me.imnowapro.proreplay.replay.Replay;
import me.imnowapro.proreplay.replay.converter.PacketConverter;
import me.imnowapro.proreplay.util.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class Replayer extends PacketAdapter implements Listener, Runnable {

  private final Replay replay;
  private Player watcher;

  private boolean replaying = false;
  private boolean slow = false;
  private double currentTick = 1.0;
  private int currentPacket = 2;

  public Replayer(Replay replay, Player replayingPlayer) {
    super(ProReplay.getInstance(),
        ListenerPriority.LOWEST,
        PacketConverter.getPacketTypes(true, true, true));
    this.replay = replay;
    setWatcher(replayingPlayer);
  }

  @Override
  public void run() {
    if (this.replaying) {
      executeTick(this.currentTick);
      if (this.currentTick < getDurationTicks()) {
        if (this.currentTick % 20 == 0) {
          updateXPBar();
        }
        this.currentTick += (this.slow ? 0.5 : 1.0);
        if (this.currentTick == getDurationTicks()) {
          updateXPBar();
        }
      }
    }
  }

  private void executeTick(double tick) {
    PacketData packet;
    while ((packet = this.replay.getPacket(this.currentPacket)) != null
        && packet.getTime() <= tick * 50) {
      for (Player player : Bukkit.getOnlinePlayers()) {
        try {
          ProtocolLibrary.getProtocolManager().sendWirePacket(player, packet);
        } catch (InvocationTargetException e) {
          ProReplay.getInstance().getLogger().log(Level.WARNING, "Failed to send packet.", e);
        }
      }
      this.currentPacket++;
    }
  }

  @Override
  public void onPacketSending(PacketEvent event) {
    if (this.replaying) {
      event.setCancelled(true);
    }
  }

  @Override
  public void onPacketReceiving(PacketEvent event) {
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    event.getPlayer().setGameMode(GameMode.ADVENTURE);
    event.getPlayer().setAllowFlight(true);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onDrop(PlayerDropItemEvent event) {
    event.setCancelled(true);
  }

  @EventHandler
  public void onInteract(PlayerInteractEvent event) {
    ItemStack item = event.getItem();
    if (item != null && !event.getAction().equals(Action.PHYSICAL)) {
      if (item.getItemMeta().getDisplayName().endsWith("Backwards")) {
        reset();
      } else if (item.getItemMeta().getDisplayName().endsWith("Pause/Play")) {
        this.replaying = !this.replaying;
      } else if (item.getItemMeta().getDisplayName().endsWith("Skip")) {
        skip(60);
      } else if (item.getItemMeta().getDisplayName().endsWith("Slow")) {
        this.slow = !this.slow;
      }
    }
  }

  public void start() {
    this.replaying = true;
  }

  public void skip(int ticks) {
    ticks = Math.min((int) Math.round(getDurationTicks() - this.currentTick), ticks);
    if (ticks > 0) {
      executeTick(this.currentTick + ticks);
      this.currentTick += ticks;
    }
    updateXPBar();
  }

  public void reset() {
    this.currentPacket = 3;
    this.currentTick = 1;
    updateXPBar();
  }

  private void updateXPBar() {
    Bukkit.getOnlinePlayers().forEach(player -> {
      player.setLevel((int) Math.round(this.currentTick / 20));
      player.setExp((float) (this.currentTick / getDurationTicks()));
    });
  }

  private double getDurationTicks() {
    return Math.ceil(((float) this.replay.getMeta().getDuration()) / 50);
  }

  public void setWatcher(Player watcher) {
    if (this.watcher != null && this.watcher.isOnline()) {
      this.watcher.getInventory().clear();
    }
    this.watcher = watcher;
    watcher.getPlayer().setGameMode(GameMode.ADVENTURE);
    watcher.getPlayer().setAllowFlight(true);
    watcher.getInventory().clear();
    watcher.getInventory().setItem(3, new ItemStackBuilder(Material.STAINED_CLAY)
        .setDisplayName(ChatColor.RED + "Backwards")
        .setDyeColor(DyeColor.RED)
        .build());
    watcher.getInventory().setItem(4, new ItemStackBuilder(Material.STAINED_CLAY)
        .setDisplayName(ChatColor.GOLD + "Pause/Play")
        .setDyeColor(DyeColor.ORANGE)
        .build());
    watcher.getInventory().setItem(5, new ItemStackBuilder(Material.STAINED_CLAY)
        .setDisplayName(ChatColor.GREEN + "Forward")
        .setDyeColor(DyeColor.GREEN)
        .build());
    watcher.getInventory().setItem(7, new ItemStackBuilder(Material.SLIME_BALL)
        .setDisplayName(ChatColor.BLUE + "Slow")
        .build());
  }
}
