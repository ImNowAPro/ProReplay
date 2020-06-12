package me.imnowapro.proreplay.replay;

import com.comphenix.protocol.injector.netty.WirePacket;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class PacketData implements Serializable {

  private final ByteBuffer data;

  public PacketData(int time, WirePacket packet) {
    this.data = ByteBuffer.allocate(Integer.BYTES * 3 + packet.getBytes().length)
        .putInt(time)
        .putInt(Integer.BYTES + packet.getBytes().length)
        .putInt(packet.getId())
        .put(packet.getBytes());
  }

  public PacketData(int time, byte[] data) {
    this.data = ByteBuffer.allocate(Integer.BYTES * 2 + data.length)
        .putInt(time)
        .putInt(data.length)
        .put(data);
  }

  public int getTime() {
    return this.data.getInt(0);
  }

  public int getID() {
    return this.data.getInt(4);
  }

  public byte[] serialize() {
    return this.data.array();
  }
}
