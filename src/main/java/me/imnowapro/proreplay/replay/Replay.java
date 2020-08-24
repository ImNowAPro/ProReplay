package me.imnowapro.proreplay.replay;

import java.util.LinkedList;
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

  public ReplayMeta getMeta() {
    return this.meta;
  }

  public LinkedList<PacketData> getPackets() {
    return this.packets;
  }
}
