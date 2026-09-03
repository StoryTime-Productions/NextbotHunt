package com.storytimeproductions.nextbothunt.nextbot;

import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

/** Brain/body/skin composite for one nextbot - see specs/nextbot-entity-design.md. */
public class NextbotEntity {

  private static final float BODY_WIDTH = 0.8f;
  private static final float BODY_HEIGHT = 1.9f;
  private static final float SKIN_SCALE = 2.0f;
  private static final float SKIN_Y_OFFSET = 1.0f;
  private static final int TELEPORT_DURATION_TICKS = 3;
  private static final double TOUCH_RADIUS = 1.0;

  private final Zombie brain;
  private final Interaction body;
  private final ItemDisplay skin;
  private Player target;
  private int targetLostTicks;
  private Consumer<Player> onTouch = player -> {};

  /** Spawns the composite's three entities at the given location. */
  public NextbotEntity(Location spawnLocation, JavaPlugin plugin) {
    this.brain = spawnLocation.getWorld().spawn(spawnLocation, Zombie.class, this::configureBrain);
    this.body =
        spawnLocation.getWorld().spawn(spawnLocation, Interaction.class, this::configureBody);
    this.skin =
        spawnLocation.getWorld().spawn(spawnLocation, ItemDisplay.class, this::configureSkin);
    Bukkit.getMobGoals().addGoal(brain, 0, new NextbotTargetGoal(this, plugin));
    Bukkit.getMobGoals().addGoal(brain, 0, new NextbotChaseGoal(this, plugin));
  }

  private void configureBrain(Zombie zombie) {
    zombie.setInvisible(true);
    zombie.setSilent(true);
    zombie.setInvulnerable(true);
    zombie.setShouldBurnInDay(false);
    zombie.setCollidable(false);
    zombie.setPersistent(true);
    zombie.setRemoveWhenFarAway(false);
    Bukkit.getMobGoals().removeAllGoals(zombie);
  }

  private void configureBody(Interaction interaction) {
    interaction.setInteractionWidth(BODY_WIDTH);
    interaction.setInteractionHeight(BODY_HEIGHT);
    interaction.setPersistent(true);
  }

  private void configureSkin(ItemDisplay display) {
    display.setBillboard(Display.Billboard.CENTER);
    display.setItemStack(new ItemStack(Material.PAPER));
    display.setPersistent(true);
    display.setTransformation(
        new Transformation(
            new Vector3f(0, SKIN_Y_OFFSET, 0),
            new AxisAngle4f(0, 0, 0, 1),
            new Vector3f(SKIN_SCALE, SKIN_SCALE, SKIN_SCALE),
            new AxisAngle4f(0, 0, 0, 1)));
  }

  /** Syncs body/skin position to the brain. Call once per tick. */
  public void tick() {
    if (!brain.isValid()) {
      return;
    }
    // setShouldBurnInDay(false) alone doesn't stop daylight ignition on this build - see
    // specs/nextbot-entity-design.md. Extinguish defensively every tick instead.
    if (brain.getFireTicks() > 0) {
      brain.setFireTicks(0);
    }
    Location loc = brain.getLocation();
    body.teleport(loc);
    skin.setTeleportDuration(TELEPORT_DURATION_TICKS);
    skin.teleport(loc);

    for (Player player : body.getWorld().getPlayers()) {
      if (body.getLocation().distance(player.getLocation()) <= TOUCH_RADIUS) {
        onTouch.accept(player);
      }
    }
  }

  /** Sets the callback invoked (every tick while in range) when a player touches the body. */
  public void setOnTouch(Consumer<Player> onTouch) {
    this.onTouch = onTouch;
  }

  public boolean isValid() {
    return brain.isValid() && body.isValid() && skin.isValid();
  }

  public Zombie getBrain() {
    return brain;
  }

  public Interaction getBody() {
    return body;
  }

  public Player getTarget() {
    return target;
  }

  public void setTarget(Player target) {
    this.target = target;
  }

  public int getTargetLostTicks() {
    return targetLostTicks;
  }

  public void setTargetLostTicks(int targetLostTicks) {
    this.targetLostTicks = targetLostTicks;
  }

  /** Removes all three entities. */
  public void remove() {
    brain.remove();
    body.remove();
    skin.remove();
  }
}
