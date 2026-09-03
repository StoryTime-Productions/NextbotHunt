package com.storytimeproductions.nextbothunt.nextbot;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import java.util.EnumSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.java.JavaPlugin;

/** Paths the brain toward its current target at an aggressive speed. */
public class NextbotChaseGoal implements Goal<Zombie> {

  private static final double CHASE_SPEED = 1.6;

  private final NextbotEntity nextbot;
  private final GoalKey<Zombie> key;

  /** Constructs the chase goal for the given nextbot. */
  public NextbotChaseGoal(NextbotEntity nextbot, JavaPlugin plugin) {
    this.nextbot = nextbot;
    this.key = GoalKey.of(Zombie.class, new NamespacedKey(plugin, "nextbot_chase"));
  }

  @Override
  public boolean shouldActivate() {
    return nextbot.getTarget() != null;
  }

  @Override
  public void tick() {
    Player target = nextbot.getTarget();
    if (target == null) {
      return;
    }
    nextbot.getBrain().getPathfinder().moveTo(target.getLocation(), CHASE_SPEED);
  }

  @Override
  public GoalKey<Zombie> getKey() {
    return key;
  }

  @Override
  public EnumSet<GoalType> getTypes() {
    return EnumSet.of(GoalType.MOVE);
  }
}
