package me.imnowapro.proreplay.listener;

import me.imnowapro.proreplay.ProReplay;
import me.imnowapro.proreplay.replay.Replay;
import me.imnowapro.proreplay.replay.recording.Recorder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Recorder recorder = new Recorder(event.getPlayer().getName() + "_" + Replay.getRandomName(6),
        event.getPlayer(),
        ProReplay.getInstance().getConfig().getBoolean("writeDirectly"));
    ProReplay.getInstance().getRecorder().put(event.getPlayer(), recorder);
    recorder.start();
    /*try {
      ReplayReader reader = new ReplayReader(new File(ProReplay.getInstance().getReplayFolder(),
          "test.mcpr"));
      Replayer replayer = new Replayer(reader.readReplayAndClose(), event.getPlayer());
      ProtocolLibrary.getProtocolManager().addPacketListener(replayer);
      Bukkit.getPluginManager().registerEvents(replayer, ProReplay.getInstance());
      Bukkit.getScheduler().scheduleSyncRepeatingTask(ProReplay.getInstance(), replayer, 0, 1);
      replayer.start();
    } catch (IOException e) {
      e.printStackTrace();
    }*/
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    if (ProReplay.getInstance().getRecorder().containsKey(event.getPlayer())) {
      ProReplay.getInstance().getRecorder().get(event.getPlayer()).stop();
      ProReplay.getInstance().getRecorder().remove(event.getPlayer());
    }
  }
}
