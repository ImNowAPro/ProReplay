package me.imnowapro.proreplay.listener;

import com.comphenix.protocol.ProtocolLibrary;
import me.imnowapro.proreplay.ProReplay;
import me.imnowapro.proreplay.replay.recording.Recorder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinQuitListener implements Listener {

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    // Test
    Recorder recorder = new Recorder("test",
        event.getPlayer(),
        ProReplay.getInstance().getConfig().getBoolean("writeDirectly"));
    ProtocolLibrary.getProtocolManager().addPacketListener(recorder);
    Bukkit.getPluginManager().registerEvents(recorder, ProReplay.getInstance());
    recorder.start();
    Bukkit.getScheduler().runTaskLater(ProReplay.getInstance(), () -> {
      recorder.stop();
      recorder.getRecordedPlayer().sendMessage("Finished replay.");
    }, 20 * 10);
  }
}
