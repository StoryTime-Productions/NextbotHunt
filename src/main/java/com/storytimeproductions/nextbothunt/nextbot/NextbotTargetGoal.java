package com.storytimeproductions.nextbothunt.nextbot;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import java.util.EnumSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.java.JavaPlugin;

/** Re-targets the nearest LOS-visible player; see specs/nextbot-entity-design.md. */
public class NextbotTargetGoal implements Goal<Zombie> {

  private static final double DETECTION_RADIUS = 20.0;
  private static final int TARGET_LOST_GRACE_TICKS = 100;

  private final NextbotEntity nextbot;
  private final GoalKey<Zombie> key;

  /** Constructs the targeting goal for the given nextbot. */
  public NextbotTargetGoal(NextbotEntity nextbot, JavaPlugin plugin) {
    this.nextbot = nextbot;
    this.key = GoalKey.of(Zombie.class, new NamespacedKey(plugin, "nextbot_target"));
  }

  @Override
  public boolean shouldActivate() {
    return true;
  }

  @Override
  public void tick() {
    Zombie brain = nextbot.getBrain();
    Player current = nextbot.getTarget();

    if (current != null && isValidTarget(brain, current)) {
      nextbot.setTargetLostTicks(0);
      return;
    }

    if (current != null) {
      int lost = nextbot.getTargetLostTicks() + 1;
      if (lost < TARGET_LOST_GRACE_TICKS) {
        nextbot.setTargetLostTicks(lost);
        return;
      }
      nextbot.setTarget(null);
      nextbot.setTargetLostTicks(0);
    }

    nextbot.setTarget(findNearestVisiblePlayer(brain));
  }

  private boolean isValidTarget(Zombie brain, Player player) {
    return player.isValid()
        && !player.isDead()
        && player.getWorld().equals(brain.getWorld())
        && brain.getLocation().distanceSquared(player.getLocation())
            <= DETECTION_RADIUS * DETECTION_RADIUS
        && brain.hasLineOfSight(player);
  }

  private Player findNearestVisiblePlayer(Zombie brain) {
    Player nearest = null;
    double nearestDistanceSquared = Double.MAX_VALUE;
    for (Player player : brain.getWorld().getPlayers()) {
      if (!isValidTarget(brain, player)) {
        continue;
      }
      double distanceSquared = brain.getLocation().distanceSquared(player.getLocation());
      if (distanceSquared < nearestDistanceSquared) {
        nearestDistanceSquared = distanceSquared;
        nearest = player;
      }
    }
    return nearest;
  }

  @Override
  public GoalKey<Zombie> getKey() {
    return key;
  }

  @Override
  public EnumSet<GoalType> getTypes() {
    return EnumSet.of(GoalType.TARGET);
  }
}
