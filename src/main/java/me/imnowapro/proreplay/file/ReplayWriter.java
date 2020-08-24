package me.imnowapro.proreplay.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import me.imnowapro.proreplay.ProReplay;
import me.imnowapro.proreplay.replay.PacketData;
import me.imnowapro.proreplay.replay.ReplayMeta;
import me.imnowapro.proreplay.replay.recording.PacketUtil;

public class ReplayWriter {

  private final ZipOutputStream outputStream;

  public ReplayWriter(File file) throws IOException {
    this.outputStream = new ZipOutputStream(new FileOutputStream(file));
    this.outputStream.putNextEntry(new ZipEntry("recording.tmcpr"));
  }

  public void writePacket(PacketData packetData) throws IOException {
    byte[] packetID = PacketUtil.toVarInt(packetData.getId());
    this.outputStream.write(PacketUtil.toByteArray(packetData.getTime()));
    this.outputStream.write(PacketUtil.toByteArray(packetID.length
        + packetData.getBytes().length));
    this.outputStream.write(packetID);
    this.outputStream.write(packetData.getBytes());
    this.outputStream.flush();
  }

  public void writeMetaAndClose(ReplayMeta meta) throws IOException {
    this.outputStream.closeEntry();
    this.outputStream.putNextEntry(new ZipEntry("metaData.json"));
    this.outputStream.write(ProReplay.GSON.toJson(meta, ReplayMeta.class).getBytes());
    this.outputStream.closeEntry();
    this.outputStream.flush();
    this.outputStream.close();
  }
}
