package com.storytimeproductions.nextbothunt;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

/**
 * Smoke test verifying the plugin enables cleanly under MockBukkit.
 *
 * <p>Disabled: constructing a MockBukkit {@code ServerMock} against Paper 26.1.2/26.2 currently
 * throws (RegistryAccessMock fails to instantiate) - a known, open upstream bug:
 * https://github.com/MockBukkit/MockBukkit/issues/1595. Re-enable once that's fixed.
 */
@Disabled("blocked on MockBukkit/MockBukkit#1595 (Paper 26.x registry access)")
class NextbotHuntTest {

  private ServerMock server;
  private NextbotHunt plugin;

  @BeforeEach
  void setUp() {
    server = MockBukkit.mock();
    plugin = MockBukkit.load(NextbotHunt.class);
  }

  @AfterEach
  void tearDown() {
    MockBukkit.unmock();
  }

  @Test
  void pluginEnablesSuccessfully() {
    assertNotNull(plugin);
    assertTrue(plugin.isEnabled());
  }
}
