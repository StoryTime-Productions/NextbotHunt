package com.storytimeproductions.nextbothunt;

import com.storytimeproductions.nextbothunt.nextbot.NextbotManager;
import org.bukkit.plugin.java.JavaPlugin;

/** Entry point for the Nextbot Hunt plugin. */
public class NextbotHunt extends JavaPlugin {

  private NextbotManager nextbotManager;

  @Override
  public void onEnable() {
    nextbotManager = new NextbotManager(this);
    nextbotManager.start();
    var command = getCommand("nextbothunt");
    if (command != null) {
      command.setExecutor(new NextbotHuntCommand(nextbotManager));
    }
    getLogger().info("Nextbot Hunt enabled.");
  }

  @Override
  public void onDisable() {
    if (nextbotManager != null) {
      nextbotManager.stop();
    }
    getLogger().info("Nextbot Hunt disabled.");
  }

  public NextbotManager getNextbotManager() {
    return nextbotManager;
  }
}
