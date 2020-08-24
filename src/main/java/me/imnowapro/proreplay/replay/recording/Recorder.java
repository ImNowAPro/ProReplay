package me.imnowapro.proreplay.replay.recording;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.imnowapro.proreplay.ProReplay;
import me.imnowapro.proreplay.replay.PacketData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

public class Recorder extends PacketAdapter implements Listener {

  private final Player recordedPlayer;
  private final Collection<Player> players = new HashSet<>(Bukkit.getOnlinePlayers());
  private boolean recording = false;
  private final long date = new Date().getTime();
  private long startTime = 0;
  private final LinkedList<PacketData> recordedPackets = new LinkedList<>();

  public Recorder(Player recordedPlayer) {
    super(ProReplay.getInstance(), ListenerPriority.MONITOR,
        Stream.of(PacketType.Play.Client.ARM_ANIMATION, PacketType.Play.Server.REL_ENTITY_MOVE,
            PacketType.Play.Server.REL_ENTITY_MOVE_LOOK, PacketType.Play.Server.ENTITY_LOOK,
            PacketType.Play.Server.MAP_CHUNK, PacketType.Play.Server.MAP_CHUNK_BULK,
            PacketType.Play.Server.LIGHT_UPDATE, PacketType.Play.Server.WORLD_BORDER,
            PacketType.Play.Server.WORLD_PARTICLES, PacketType.Play.Server.WORLD_EVENT,
            PacketType.Play.Server.NAMED_SOUND_EFFECT, PacketType.Play.Server.LOOK_AT,
            PacketType.Play.Server.VIEW_CENTRE, PacketType.Play.Server.UPDATE_TIME,
            PacketType.Play.Server.COLLECT, PacketType.Play.Server.NAMED_ENTITY_SPAWN,
            PacketType.Play.Server.SPAWN_ENTITY, PacketType.Play.Server.SPAWN_ENTITY_EXPERIENCE_ORB,
            PacketType.Play.Server.SPAWN_ENTITY_LIVING, PacketType.Play.Server.SPAWN_ENTITY_WEATHER,
            PacketType.Play.Server.SPAWN_ENTITY_PAINTING, PacketType.Play.Server.ENTITY,
            PacketType.Play.Server.ENTITY_VELOCITY, PacketType.Play.Server.ENTITY_TELEPORT,
            PacketType.Play.Server.ENTITY_STATUS, PacketType.Play.Server.ATTACH_ENTITY,
            PacketType.Play.Server.ENTITY_EFFECT, PacketType.Play.Server.REMOVE_ENTITY_EFFECT,
            PacketType.Play.Server.ENTITY_EQUIPMENT, PacketType.Play.Server.EXPLOSION,
            PacketType.Play.Server.ENTITY_METADATA, PacketType.Play.Server.ENTITY_DESTROY,
            PacketType.Play.Server.ENTITY_SOUND, PacketType.Play.Server.UNLOAD_CHUNK,
            PacketType.Play.Server.MULTI_BLOCK_CHANGE, PacketType.Play.Server.BLOCK_CHANGE,
            PacketType.Play.Server.BLOCK_ACTION, PacketType.Play.Server.BLOCK_ACTION,
            PacketType.Play.Server.UPDATE_SIGN, PacketType.Play.Server.BLOCK_BREAK_ANIMATION,
            PacketType.Play.Server.GAME_STATE_CHANGE)
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

  @EventHandler(priority = EventPriority.MONITOR)
  public void onJoin(PlayerJoinEvent event) {
    this.players.add(event.getPlayer());
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

  public Collection<Player> getPlayers() {
    return this.players;
  }

  public long getDate() {
    return this.date;
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
