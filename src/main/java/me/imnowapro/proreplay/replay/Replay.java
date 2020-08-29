package me.imnowapro.proreplay.replay;

import java.util.LinkedList;
import java.util.Random;
import me.imnowapro.proreplay.replay.recording.Recorder;

public class Replay {

  private final ReplayMeta meta;
  private final LinkedList<PacketData> packets;

  public Replay(ReplayMeta meta, LinkedList<PacketData> packets) {
    this.meta = meta;
    this.packets = packets;
  }

  public Replay(Recorder recorder) {
    this(recorder.getMeta(), recorder.getRecordedPackets());
  }

  public PacketData getPacket(int index) {
    if (index >= 0 && index < this.packets.size()) {
      return this.packets.get(index);
    }
    return null;
  }

  public ReplayMeta getMeta() {
    return this.meta;
  }

  public static String getRandomName() {
    return getRandomName(12);
  }

  public static String getRandomName(int length) {
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    Random random = new Random();
    StringBuilder name = new StringBuilder();
    for (int i = 0; i < length; i++) {
      name.append(chars.toCharArray()[random.nextInt(chars.length())]);
    }
    return name.toString();
  }
}
