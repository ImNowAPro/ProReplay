package me.imnowapro.proreplay.replay.recording;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.imnowapro.proreplay.ProReplay;
import me.imnowapro.proreplay.replay.PacketData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class Recorder extends PacketAdapter implements Listener {

  private final Player recordedPlayer;
  private boolean recording = false;
  private long startTime = new Date().getTime();
  private final LinkedList<PacketData> recordedPackets = new LinkedList<>();

  public Recorder(Player recordedPlayer) {
    super(ProReplay.getInstance(), ListenerPriority.LOWEST,
        Stream.of(PacketType.Play.Client.ARM_ANIMATION, PacketType.Play.Server.REL_ENTITY_MOVE,
            PacketType.Play.Server.REL_ENTITY_MOVE_LOOK, PacketType.Play.Server.ENTITY_LOOK,
            PacketType.Play.Server.MAP_CHUNK, PacketType.Play.Server.MAP_CHUNK_BULK,
            PacketType.Play.Server.MULTI_BLOCK_CHANGE, PacketType.Play.Server.BLOCK_CHANGE,
            PacketType.Play.Server.LIGHT_UPDATE, PacketType.Play.Server.WORLD_BORDER,
            PacketType.Play.Server.SPAWN_POSITION, PacketType.Play.Server.LOOK_AT,
            PacketType.Play.Server.COMMANDS, PacketType.Play.Server.RECIPES,
            PacketType.Play.Server.TAGS, PacketType.Play.Server.VIEW_CENTRE)
            .filter(PacketType::isSupported)
            .collect(Collectors.toSet()));
    this.recordedPlayer = recordedPlayer;
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

  @EventHandler(priority = EventPriority.LOWEST)
  public void onMove(PlayerMoveEvent event) {
    if (this.recording && event.getPlayer() == this.recordedPlayer) {
      if (event.getFrom().getYaw() != event.getTo().getYaw()) {
        savePacket(ProReplay.getInstance().getPacketConverter()
            .createHeadRotationPacket(event.getPlayer(), event.getTo().getYaw()));
      }
      Vector move = event.getTo().toVector().subtract(event.getFrom().toVector());
      if (move.length() > 0 && (event.getFrom().getYaw() != event.getTo().getYaw()
          || event.getFrom().getPitch() != event.getTo().getPitch())) {
        savePacket(ProReplay.getInstance().getPacketConverter()
            .createPositionLookPacket(event.getPlayer(), move,
                event.getTo().getYaw(), event.getTo().getPitch()));
      } else if (event.getFrom().getYaw() != event.getTo().getYaw()
          || event.getFrom().getPitch() != event.getTo().getPitch()) {
        savePacket(ProReplay.getInstance().getPacketConverter()
            .createLookPacket(event.getPlayer(), event.getTo().getYaw(), event.getTo().getPitch()));
      } else if (move.length() > 0) {
        savePacket(ProReplay.getInstance().getPacketConverter()
            .createPositionPacket(event.getPlayer(), move));
      }
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
  }

  public void stop() {
    this.recording = false;
  }

  private void savePacket(PacketContainer packet) {
    recordedPackets.add(new PacketData((int) (System.currentTimeMillis() - this.startTime),
        packet));
  }

  public Player getRecordedPlayer() {
    return this.recordedPlayer;
  }

  public long getStartTime() {
    return this.startTime;
  }

  public LinkedList<PacketData> getRecordedPackets() {
    return this.recordedPackets;
  }

  public String getVersion() {
    return ProtocolLibrary.getProtocolManager().getMinecraftVersion().getVersion();
  }

  public int getProtocolVersion() {
    return ProtocolLibrary.getProtocolManager().getProtocolVersion(this.recordedPlayer);
  }
}
