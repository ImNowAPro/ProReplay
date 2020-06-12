package me.imnowapro.proreplay.replay.recording;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.netty.WirePacket;
import java.util.Date;
import java.util.LinkedList;
import me.imnowapro.proreplay.ProReplay;
import me.imnowapro.proreplay.replay.PacketData;
import org.bukkit.entity.Player;

public class Recorder extends PacketAdapter {

  private final Player recordedPlayer;
  private long startTime = new Date().getTime();
  private LinkedList<PacketData> recordedPackets = new LinkedList<>();

  public Recorder(Player recordedPlayer) {
    super(ProReplay.getInstance(), ListenerPriority.LOWEST,
        PacketType.Play.Client.POSITION, PacketType.Play.Client.POSITION_LOOK,
        PacketType.Play.Client.LOOK, PacketType.Play.Server.REL_ENTITY_MOVE,
        PacketType.Play.Server.REL_ENTITY_MOVE_LOOK, PacketType.Play.Server.ENTITY_LOOK,
        PacketType.Play.Server.MAP_CHUNK, PacketType.Play.Server.MAP_CHUNK_BULK,
        PacketType.Play.Server.MAP);
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

  public void record() {
    this.startTime = System.currentTimeMillis();
    savePacket(ProReplay.getInstance().getPacketConverter()
        .createPlayerListItemPacket(this.recordedPlayer));
    savePacket(ProReplay.getInstance().getPacketConverter()
        .createPlayerSpawnPacket(this.recordedPlayer));
    ProtocolLibrary.getProtocolManager().addPacketListener(this);
  }

  public void stop() {
    ProtocolLibrary.getProtocolManager().removePacketListener(this);
  }

  private void savePacket(PacketContainer packet) {
    recordedPackets.add(new PacketData((int) (System.currentTimeMillis() - this.startTime),
        WirePacket.fromPacket(packet)));
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
}
