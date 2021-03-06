package me.imnowapro.proreplay.replay;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.injector.netty.WirePacket;
import java.io.Serializable;
import me.imnowapro.proreplay.ProReplay;

public class PacketData extends WirePacket implements Serializable {

  private final int time;

  public PacketData(int time, PacketContainer packet) {
    super(ProReplay.getInstance().getPacketConverter().getPacketID(packet.getType()),
        WirePacket.bytesFromPacket(packet));
    this.time = time;
  }

  public PacketData(int time, int id, byte[] data) {
    super(id, data);
    this.time = time;
  }

  public PacketType getType() {
    return ProReplay.getInstance().getPacketConverter().getPacketType(getId());
  }

  public int getTime() {
    return this.time;
  }
}
