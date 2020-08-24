package me.imnowapro.proreplay;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import me.imnowapro.proreplay.listener.JoinQuitListener;
import me.imnowapro.proreplay.replay.recording.converter.PacketConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ProReplay extends JavaPlugin implements Listener {

  public static Gson GSON = new GsonBuilder()
      .setPrettyPrinting()
      .create();
  private static ProReplay instance = null;

  private PacketConverter packetConverter;
  private File replayFolder;

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
    saveResource("config.yml", true);
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

  public static ProReplay getInstance() {
    return instance;
  }
}
