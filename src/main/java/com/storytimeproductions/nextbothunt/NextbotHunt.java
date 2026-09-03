package com.storytimeproductions.nextbothunt;

import com.storytimeproductions.nextbothunt.lobby.LobbyManager;
import com.storytimeproductions.nextbothunt.nextbot.NextbotManager;
import com.storytimeproductions.nextbothunt.round.RoundManager;
import org.bukkit.plugin.java.JavaPlugin;

/** Entry point for the Nextbot Hunt plugin. */
public class NextbotHunt extends JavaPlugin {

  private NextbotManager nextbotManager;
  private LobbyManager lobbyManager;
  private RoundManager roundManager;

  @Override
  public void onEnable() {
    nextbotManager = new NextbotManager(this);
    nextbotManager.start();
    lobbyManager = new LobbyManager();
    roundManager = new RoundManager(this, lobbyManager, nextbotManager);
    var command = getCommand("nextbothunt");
    if (command != null) {
      command.setExecutor(new NextbotHuntCommand(nextbotManager, lobbyManager, roundManager));
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

  public LobbyManager getLobbyManager() {
    return lobbyManager;
  }

  public RoundManager getRoundManager() {
    return roundManager;
  }
}
