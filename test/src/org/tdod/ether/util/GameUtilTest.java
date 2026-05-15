package org.tdod.ether.util;

import org.testng.AssertJUnit;

import org.tdod.ether.ta.player.Player;
import org.tdod.ether.ta.player.enums.PlayerClass;
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
   public void testExperienceAwardIsCappedByAttackerLevel() {
      Player player = createPlayer();
      Player target = createPlayer();
      player.setLevel(2);

      target.getVitality().setCurVitality(-100000);
      GameUtil.giveExperience(player, target, 100000);

      AssertJUnit.assertEquals(6000L, player.getExperience());
   }

   private Player createPlayer() {
      System.setProperty("TaConfigFile", "config/ta.properties");
      Player player = new DefaultPlayer();
      player.setPlayerClass(PlayerClass.WARRIOR);
      player.setLevel(20);
      return player;
   }
}
