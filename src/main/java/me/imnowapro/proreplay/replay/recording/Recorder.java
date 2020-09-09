package me.imnowapro.proreplay.replay.recording;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;
import me.imnowapro.proreplay.ProReplay;
import me.imnowapro.proreplay.file.ReplayWriter;
import me.imnowapro.proreplay.replay.PacketData;
import me.imnowapro.proreplay.replay.ReplayMeta;
import me.imnowapro.proreplay.replay.converter.PacketConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

public class Recorder extends PacketAdapter implements Listener {

  private final ReplayMeta meta;
  private final Player recordedPlayer;
  private long startTime = 0;

  private boolean recording = false;
  private final boolean writeDirectly;
  private ReplayWriter writer;
  private LinkedList<PacketData> recordedPackets = new LinkedList<>();

  public Recorder(String name, Player recordedPlayer, boolean writeDirectly) {
    super(ProReplay.getInstance(), ListenerPriority.MONITOR, PacketConverter.getPacketTypes());
    this.meta = new ReplayMeta(name,
        0, new Date().getTime(),
        ProtocolLibrary.getProtocolManager().getMinecraftVersion().getVersion(),
        ProtocolLibrary.getProtocolManager().getProtocolVersion(recordedPlayer));
    this.meta.getPlayers().add(recordedPlayer.getUniqueId().toString());
    this.recordedPlayer = recordedPlayer;
    this.writeDirectly = writeDirectly;
    try {
      this.writer = new ReplayWriter(new File(ProReplay.getInstance().getReplayFolder(),
          name + ".mcpr"));
    } catch (IOException e) {
      ProReplay.getInstance().getLogger().log(Level.WARNING, "Failed to create ReplayWriter.", e);
    }
    ProReplay.getInstance().getRecorders().put(recordedPlayer, this);
    ProtocolLibrary.getProtocolManager().addPacketListener(this);
    Bukkit.getPluginManager().registerEvents(this, ProReplay.getInstance());
  }

  @Override
  public void onPacketReceiving(PacketEvent event) {
    if (this.recording && event.getPlayer() == this.recordedPlayer) {
      if (event.getPacketType() == PacketType.Play.Client.ARM_ANIMATION) {
        savePacket(ProReplay.getInstance().getPacketConverter()
            .convertAnimationPacket(event.getPlayer(), event.getPacket()));
      }
    }
  }

  @Override
  public void onPacketSending(PacketEvent event) {
    if (this.recording && event.getPlayer() == this.recordedPlayer) {
      savePacket(event.getPacket());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onJoin(PlayerJoinEvent event) {
    this.meta.getPlayers().add(event.getPlayer().getUniqueId().toString());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onRespawn(PlayerRespawnEvent event) {
    if (this.recording && event.getPlayer() == this.recordedPlayer) {
      savePacket(ProReplay.getInstance().getPacketConverter()
          .createPlayerSpawnPacket(event.getPlayer(), event.getRespawnLocation()));
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onMove(PlayerMoveEvent event) {
    if (this.recording && event.getPlayer() == this.recordedPlayer) {
      if (event.getFrom().getYaw() != event.getTo().getYaw()) {
        savePacket(ProReplay.getInstance().getPacketConverter()
            .createHeadRotationPacket(event.getPlayer(), event.getTo().getYaw()));
      }
      Vector move = event.getTo().clone().subtract(event.getFrom()).toVector();
      if (PacketUtil.differentLocation(event.getFrom(), event.getTo())) {
        savePacket(ProReplay.getInstance().getPacketConverter()
            .createPositionLookPacket(event.getPlayer(), move,
                event.getTo().getYaw(), event.getTo().getPitch()));
      } else if (PacketUtil.hasRotated(event.getFrom(), event.getTo())) {
        savePacket(ProReplay.getInstance().getPacketConverter()
            .createLookPacket(event.getPlayer(), event.getTo().getYaw(), event.getTo().getPitch()));
      } else {
        savePacket(ProReplay.getInstance().getPacketConverter()
            .createPositionPacket(event.getPlayer(), move));
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onTeleport(PlayerTeleportEvent event) {
    if (this.recording && event.getPlayer() == this.recordedPlayer) {
      savePacket(ProReplay.getInstance().getPacketConverter()
          .createTeleportPacket(event.getPlayer(), event.getTo()));
    }
  }

  public void start() {
    this.startTime = System.currentTimeMillis();
    savePacket(ProReplay.getInstance().getPacketConverter()
        .createLoginSuccessPacket(this.recordedPlayer));
    savePacket(ProReplay.getInstance().getPacketConverter()
        .createLoginPacket(this.recordedPlayer));
    savePacket(ProReplay.getInstance().getPacketConverter()
        .createPositionPacket(this.recordedPlayer.getLocation()));
    savePacket(ProReplay.getInstance().getPacketConverter()
        .createPlayerListItemPacket(this.recordedPlayer));
    this.recording = true;
    savePacket(ProReplay.getInstance().getPacketConverter()
        .createPlayerSpawnPacket(this.recordedPlayer));
    savePacket(ProReplay.getInstance().getPacketConverter()
        .createTeleportPacket(this.recordedPlayer, this.recordedPlayer.getLocation()));
  }

  public void stopAndSave() {
    this.recording = false;
    this.meta.setDuration(System.currentTimeMillis() - this.startTime);
    try {
      if (!this.writeDirectly) {
        for (PacketData packetData : this.recordedPackets) {
          this.writer.writePacket(packetData);
        }
      }
      this.writer.writeMetaAndClose(this.meta);
    } catch (IOException e) {
      ProReplay.getInstance().getLogger().log(Level.WARNING, "Failed to write replay.", e);
    }
    HandlerList.unregisterAll(this);
    ProtocolLibrary.getProtocolManager().removePacketListener(this);
  }

  private void savePacket(PacketContainer packet) {
    PacketData packetData = new PacketData((int) (System.currentTimeMillis() - this.startTime),
        packet);
    if (this.writeDirectly) {
      try {
        this.writer.writePacket(packetData);
      } catch (IOException e) {
        ProReplay.getInstance().getLogger().log(Level.WARNING, "Failed to write packet.", e);
      }
    } else {
      this.recordedPackets.add(packetData);
    }
  }

  public ReplayMeta getMeta() {
    return this.meta;
  }

  public Player getRecordedPlayer() {
    return this.recordedPlayer;
  }

  public LinkedList<PacketData> getRecordedPackets() {
    return this.recordedPackets;
  }
}
