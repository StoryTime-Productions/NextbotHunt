package com.storytimeproductions.nextbothunt;

import com.storytimeproductions.nextbothunt.lobby.HiderPlayerData;
import com.storytimeproductions.nextbothunt.lobby.LobbyManager;
import com.storytimeproductions.nextbothunt.nextbot.NextbotManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/** Handles /nextbothunt: join/leave/ready/status the lobby, plus the debugspawn dev command. */
public class NextbotHuntCommand implements CommandExecutor {

  private final NextbotManager nextbotManager;
  private final LobbyManager lobbyManager;

  /** Constructs a new command handler backed by the given managers. */
  public NextbotHuntCommand(NextbotManager nextbotManager, LobbyManager lobbyManager) {
    this.nextbotManager = nextbotManager;
    this.lobbyManager = lobbyManager;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player player)) {
      sender.sendMessage(
          Component.text("This command can only be used by players!", NamedTextColor.RED));
      return true;
    }

    if (args.length != 1) {
      player.sendMessage(
          Component.text("Usage: /nextbothunt [join|leave|ready|status]", NamedTextColor.YELLOW));
      return true;
    }

    switch (args[0].toLowerCase()) {
      case "join" -> handleJoin(player);
      case "leave" -> handleLeave(player);
      case "ready" -> handleReady(player);
      case "status" -> handleStatus(player);
      case "debugspawn" -> handleDebugSpawn(player);
      default ->
          player.sendMessage(
              Component.text(
                  "Usage: /nextbothunt [join|leave|ready|status]", NamedTextColor.YELLOW));
    }
    return true;
  }

  private void handleJoin(Player player) {
    if (lobbyManager.isInLobby(player.getUniqueId())) {
      player.sendMessage(Component.text("You're already in the lobby.", NamedTextColor.YELLOW));
      return;
    }
    lobbyManager.join(player.getUniqueId());
    player.sendMessage(
        Component.text(
            "Joined the Nextbot Hunt lobby. Everyone is a Hider in this mode - use /nextbothunt"
                + " ready when you're set.",
            NamedTextColor.GREEN));
  }

  private void handleLeave(Player player) {
    if (!lobbyManager.isInLobby(player.getUniqueId())) {
      player.sendMessage(Component.text("You're not in the lobby.", NamedTextColor.YELLOW));
      return;
    }
    lobbyManager.leave(player.getUniqueId());
    player.sendMessage(Component.text("Left the Nextbot Hunt lobby.", NamedTextColor.YELLOW));
  }

  private void handleReady(Player player) {
    HiderPlayerData data = lobbyManager.getPlayerData(player.getUniqueId());
    if (data == null) {
      player.sendMessage(
          Component.text("Join the lobby first with /nextbothunt join.", NamedTextColor.RED));
      return;
    }
    data.setReady(!data.isReady());
    player.sendMessage(
        Component.text(
            data.isReady() ? "You are now ready!" : "You are no longer ready.",
            data.isReady() ? NamedTextColor.GREEN : NamedTextColor.YELLOW));
  }

  private void handleStatus(Player player) {
    player.sendMessage(
        Component.text(
            lobbyManager.getReadyCount()
                + "/"
                + lobbyManager.getAllPlayerData().size()
                + " players ready (need "
                + LobbyManager.MINIMUM_PLAYERS
                + " minimum, all in lobby ready).",
            NamedTextColor.AQUA));
  }

  private void handleDebugSpawn(Player player) {
    if (!player.hasPermission("nextbothunt.debug")) {
      player.sendMessage(
          Component.text("You don't have permission to do that.", NamedTextColor.RED));
      return;
    }
    nextbotManager.spawn(player.getLocation());
    player.sendMessage(Component.text("Spawned a nextbot at your location.", NamedTextColor.GREEN));
  }
}
