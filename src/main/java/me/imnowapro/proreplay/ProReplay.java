package me.imnowapro.proreplay;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

  @Override
  public void onEnable() {
    instance = this;
    if (PacketConverter.getConverter().isPresent()) {
      this.packetConverter = PacketConverter.getConverter().get();
      getLogger().info("Successfully loaded PacketConverter.");
    }
    /*try {
      new ReplayReader(new File(getDataFolder(), "rewi.mcpr"))
          .readAndClose().getPackets().forEach(packet -> {
        PacketType type = packetConverter.getPacketType(packet.getId());
        getLogger().info("0x" + Integer.toHexString(packet.getId()) + " "
            + (type.getPacketClass() != null ? type.getPacketClass().getSimpleName() : "") + " "
            + type.name());
      });
    } catch (IOException e) {
      e.printStackTrace();
    }*/
    registerListener();
    getLogger().info("Successfully loaded ProReplay.");
  }

  @Override
  public void onDisable() {
    getLogger().info("Successfully unloaded ProReplay.");
  }

  private void registerListener() {
    Bukkit.getPluginManager().registerEvents(new JoinQuitListener(), this);
  }

  public PacketConverter getPacketConverter() {
    return packetConverter;
  }

  public static ProReplay getInstance() {
    return instance;
  }
}
