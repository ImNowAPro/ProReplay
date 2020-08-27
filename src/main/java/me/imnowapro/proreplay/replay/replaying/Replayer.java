package me.imnowapro.proreplay.replay.replaying;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Level;
import me.imnowapro.proreplay.ProReplay;
import me.imnowapro.proreplay.replay.PacketData;
import me.imnowapro.proreplay.replay.Replay;
import me.imnowapro.proreplay.replay.converter.PacketConverter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Replayer extends PacketAdapter implements Listener, Runnable {

  private final Replay replay;
  private final Player replayingPlayer;
  private final Collection<Player> viewer;

  private boolean replaying = false;
  private int currentTick = 0;
  private int currentPacket = 1;

  public Replayer(Replay replay, Player replayingPlayer) {
    super(ProReplay.getInstance(),
        ListenerPriority.LOWEST,
        PacketConverter.getPacketTypes(true, true, true));
    this.replay = replay;
    this.replayingPlayer = replayingPlayer;
    this.viewer = new HashSet<>(Collections.singleton(replayingPlayer));
  }

  @Override
  public void run() {
    if (this.replaying && this.currentPacket < this.replay.getPackets().size()) {
      PacketData packet = this.replay.getPackets().get(this.currentPacket);
      while (this.currentPacket < this.replay.getPackets().size()
          && packet.getTime() <= this.currentTick * 50) {
        for (Player player : this.viewer) {
          try {
            ProtocolLibrary.getProtocolManager().sendWirePacket(player, packet);
          } catch (InvocationTargetException e) {
            ProReplay.getInstance().getLogger().log(Level.WARNING, "Failed to send packet.", e);
          }
        }
        this.currentPacket++;
        packet = this.replay.getPackets().get(this.currentPacket);
      }
      if (this.currentPacket < this.replay.getPackets().size()) {
        this.currentTick++;
      }
    }
  }

  @Override
  public void onPacketSending(PacketEvent event) {
    if (this.replaying && this.viewer.contains(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  public void start() {
    this.replaying = true;
  }
}
