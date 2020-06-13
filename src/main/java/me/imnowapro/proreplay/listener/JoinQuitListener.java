package me.imnowapro.proreplay.listener;

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
    recorder.start();
    Bukkit.getScheduler().runTaskLaterAsynchronously(ProReplay.getInstance(), () -> {
      recorder.stop();
      Replay replay = new Replay(recorder);
      ProReplay.getInstance().getDataFolder().mkdirs();
      try {
        new ReplayWriter(new File(ProReplay.getInstance().getDataFolder(), "test.mcpr"))
            .writeAndClose(replay);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }, 20 * 10);
  }
}
