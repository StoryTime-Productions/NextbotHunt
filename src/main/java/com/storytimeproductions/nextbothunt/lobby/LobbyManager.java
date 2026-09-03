package com.storytimeproductions.nextbothunt.lobby;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks who's in the lobby and ready. No team selection - every player is a Hider, there is no
 * Hunters option in this gamemode. See specs/gamemode-rules.md.
 */
public class LobbyManager {

  public static final int MINIMUM_PLAYERS = 2;

  private final Map<UUID, HiderPlayerData> playerData = new HashMap<>();

  /** Adds a player to the lobby, creating fresh (not-ready) data if they weren't already in it. */
  public HiderPlayerData join(UUID playerId) {
    return playerData.computeIfAbsent(playerId, HiderPlayerData::new);
  }

  /** Removes a player from the lobby. */
  public void leave(UUID playerId) {
    playerData.remove(playerId);
  }

  /** Whether the given player is currently in the lobby. */
  public boolean isInLobby(UUID playerId) {
    return playerData.containsKey(playerId);
  }

  /** Returns the given player's lobby data, or null if they aren't in the lobby. */
  public HiderPlayerData getPlayerData(UUID playerId) {
    return playerData.get(playerId);
  }

  public Map<UUID, HiderPlayerData> getAllPlayerData() {
    return Map.copyOf(playerData);
  }

  public int getReadyCount() {
    return (int) playerData.values().stream().filter(HiderPlayerData::isReady).count();
  }

  /** Whether enough ready players are present to start a round. */
  public boolean canStart() {
    return getReadyCount() >= MINIMUM_PLAYERS && getReadyCount() == playerData.size();
  }
}
