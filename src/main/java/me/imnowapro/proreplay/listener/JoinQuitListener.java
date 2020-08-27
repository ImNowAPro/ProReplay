package me.imnowapro.proreplay.listener;

import com.comphenix.protocol.ProtocolLibrary;
import java.io.File;
import java.io.IOException;
import me.imnowapro.proreplay.ProReplay;
import me.imnowapro.proreplay.file.ReplayReader;
import me.imnowapro.proreplay.replay.replaying.Replayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinQuitListener implements Listener {

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    // Test
    /*Recorder recorder = new Recorder("test",
        event.getPlayer(),
        ProReplay.getInstance().getConfig().getBoolean("writeDirectly"));
    ProtocolLibrary.getProtocolManager().addPacketListener(recorder);
    Bukkit.getPluginManager().registerEvents(recorder, ProReplay.getInstance());
    recorder.start();
    Bukkit.getScheduler().runTaskLater(ProReplay.getInstance(), () -> {
      recorder.stop();
      recorder.getRecordedPlayer().sendMessage("Finished replay.");
    }, 20 * 10);*/
    try {
      ReplayReader reader = new ReplayReader(new File(ProReplay.getInstance().getReplayFolder(),
          "test.mcpr"));
      Replayer replayer = new Replayer(reader.readReplayAndClose(), event.getPlayer());
      ProtocolLibrary.getProtocolManager().addPacketListener(replayer);
      Bukkit.getPluginManager().registerEvents(replayer, ProReplay.getInstance());
      Bukkit.getScheduler().scheduleSyncRepeatingTask(ProReplay.getInstance(), replayer, 0, 1);
      replayer.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
