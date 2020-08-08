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

public class Recorder extends PacketAdapter {

  private final Player recordedPlayer;
  private long startTime = new Date().getTime();
  private LinkedList<PacketData> recordedPackets = new LinkedList<>();

  private int recordedChunks = 0;

  public Recorder(Player recordedPlayer) {
    super(ProReplay.getInstance(), ListenerPriority.LOWEST,
        Stream.of(PacketType.Play.Client.POSITION, PacketType.Play.Client.POSITION_LOOK,
            PacketType.Play.Client.LOOK, PacketType.Play.Server.REL_ENTITY_MOVE,
            PacketType.Play.Server.REL_ENTITY_MOVE_LOOK, PacketType.Play.Server.ENTITY_LOOK,
            PacketType.Play.Server.MAP_CHUNK, PacketType.Play.Server.MAP_CHUNK_BULK,
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
    if (event.getPlayer().equals(this.recordedPlayer)) {
      if (event.getPacketType().equals(PacketType.Play.Client.POSITION)) {
        savePacket(ProReplay.getInstance().getPacketConverter()
            .convertPositionPacket(event.getPlayer(), event.getPacket()));
      } else if (event.getPacketType().equals(PacketType.Play.Client.POSITION_LOOK)) {
        savePacket(ProReplay.getInstance().getPacketConverter()
            .convertPositionLookPacket(event.getPlayer(), event.getPacket()));
      } else if (event.getPacketType().equals(PacketType.Play.Client.LOOK)) {
        savePacket(ProReplay.getInstance().getPacketConverter()
            .convertLookPacket(event.getPlayer(), event.getPacket()));
      }
    }
  }

  @Override
  public void onPacketSending(PacketEvent event) {
    if (event.getPlayer().equals(this.recordedPlayer)) {
      savePacket(event.getPacket());
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
    ProtocolLibrary.getProtocolManager().addPacketListener(this);
    savePacket(ProReplay.getInstance().getPacketConverter()
        .createPlayerSpawnPacket(this.recordedPlayer));
  }

  public void stop() {
    ProtocolLibrary.getProtocolManager().removePacketListener(this);
  }

  private void savePacket(PacketContainer packet) {
    if ((packet.getType().equals(PacketType.Play.Server.MAP_CHUNK)
        || packet.getType().equals(PacketType.Play.Server.MAP_CHUNK_BULK))
        && this.recordedPackets.size() - 3 < 49) {
      this.recordedPackets.add(this.recordedChunks + 4,
          new PacketData(this.recordedChunks + 5, packet));
      this.recordedChunks++;
    } else if (packet.getType().equals(PacketType.Login.Server.SUCCESS)
        || packet.getType().equals(PacketType.Play.Server.LOGIN)
        || packet.getType().equals(PacketType.Play.Server.PLAYER_INFO)
        || packet.getType().equals(PacketType.Play.Server.POSITION)) {
      this.recordedPackets.add(new PacketData(this.recordedPackets.size() + 1, packet));
    } else {
      recordedPackets.add(new PacketData((int) (System.currentTimeMillis() - this.startTime),
          packet));
    }
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
