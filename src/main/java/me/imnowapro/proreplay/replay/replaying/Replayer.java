package me.imnowapro.proreplay.replay.replaying;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import me.imnowapro.proreplay.ProReplay;
import me.imnowapro.proreplay.replay.PacketData;
import me.imnowapro.proreplay.replay.Replay;
import me.imnowapro.proreplay.replay.converter.PacketConverter;
import me.imnowapro.proreplay.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Replayer extends PacketAdapter implements Listener, Runnable {

  private final Replay replay;
  private Player replayingPlayer;
  private final Collection<Player> viewer = new HashSet<>();

  private boolean replaying = false;
  private boolean slow = false;
  private int currentTick = 1;
  private int currentPacket = 2;

  public Replayer(Replay replay, Player replayingPlayer) {
    super(ProReplay.getInstance(),
        ListenerPriority.LOWEST,
        PacketConverter.getPacketTypes(true, true, true));
    this.replay = replay;
    setReplayingPlayer(replayingPlayer);
  }

  @Override
  public void run() {
    if (this.replaying) {
      onTick(this.currentTick);
      if (this.currentTick < getDurationTicks()) {
        this.currentTick++;
      }
    }
  }

  private void onTick(int tick) {
    PacketData packet;
    while ((packet = this.replay.getPacket(this.currentPacket)) != null
        && packet.getTime() <= tick * 50) {
      for (Player player : this.viewer) {
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
    if (this.replaying && this.viewer.contains(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @Override
  public void onPacketReceiving(PacketEvent event) {

  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onDrop(PlayerDropItemEvent event) {
    if (this.viewer.contains(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onInteract(PlayerInteractEvent event) {
    ItemStack item = event.getItem();
    if (item != null && (event.getAction().equals(Action.RIGHT_CLICK_AIR)
        || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
      if (item.getItemMeta().getDisplayName().equals(ChatColor.DARK_GRAY + "Stop/Play")) {
        this.replaying = !this.replaying;
      } else if (item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Skip")) {
        skip(60);
      }
    }
  }

  public void start() {
    addViewer(this.replayingPlayer);
    this.replaying = true;
  }

  public void skip(int ticks) {
    ticks = Math.min(getDurationTicks() - this.currentTick, ticks);
    if (ticks > 0) {
      onTick(this.currentTick + ticks);
      this.currentTick += ticks;
    }
  }

  private int getDurationTicks() {
    return (int) Math.ceil(this.replay.getMeta().getDuration() / 50);
  }

  public void setReplayingPlayer(Player replayingPlayer) {
    this.replayingPlayer = replayingPlayer;
    replayingPlayer.getInventory().clear();
    replayingPlayer.getInventory().setItem(4, new ItemStackBuilder(Material.SKULL_ITEM)
        .setDisplayName(ChatColor.DARK_GRAY + "Stop/Play")
        .build());
    replayingPlayer.getInventory().setItem(5, new ItemStackBuilder(Material.SKULL_ITEM)
        .setDisplayName(ChatColor.GREEN + "Skip")
        .build());
  }

  public void addViewer(Player player) {
    player.setGameMode(GameMode.ADVENTURE);
    player.setAllowFlight(true);
    this.viewer.add(player);
  }
}
