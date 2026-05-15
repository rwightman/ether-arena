package org.tdod.ether.ta.player.enums;

import junit.framework.Assert;

import org.testng.annotations.Test;

public class PlayerClassTest {

   @Test(groups = { "unit" })
   public void testBaseClassMaxLevelMatchesOriginalTable() {
      Assert.assertFalse(PlayerClass.WARRIOR.isMaxLevel(24));
      Assert.assertTrue(PlayerClass.WARRIOR.isMaxLevel(25));
   }

   @Test(groups = { "unit" })
   public void testBaseClassOutOfRangeLevelClampsToKnownTable() {
      int level25Requirement = PlayerClass.WARRIOR.getExpRequirement(25, false);

      Assert.assertEquals(level25Requirement, PlayerClass.WARRIOR.getExpRequirement(26, false));
   }

   @Test(groups = { "unit" })
   public void testPromotedClassUsesLongerTable() {
      Assert.assertFalse(PlayerClass.KNIGHT.isMaxLevel(49));
      Assert.assertTrue(PlayerClass.KNIGHT.isMaxLevel(50));
   }
}
