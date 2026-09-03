package com.storytimeproductions.nextbothunt.nextbot;

import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

/** Owns active nextbot composites and ticks their body/skin position sync. */
public class NextbotManager {

  private static final double DARKNESS_RADIUS = 15.0;
  private static final int ATMOSPHERE_INTERVAL_TICKS = 10;

  private final JavaPlugin plugin;
  private final List<NextbotEntity> active = new ArrayList<>();
  private BukkitTask tickTask;
  private int tickCount;

  /** Constructs a new NextbotManager for the given plugin instance. */
  public NextbotManager(JavaPlugin plugin) {
    this.plugin = plugin;
  }

  /** Starts the per-tick position-sync task. */
  public void start() {
    if (tickTask != null) {
      return;
    }
    tickTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::tickAll, 1L, 1L);
  }

  /** Cancels the tick task and removes all active nextbots. */
  public void stop() {
    if (tickTask != null) {
      tickTask.cancel();
      tickTask = null;
    }
    despawnAll();
    Bukkit.getOnlinePlayers().forEach(p -> p.removePotionEffect(PotionEffectType.DARKNESS));
  }

  /** Spawns a new nextbot at the given location. */
  public NextbotEntity spawn(Location location) {
    NextbotEntity nextbot = new NextbotEntity(location, plugin);
    nextbot.setOnTouch(this::eliminate);
    active.add(nextbot);
    return nextbot;
  }

  /**
   * Placeholder touch response until Phase C's round system exists to handle real elimination
   * (removing from the round roster, win-condition checks, etc.) - see specs/gamemode-rules.md.
   */
  private void eliminate(Player player) {
    if (player.getGameMode() == GameMode.SPECTATOR) {
      return;
    }
    player.setGameMode(GameMode.SPECTATOR);
    player.sendMessage(Component.text("The nextbot got you!", NamedTextColor.RED));
  }

  /** Removes every active nextbot. */
  public void despawnAll() {
    active.forEach(NextbotEntity::remove);
    active.clear();
  }

  /** Returns a snapshot of the currently active nextbots. */
  public List<NextbotEntity> getActive() {
    return List.copyOf(active);
  }

  private void tickAll() {
    active.removeIf(n -> !n.isValid());
    active.forEach(NextbotEntity::tick);

    tickCount++;
    if (tickCount % ATMOSPHERE_INTERVAL_TICKS == 0) {
      applyAtmosphere();
    }
  }

  /**
   * Applies a proximity-driven darkness cue - see specs/plugin-limitation-circumvention.md.
   *
   * <p>Deliberately darkness-only, no dynamic view-distance/fog. Both setViewDistance and
   * setSendViewDistance were tried for a "closing-in fog" effect and both caused visible chunk
   * reload/pop-in on this Paper build when changed at runtime (see specs/nextbot-entity-design.md)
   * - darkness alone is a pure client-side overlay with no chunk loading involved, matching what
   * the original spec already concluded was sufficient.
   */
  private void applyAtmosphere() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      double nearest =
          active.stream()
              .filter(n -> n.getBrain().getWorld().equals(player.getWorld()))
              .mapToDouble(n -> n.getBrain().getLocation().distance(player.getLocation()))
              .min()
              .orElse(Double.MAX_VALUE);

      if (nearest <= DARKNESS_RADIUS) {
        player.addPotionEffect(
            new PotionEffect(
                PotionEffectType.DARKNESS, ATMOSPHERE_INTERVAL_TICKS * 4, 0, true, false, false));
      } else {
        player.removePotionEffect(PotionEffectType.DARKNESS);
      }
    }
  }
}
