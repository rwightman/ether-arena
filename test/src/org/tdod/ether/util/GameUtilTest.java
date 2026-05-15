package org.tdod.ether.util;

import org.testng.AssertJUnit;

import org.tdod.ether.ta.manager.WorldManager;
import org.tdod.ether.ta.player.Player;
import org.tdod.ether.ta.player.enums.PlayerClass;
import org.tdod.ether.taimpl.engine.DefaultGameMechanics;
import org.tdod.ether.taimpl.player.DefaultPlayer;
import org.testng.annotations.Test;

public class GameUtilTest {

   @Test(groups = { "unit" })
   public void testNoExperienceAwardedWithoutDamage() {
      Player player = createPlayer();
      Player target = createPlayer();

      target.getVitality().setCurVitality(100);
      GameUtil.giveExperience(player, target, 100);

      AssertJUnit.assertEquals(0L, player.getExperience());
   }

   @Test(groups = { "unit" })
   public void testExperienceIsCappedAtRemainingVitality() {
      Player player = createPlayer();
      Player target = createPlayer();

      target.getVitality().setCurVitality(-15);
      GameUtil.giveExperience(player, target, 10);

      AssertJUnit.assertEquals(10L, player.getExperience());
   }

   @Test(groups = { "unit" })
   public void testPlayerExperienceAwardIsCappedByTargetMaxVitality() {
      Player player = createPlayer();
      Player target = createPlayer();
      player.setLevel(2);

      target.getVitality().setCurVitality(-100000);
      GameUtil.giveExperience(player, target, 100000);

      AssertJUnit.assertEquals(100L, player.getExperience());
   }

   @Test(groups = { "unit" })
   public void testNoPlayerExperienceAwardedForLevelOneTarget() {
      Player player = createPlayer();
      Player target = createPlayer();
      target.setLevel(1);

      target.getVitality().setCurVitality(90);
      GameUtil.giveExperience(player, target, 100);

      AssertJUnit.assertEquals(0L, player.getExperience());
   }

   private Player createPlayer() {
      System.setProperty("TaConfigFile", "config/ta.properties");
      WorldManager.setGameMechanics(new DefaultGameMechanics());
      Player player = new DefaultPlayer();
      player.setPlayerClass(PlayerClass.WARRIOR);
      player.setLevel(20);
      player.getVitality().setCurVitality(100);
      player.getVitality().setMaxVitality(100);
      return player;
   }
}
