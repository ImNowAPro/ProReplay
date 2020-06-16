package me.imnowapro.proreplay.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import me.imnowapro.proreplay.ProReplay;
import me.imnowapro.proreplay.replay.PacketData;
import me.imnowapro.proreplay.replay.Replay;
import me.imnowapro.proreplay.replay.recording.converter.PacketUtil;

public class ReplayWriter {

  private final ZipOutputStream outputStream;

  public ReplayWriter(File file) throws IOException {
    this.outputStream = new ZipOutputStream(new FileOutputStream(file));
  }

  public void writeAndClose(Replay replay) throws IOException {
    this.outputStream.putNextEntry(new ZipEntry("recording.tmcpr"));
    for (PacketData packetData : replay.getPackets()) {
      this.outputStream.write(ByteBuffer.allocate(Integer.BYTES * 2)
          .putInt(packetData.getTime())
          .putInt(PacketUtil.varIntSize(packetData.getId()) + packetData.getBytes().length)
          .array());
      this.outputStream.write(PacketUtil.toVarInt(packetData.getId()));
      this.outputStream.write(packetData.getBytes());
    }
    this.outputStream.closeEntry();
    this.outputStream.flush();
    this.outputStream.putNextEntry(new ZipEntry("metaData.json"));
    this.outputStream.write(ProReplay.GSON.toJson(replay.getMetaData(), Map.class).getBytes());
    this.outputStream.closeEntry();
    this.outputStream.flush();
    this.outputStream.close();
  }
}
