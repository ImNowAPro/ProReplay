package me.imnowapro.proreplay.replay;

import com.comphenix.protocol.injector.netty.WirePacket;
import java.io.Serializable;
import java.nio.ByteBuffer;
import me.imnowapro.proreplay.replay.recording.converter.PacketUtil;

public class PacketData implements Serializable {

  private final ByteBuffer data;

  public PacketData(int time, WirePacket packet) {
    byte[] packetData = ByteBuffer
        .allocate(PacketUtil.varIntSize(packet.getId()) + packet.getBytes().length)
        .put(PacketUtil.toVarInt(packet.getId()))
        .put(packet.getBytes())
        .array();
    this.data = ByteBuffer.allocate(Integer.BYTES * 2 + packetData.length)
        .putInt(time)
        .putInt(packetData.length)
        .put(packetData);
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
    int numRead = 0;
    int result = 0;
    byte read;
    do {
      read = this.data.get(8 + numRead);
      int value = (read & 0b01111111);
      result |= (value << (7 * numRead));
      numRead++;
      if (numRead > 5) {
        throw new RuntimeException("Packet-ID is too big");
      }
    } while ((read & 0b10000000) != 0);
    return result;
  }

  public byte[] serialize() {
    return this.data.array();
  }

}
