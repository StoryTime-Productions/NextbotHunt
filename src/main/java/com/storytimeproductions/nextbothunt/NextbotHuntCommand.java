package com.storytimeproductions.nextbothunt;

import com.storytimeproductions.nextbothunt.nextbot.NextbotManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/** Handles /nextbothunt. Only debugspawn is wired so far - join/leave/status land in Phase C. */
public class NextbotHuntCommand implements CommandExecutor {

  private final NextbotManager nextbotManager;

  /** Constructs a new command handler backed by the given nextbot manager. */
  public NextbotHuntCommand(NextbotManager nextbotManager) {
    this.nextbotManager = nextbotManager;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player player)) {
      sender.sendMessage(
          Component.text("This command can only be used by players!", NamedTextColor.RED));
      return true;
    }

    if (args.length == 1 && args[0].equalsIgnoreCase("debugspawn")) {
      if (!player.hasPermission("nextbothunt.debug")) {
        player.sendMessage(
            Component.text("You don't have permission to do that.", NamedTextColor.RED));
        return true;
      }
      nextbotManager.spawn(player.getLocation());
      player.sendMessage(
          Component.text("Spawned a nextbot at your location.", NamedTextColor.GREEN));
      return true;
    }

    player.sendMessage(
        Component.text("Nextbot Hunt lobby isn't implemented yet.", NamedTextColor.YELLOW));
    return true;
  }
}
