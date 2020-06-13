package me.imnowapro.proreplay.replay;

import com.comphenix.protocol.injector.netty.WirePacket;
import com.google.common.base.Preconditions;
import me.imnowapro.proreplay.replay.recording.converter.PacketUtil;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class PacketData implements Serializable {

    private final ByteBuffer data;

    public PacketData(int time, WirePacket packet) {
        byte[] packetData = ByteBuffer.allocate(PacketUtil.varIntSize(packet.getId()) + packet.getBytes().length)
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
        return this.data.getInt(4);
    }

    public byte[] serialize() {
        return this.data.array();
    }


}
