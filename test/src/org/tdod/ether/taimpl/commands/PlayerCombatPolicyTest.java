package org.tdod.ether.taimpl.commands;

import org.testng.AssertJUnit;

import org.tdod.ether.ta.player.Player;
import org.tdod.ether.ta.player.enums.PlayerClass;
import org.tdod.ether.taimpl.player.DefaultPlayer;
import org.tdod.ether.util.TaMessageManager;
import org.testng.annotations.Test;

public class PlayerCombatPolicyTest {

   @Test(groups = { "unit" })
   public void testDisabledAttackGate() {
      PlayerCombatPolicy.Settings settings = createSettings(false, true, 0, true, 0, 0);

      AssertJUnit.assertEquals(TaMessageManager.NOAUSR,
            PlayerCombatPolicy.getAttackRebuffMessage(createPlayer(10), createPlayer(10), settings));
   }

   @Test(groups = { "unit" })
   public void testDisabledRobGate() {
      PlayerCombatPolicy.Settings settings = createSettings(true, false, 0, true, 0, 0);

      AssertJUnit.assertEquals(TaMessageManager.NORUSR,
            PlayerCombatPolicy.getRobRebuffMessage(createPlayer(10), createPlayer(10), settings));
   }

   @Test(groups = { "unit" })
   public void testPromotedCombatGate() {
      Player promoted = createPlayer(30);
      promoted.setPromoted(true);
      Player target = createPlayer(20);
      PlayerCombatPolicy.Settings settings = createSettings(true, true, 0, false, 0, 0);

      AssertJUnit.assertEquals(TaMessageManager.UTOOHI,
            PlayerCombatPolicy.getAttackRebuffMessage(promoted, target, settings));
      AssertJUnit.assertEquals(TaMessageManager.UTOOLO,
            PlayerCombatPolicy.getAttackRebuffMessage(target, promoted, settings));
   }

   @Test(groups = { "unit" })
   public void testNoCombatLevelGate() {
      Player actor = createPlayer(10);
      Player target = createPlayer(5);
      PlayerCombatPolicy.Settings settings = createSettings(true, true, 0, true, 0, 5);

      AssertJUnit.assertEquals(TaMessageManager.UTOOHI,
            PlayerCombatPolicy.getAttackRebuffMessage(actor, target, settings));
      AssertJUnit.assertEquals(TaMessageManager.UTOOLO,
            PlayerCombatPolicy.getAttackRebuffMessage(target, actor, settings));
   }

   @Test(groups = { "unit" })
   public void testLevelDifferenceGate() {
      Player actor = createPlayer(20);
      Player target = createPlayer(10);
      PlayerCombatPolicy.Settings settings = createSettings(true, true, 10, true, 0, 0);

      AssertJUnit.assertEquals(TaMessageManager.UTOOHI,
            PlayerCombatPolicy.getAttackRebuffMessage(actor, target, settings));
      AssertJUnit.assertEquals(TaMessageManager.UTOOLO,
            PlayerCombatPolicy.getAttackRebuffMessage(target, actor, settings));
   }

   @Test(groups = { "unit" })
   public void testCombatExemptionRequiresBothPlayers() {
      Player actor = createPlayer(50);
      Player target = createPlayer(10);
      PlayerCombatPolicy.Settings settings = createSettings(true, true, 10, true, 40, 0);

      AssertJUnit.assertEquals(TaMessageManager.UTOOHI,
            PlayerCombatPolicy.getAttackRebuffMessage(actor, target, settings));

      target.setLevel(40);
      AssertJUnit.assertNull(PlayerCombatPolicy.getAttackRebuffMessage(actor, target, settings));
   }

   private PlayerCombatPolicy.Settings createSettings(boolean userAttackEnabled, boolean userRobEnabled,
         int userCombatLevelDifference, boolean promotedCombatEnabled, int combatExemptionLevel,
         int noCombatMaxLevel) {
      return new PlayerCombatPolicy.Settings(userAttackEnabled, userRobEnabled, userCombatLevelDifference,
            promotedCombatEnabled, combatExemptionLevel, noCombatMaxLevel);
   }

   private Player createPlayer(int level) {
      Player player = new DefaultPlayer();
      player.setPlayerClass(PlayerClass.WARRIOR);
      player.setLevel(level);
      return player;
   }
}
