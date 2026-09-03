package com.storytimeproductions.nextbothunt.lobby;

import java.util.UUID;

/** Per-player lobby state. Every player is a Hider - see specs/gamemode-rules.md. */
public class HiderPlayerData {

  private final UUID playerId;
  private boolean ready;

  /** Constructs fresh, not-ready data for the given player. */
  public HiderPlayerData(UUID playerId) {
    this.playerId = playerId;
  }

  public UUID getPlayerId() {
    return playerId;
  }

  public boolean isReady() {
    return ready;
  }

  public void setReady(boolean ready) {
    this.ready = ready;
  }
}
