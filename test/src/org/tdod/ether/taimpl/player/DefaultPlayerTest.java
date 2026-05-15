package org.tdod.ether.taimpl.player;

import org.testng.AssertJUnit;

import org.tdod.ether.ta.player.Player;
import org.tdod.ether.ta.player.enums.PlayerClass;
import org.testng.annotations.Test;

public class DefaultPlayerTest {

   @Test(groups = { "unit" })
   public void testBaseExperienceCapUsesBaseClassTable() {
      Player player = createPlayer(PlayerClass.WARRIOR, 24, false);

      player.addExperience(Long.MAX_VALUE);

      AssertJUnit.assertEquals(PlayerClass.WARRIOR.getExpRequirement(25, false) - 1,
            player.getExperience());
   }

   @Test(groups = { "unit" })
   public void testPromotedExperienceCapUsesPromotedClassTable() {
      Player player = createPlayer(PlayerClass.WARRIOR, 74, true);

      player.addExperience(Long.MAX_VALUE);

      AssertJUnit.assertEquals(PlayerClass.KNIGHT.getExpRequirement(50, true) - 1,
            player.getExperience());
   }

   @Test(groups = { "unit" })
   public void testEarlyPromotedExperienceCapMatchesOriginalProgressionCurve() {
      Player player = createPlayer(PlayerClass.WARRIOR, 26, true);

      player.addExperience(Long.MAX_VALUE);

      AssertJUnit.assertEquals(PlayerClass.KNIGHT.getExpRequirement(8, true) - 1,
            player.getExperience());
   }

   private Player createPlayer(PlayerClass playerClass, int level, boolean promoted) {
      System.setProperty("TaConfigFile", "config/ta.properties");
      Player player = new DefaultPlayer();
      player.setPlayerClass(playerClass);
      player.setLevel(level);
      player.setPromoted(promoted);
      return player;
   }
}
