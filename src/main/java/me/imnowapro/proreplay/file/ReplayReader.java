package me.imnowapro.proreplay.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import me.imnowapro.proreplay.ProReplay;
import me.imnowapro.proreplay.replay.PacketData;
import me.imnowapro.proreplay.replay.Replay;

public class ReplayReader {

  private final ZipInputStream inputStream;

  public ReplayReader(File file) throws IOException {
    this.inputStream = new ZipInputStream(new FileInputStream(file));
  }

  public Replay readAndClose() throws IOException {
    Map<String, Object> metaData = new HashMap<>();
    LinkedList<PacketData> packets = new LinkedList<>();
    ZipEntry entry;
    while ((entry = this.inputStream.getNextEntry()) != null) {
      if (entry.getName().equals("recording.tmcpr")) {
        while (this.inputStream.available() >= Integer.BYTES * 2) {
          ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 2);
          this.inputStream.read(buffer.array());
          byte[] data = new byte[buffer.getInt(4)];
          this.inputStream.read(data);
          packets.add(new PacketData(buffer.getInt(0), data));
        }
      } else if (entry.getName().equals("metaData.json")) {
        byte[] buffer = new byte[this.inputStream.available()];
        this.inputStream.read(buffer);
        metaData = ProReplay.GSON.fromJson(new String(buffer), Map.class);
      }
    }
    this.inputStream.close();
    return new Replay(metaData, packets);
  }
}
