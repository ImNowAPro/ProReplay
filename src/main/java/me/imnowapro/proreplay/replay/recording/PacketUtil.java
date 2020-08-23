package me.imnowapro.proreplay.replay.recording;

import java.io.ByteArrayOutputStream;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class PacketUtil {

  public static int toFixedPointNumber(double old) {
    return (int) Math.round(old * 32D);
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

  public static boolean differentLocation(Location one, Location two) {
    return (hasMoved(one, two) && hasRotated(one, two));
  }

  public static boolean hasMoved(Vector move) {
    return (move.getX() > 0
        || move.getY() > 0
        || move.getZ() > 0);
  }

  public static boolean hasMoved(Location one, Location two) {
    return (one.getX() != two.getX()
        || one.getY() != two.getY()
        || one.getZ() != two.getZ());
  }

  public static boolean hasRotated(Location one, Location two) {
    return (one.getYaw() != two.getYaw()
        || one.getPitch() != two.getPitch());
  }
}
