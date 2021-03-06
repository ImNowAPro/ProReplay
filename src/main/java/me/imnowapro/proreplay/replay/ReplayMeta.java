package me.imnowapro.proreplay.replay;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import me.imnowapro.proreplay.ProReplay;

public class ReplayMeta implements Serializable {

  private transient String name;
  private final boolean singleplayer = false;
  private final String generator = "ProReplay Generator";
  private long duration;
  private long date;
  private String mcversion;
  private int protocol;
  private final int fileFormatVersion = 14;
  private final Collection<String> players = new HashSet<>();

  public ReplayMeta(String name, long duration, long date, String mcversion, int protocol) {
    this.name = name;
    this.duration = duration;
    this.date = date;
    this.mcversion = mcversion;
    this.protocol = protocol;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public File getFile() {
    return new File(ProReplay.getInstance().getReplayFolder(), this.name + ".mcpr");
  }

  public boolean isSingleplayer() {
    return this.singleplayer;
  }

  public String getGenerator() {
    return this.generator;
  }

  public long getDuration() {
    return this.duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public long getDate() {
    return this.date;
  }

  public void setDate(long date) {
    this.date = date;
  }

  public String getMcversion() {
    return this.mcversion;
  }

  public void setMcversion(String mcversion) {
    this.mcversion = mcversion;
  }

  public int getProtocol() {
    return this.protocol;
  }

  public void setProtocol(int protocol) {
    this.protocol = protocol;
  }

  public int getFileFormatVersion() {
    return this.fileFormatVersion;
  }

  public Collection<String> getPlayers() {
    return this.players;
  }
}
