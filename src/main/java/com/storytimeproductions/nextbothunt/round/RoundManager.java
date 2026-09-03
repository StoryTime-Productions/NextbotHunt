package com.storytimeproductions.nextbothunt.round;

import com.storytimeproductions.nextbothunt.lobby.HiderPlayerData;
import com.storytimeproductions.nextbothunt.lobby.LobbyManager;
import com.storytimeproductions.nextbothunt.nextbot.NextbotEntity;
import com.storytimeproductions.nextbothunt.nextbot.NextbotManager;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * Owns a single round: prep countdown, one nextbot spawn, the round timer, and win/loss - see
 * specs/gamemode-rules.md.
 */
public class RoundManager {

  /** Round lifecycle state. */
  public enum State {
    WAITING,
    PREP,
    ACTIVE,
    ENDED
  }

  private static final int PREP_SECONDS = 10;
  private static final int ROUND_SECONDS = 180;

  private final JavaPlugin plugin;
  private final LobbyManager lobbyManager;
  private final NextbotManager nextbotManager;

  private State state = State.WAITING;
  private final Set<UUID> participants = new HashSet<>();
  private final Set<UUID> eliminated = new HashSet<>();
  private BukkitTask scheduledTask;

  /** Constructs a new RoundManager backed by the given lobby and nextbot managers. */
  public RoundManager(JavaPlugin plugin, LobbyManager lobbyManager, NextbotManager nextbotManager) {
    this.plugin = plugin;
    this.lobbyManager = lobbyManager;
    this.nextbotManager = nextbotManager;
  }

  public State getState() {
    return state;
  }

  /**
   * Starts a round if enough ready players are in the lobby. Returns false (no message sent) if a
   * round is already in progress or there aren't enough ready players.
   */
  public boolean startRound(Player initiator) {
    if (state != State.WAITING) {
      initiator.sendMessage(Component.text("A round is already in progress.", NamedTextColor.RED));
      return false;
    }
    if (!lobbyManager.canStart()) {
      initiator.sendMessage(
          Component.text(
              "Need at least " + LobbyManager.MINIMUM_PLAYERS + " players, all ready, to start.",
              NamedTextColor.RED));
      return false;
    }

    participants.clear();
    eliminated.clear();
    for (HiderPlayerData data : lobbyManager.getAllPlayerData().values()) {
      participants.add(data.getPlayerId());
    }
    state = State.PREP;

    Location spawnLocation = initiator.getLocation();
    broadcast(
        Component.text(
            "Round starting in " + PREP_SECONDS + " seconds - scatter and hide!",
            NamedTextColor.YELLOW));
    scheduledTask =
        Bukkit.getScheduler()
            .runTaskLater(plugin, () -> beginActivePhase(spawnLocation), PREP_SECONDS * 20L);
    return true;
  }

  private void beginActivePhase(Location spawnLocation) {
    state = State.ACTIVE;
    NextbotEntity nextbot = nextbotManager.spawn(spawnLocation);
    nextbot.setOnTouch(this::onHiderTouched);
    broadcast(Component.text("The nextbot is loose. Good luck.", NamedTextColor.RED));
    scheduledTask =
        Bukkit.getScheduler().runTaskLater(plugin, () -> endRound(true), ROUND_SECONDS * 20L);
  }

  private void onHiderTouched(Player player) {
    if (state != State.ACTIVE) {
      return;
    }
    UUID id = player.getUniqueId();
    if (!participants.contains(id) || eliminated.contains(id)) {
      return;
    }
    eliminated.add(id);
    player.setGameMode(GameMode.SPECTATOR);
    player.sendMessage(Component.text("The nextbot got you!", NamedTextColor.RED));

    if (eliminated.size() >= participants.size()) {
      endRound(false);
    }
  }

  /** Ends the round. hidersWin is true if the timer expired with a hider still alive. */
  private void endRound(boolean hidersWin) {
    if (state == State.ENDED || state == State.WAITING) {
      return;
    }
    state = State.ENDED;
    if (scheduledTask != null) {
      scheduledTask.cancel();
      scheduledTask = null;
    }
    nextbotManager.despawnAll();

    Component result =
        hidersWin
            ? Component.text("The hiders survived! Hiders win.", NamedTextColor.GREEN)
            : Component.text("All hiders were caught. The nextbot wins.", NamedTextColor.RED);
    broadcast(result);

    for (UUID id : participants) {
      Player player = Bukkit.getPlayer(id);
      if (player != null) {
        player.setGameMode(GameMode.ADVENTURE);
      }
      HiderPlayerData data = lobbyManager.getPlayerData(id);
      if (data != null) {
        data.setReady(false);
      }
    }

    participants.clear();
    eliminated.clear();
    state = State.WAITING;
  }

  private void broadcast(Component message) {
    for (UUID id : participants) {
      Player player = Bukkit.getPlayer(id);
      if (player != null) {
        player.sendMessage(message);
      }
    }
  }
}
