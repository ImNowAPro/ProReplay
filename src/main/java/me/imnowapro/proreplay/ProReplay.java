package me.imnowapro.proreplay;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import me.imnowapro.proreplay.listener.JoinQuitListener;
import me.imnowapro.proreplay.replay.converter.PacketConverter;
import me.imnowapro.proreplay.replay.recording.Recorder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ProReplay extends JavaPlugin implements Listener {

  public static Gson GSON = new GsonBuilder()
      .setPrettyPrinting()
      .create();
  private static ProReplay instance = null;

  private PacketConverter packetConverter;
  private File replayFolder;

  private final Map<Player, Recorder> recorder = new HashMap<>();

  @Override
  public void onEnable() {
    instance = this;
    if (PacketConverter.getConverter().isPresent()) {
      this.packetConverter = PacketConverter.getConverter().get();
      getLogger().info("Successfully loaded PacketConverter.");
    } else {
      getLogger().severe("Your minecraft-version isn't supported!");
      Bukkit.getPluginManager().disablePlugin(this);
      return;
    }
    this.replayFolder = new File(getDataFolder().getPath() + "\\replays\\");
    loadConfig();
    registerListener();
    getLogger().info("Successfully loaded ProReplay.");
  }

  @Override
  public void onDisable() {
    getLogger().info("Successfully unloaded ProReplay.");
  }

  public void loadConfig() {
    saveDefaultConfig();
    getConfig().options().copyDefaults(true);
    getConfig().addDefault("serverName", "Example.net");
    getConfig().addDefault("writeDirectly", false);
    getConfig().addDefault("record.chat", true);
    getConfig().addDefault("record.scoreboard", true);
    getConfig().addDefault("record.title", false);
    saveConfig();
  }

  private void registerListener() {
    Bukkit.getPluginManager().registerEvents(new JoinQuitListener(), this);
  }

  public PacketConverter getPacketConverter() {
    return this.packetConverter;
  }

  public File getReplayFolder() {
    if (!this.replayFolder.exists()) {
      this.replayFolder.mkdirs();
    }
    return this.replayFolder;
  }

  public Map<Player, Recorder> getRecorder() {
    return this.recorder;
  }

  public static ProReplay getInstance() {
    return instance;
  }
}
