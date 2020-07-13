package me.imnowapro.proreplay.replay;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import me.imnowapro.proreplay.replay.recording.Recorder;

public class Replay {

  private final Map<String, Object> metaData;
  private final LinkedList<PacketData> packets;

  public Replay(Map<String, Object> metaData, LinkedList<PacketData> packets) {
    this.metaData = metaData;
    this.packets = packets;
  }

  public Replay(Recorder recorder) {
    this(new HashMap<>(), recorder.getRecordedPackets());
    this.metaData.put("singleplayer", false);
    this.metaData.put("generator", "ProReplay Generator");
    this.metaData.put("duration", this.packets.getLast().getTime());
    this.metaData.put("date", recorder.getStartTime());
    this.metaData.put("mcversion", recorder.getVersion());
    this.metaData.put("protocol", recorder.getProtocolVersion());
    this.metaData.put("fileFormatVersion", 14);
    this.metaData.put("players",
        Collections.singleton(recorder.getRecordedPlayer().getUniqueId().toString()));
  }

  public Map<String, Object> getMetaData() {
    return this.metaData;
  }

  public LinkedList<PacketData> getPackets() {
    return this.packets;
  }
}
