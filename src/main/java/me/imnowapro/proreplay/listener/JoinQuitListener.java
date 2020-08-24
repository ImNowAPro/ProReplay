package me.imnowapro.proreplay.listener;

import com.comphenix.protocol.ProtocolLibrary;
import java.io.File;
import java.io.IOException;
import me.imnowapro.proreplay.ProReplay;
import me.imnowapro.proreplay.file.ReplayWriter;
import me.imnowapro.proreplay.replay.Replay;
import me.imnowapro.proreplay.replay.recording.Recorder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinQuitListener implements Listener {

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    // Test
    Recorder recorder = new Recorder(event.getPlayer());
    ProtocolLibrary.getProtocolManager().addPacketListener(recorder);
    Bukkit.getPluginManager().registerEvents(recorder, ProReplay.getInstance());
    recorder.start();
    Bukkit.getScheduler().runTaskLater(ProReplay.getInstance(), () -> {
      recorder.stop();
      Replay replay = new Replay(recorder);
      try {
        new ReplayWriter(new File(ProReplay.getInstance().getReplayFolder(), "test.mcpr"))
            .writeAndClose(replay);
      } catch (IOException e) {
        e.printStackTrace();
      }
      recorder.getRecordedPlayer().sendMessage("Finished replay.");
    }, 20 * 20);
  }
}
