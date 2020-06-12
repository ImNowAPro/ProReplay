package me.imnowapro.proreplay.replay.recording.converter;

public class PacketUtil {

  public static byte toFixedPointNumber(double old) {
    return (byte) (old * 32);
  }

  public static byte toAngle(float old) {
    return (byte) (old * 256F / 360F);
  }
}
