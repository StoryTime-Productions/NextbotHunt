package com.storytimeproductions.nextbothunt;

import org.bukkit.plugin.java.JavaPlugin;

/** Entry point for the Nextbot Hunt plugin. */
public class NextbotHunt extends JavaPlugin {

  @Override
  public void onEnable() {
    getLogger().info("Nextbot Hunt enabled.");
  }

  @Override
  public void onDisable() {
    getLogger().info("Nextbot Hunt disabled.");
  }
}
