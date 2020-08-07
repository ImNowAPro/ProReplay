package me.imnowapro.proreplay.replay.recording;

import java.io.ByteArrayOutputStream;

public class PacketUtil {

  public static byte toFixedPointNumber(double old) {
    return (byte) (old * 32);
  }

  public static byte toAngle(float old) {
    return (byte) (old * 256F / 360F);
  }

  public static byte[] toVarInt(int value) {
    ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
    do {
      byte temp = (byte) (value & 0b01111111);
      value >>>= 7;
      if (value != 0) {
        temp |= 0b10000000;
      }
      arrayOutputStream.write(temp);
    } while (value != 0);
    return arrayOutputStream.toByteArray();
  }

  public static int varIntSize(int value) {
    int result = 0;
    do {
      value >>>= 7;
      result++;
    } while (value != 0);
    return result;
  }

  public static byte[] toByteArray(int value) {
    return new byte[] {
        (byte) (value >> 24),
        (byte) (value >> 16),
        (byte) (value >> 8),
        (byte) value};
  }
}
