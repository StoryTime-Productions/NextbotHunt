package com.storytimeproductions.nextbothunt.nextbot;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

/** Brain/body/skin composite for one nextbot - see specs/nextbot-entity-design.md. */
public class NextbotEntity {

  private static final float BODY_WIDTH = 0.8f;
  private static final float BODY_HEIGHT = 1.9f;
  private static final int TELEPORT_DURATION_TICKS = 3;

  private final Zombie brain;
  private final Interaction body;
  private final ItemDisplay skin;

  /** Spawns the composite's three entities at the given location. */
  public NextbotEntity(Location spawnLocation) {
    this.brain = spawnLocation.getWorld().spawn(spawnLocation, Zombie.class, this::configureBrain);
    this.body =
        spawnLocation.getWorld().spawn(spawnLocation, Interaction.class, this::configureBody);
    this.skin =
        spawnLocation.getWorld().spawn(spawnLocation, ItemDisplay.class, this::configureSkin);
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
  }

  /** Syncs body/skin position to the brain. Call once per tick. */
  public void tick() {
    if (!brain.isValid()) {
      return;
    }
    Location loc = brain.getLocation();
    body.teleport(loc);
    skin.setTeleportDuration(TELEPORT_DURATION_TICKS);
    skin.teleport(loc);
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

  /** Removes all three entities. */
  public void remove() {
    brain.remove();
    body.remove();
    skin.remove();
  }
}
