package org.tdod.ether.taimpl.engine;

import org.testng.AssertJUnit;

import org.tdod.ether.ta.cosmos.Trap;
import org.tdod.ether.ta.player.Player;
import org.tdod.ether.ta.player.enums.PlayerClass;
import org.tdod.ether.taimpl.cosmos.DefaultTrap;
import org.tdod.ether.taimpl.cosmos.enums.TrapType;
import org.tdod.ether.taimpl.player.DefaultPlayer;
import org.testng.annotations.Test;

public class DefaultGameMechanicsTest {

   @Test(groups = { "unit" })
   public void testHighSkillRogueAvoidsTrap() {
      DefaultGameMechanics mechanics = createGameMechanics();
      Player player = createPlayer(PlayerClass.ROGUE);
      Trap trap = createTrap(TrapType.TRAP6);

      AssertJUnit.assertTrue(mechanics.avoidedTrap(player, trap));
   }

   @Test(groups = { "unit" })
   public void testNonRogueDoesNotAvoidTrap() {
      DefaultGameMechanics mechanics = createGameMechanics();
      Player player = createPlayer(PlayerClass.WARRIOR);
      Trap trap = createTrap(TrapType.TRAP0);

      AssertJUnit.assertFalse(mechanics.avoidedTrap(player, trap));
   }

   private DefaultGameMechanics createGameMechanics() {
      System.setProperty("TaConfigFile", "config/ta.properties");
      return new DefaultGameMechanics();
   }

   private Player createPlayer(PlayerClass playerClass) {
      Player player = new DefaultPlayer();
      player.setPlayerClass(playerClass);
      player.setLevel(100);
      player.getStats().getAgility().setValue(50);
      player.getStats().getIntellect().setValue(50);
      return player;
   }

   private Trap createTrap(TrapType trapType) {
      Trap trap = new DefaultTrap();
      trap.setTrapType(trapType);
      return trap;
   }
}
