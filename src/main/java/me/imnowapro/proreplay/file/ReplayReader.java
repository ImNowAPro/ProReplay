package me.imnowapro.proreplay.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import me.imnowapro.proreplay.ProReplay;
import me.imnowapro.proreplay.replay.PacketData;
import me.imnowapro.proreplay.replay.Replay;
import me.imnowapro.proreplay.replay.ReplayMeta;
import me.imnowapro.proreplay.replay.recording.PacketUtil;

public class ReplayReader {

  private final ZipInputStream inputStream;

  public ReplayReader(File file) throws IOException {
    this.inputStream = new ZipInputStream(new FileInputStream(file));
  }

  public ReplayMeta readMetaAndClose() throws IOException {
    ZipEntry entry;
    while ((entry = this.inputStream.getNextEntry()) != null) {
      ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
      while (this.inputStream.available() == 1) {
        byteArrayStream.write(this.inputStream.read());
      }
      if (entry.getName().equals("metaData.json")) {
        ReplayMeta meta = ProReplay.GSON.fromJson(new String(byteArrayStream.toByteArray()),
            ReplayMeta.class);
        this.inputStream.close();
        return meta;
      }
    }
    close();
    return null;
  }

  public Replay readReplayAndClose() throws IOException {
    ReplayMeta meta = null;
    LinkedList<PacketData> packets = new LinkedList<>();
    ZipEntry entry;
    while ((entry = this.inputStream.getNextEntry()) != null) {
      ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
      while (inputStream.available() == 1) {
        byteArrayStream.write(this.inputStream.read());
      }
      ByteBuffer buffer = ByteBuffer.wrap(byteArrayStream.toByteArray());
      if (entry.getName().equals("recording.tmcpr")) {
        while (buffer.remaining() >= Integer.BYTES * 2) {
          int time = buffer.getInt();
          int length = buffer.getInt();
          if (length > 0) {
            int id = readVarInt(buffer);
            byte[] data = new byte[length - PacketUtil.varIntSize(id)];
            buffer.get(data);
            packets.add(new PacketData(time, id, data));
          }
        }
      } else if (entry.getName().equals("metaData.json")) {
        meta = ProReplay.GSON.fromJson(new String(buffer.array()), ReplayMeta.class);
      }
      this.inputStream.closeEntry();
    }
    close();
    return new Replay(meta, packets);
  }

  public void close() throws IOException {
    this.inputStream.close();
  }

  private static int readVarInt(ByteBuffer buffer) {
    int numRead = 0;
    int result = 0;
    byte read;
    do {
      read = buffer.get();
      int value = (read & 0b01111111);
      result |= (value << (7 * numRead));
      numRead++;
      if (numRead > 5) {
        throw new RuntimeException("VarInt is too big");
      }
    } while ((read & 0b10000000) != 0);
    return result;
  }
}
