package me.imnowapro.proreplay.replay.recording.converter;

import java.nio.ByteBuffer;

public class PacketUtil {

  public static byte toFixedPointNumber(double old) {
    return (byte) (old * 32);
  }

  public static byte toAngle(float old) {
    return (byte) (old * 256F / 360F);
  }

  public static byte[] toVarInt(int value) {
    int size = varIntSize(value);
    ByteBuffer output = ByteBuffer.allocate(size);

    do {
      byte temp = (byte) (value & 0b01111111);
      value >>>= 7;
      if (value != 0) {
        temp |= 0b10000000;
      }
      output.put(temp);
    } while (value != 0);

    return output.array();
  }

  public static int varIntSize(int i) {
    int result = 0;
    do {
      result++;
      i >>>= 7;
    } while (i != 0);
    return result;
  }
}
