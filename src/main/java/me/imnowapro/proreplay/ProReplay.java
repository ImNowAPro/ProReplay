package me.imnowapro.proreplay;

import org.bukkit.plugin.java.JavaPlugin;

public class ProReplay extends JavaPlugin {

  private static ProReplay instance = null;

  @Override
  public void onEnable() {
    instance = this;
  }

  public static ProReplay getInstance() {
    return instance;
  }
}
