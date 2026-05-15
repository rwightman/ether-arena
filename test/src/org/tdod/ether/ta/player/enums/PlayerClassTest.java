package org.tdod.ether.ta.player.enums;

import org.testng.AssertJUnit;

import org.testng.annotations.Test;

public class PlayerClassTest {

   @Test(groups = { "unit" })
   public void testBaseClassMaxLevelMatchesOriginalTable() {
      AssertJUnit.assertFalse(PlayerClass.WARRIOR.isMaxLevel(24));
      AssertJUnit.assertTrue(PlayerClass.WARRIOR.isMaxLevel(25));
   }

   @Test(groups = { "unit" })
   public void testBaseClassOutOfRangeLevelClampsToKnownTable() {
      int level25Requirement = PlayerClass.WARRIOR.getExpRequirement(25, false);

      AssertJUnit.assertEquals(level25Requirement, PlayerClass.WARRIOR.getExpRequirement(26, false));
   }

   @Test(groups = { "unit" })
   public void testPromotedClassUsesLongerTable() {
      AssertJUnit.assertFalse(PlayerClass.KNIGHT.isMaxLevel(49));
      AssertJUnit.assertTrue(PlayerClass.KNIGHT.isMaxLevel(50));
   }

   @Test(groups = { "unit" })
   public void testClassTableReportsMaxLevel() {
      AssertJUnit.assertEquals(25, PlayerClass.WARRIOR.getMaxLevel());
      AssertJUnit.assertEquals(50, PlayerClass.KNIGHT.getMaxLevel());
   }

   @Test(groups = { "unit" })
   public void testClassTableClampsLevels() {
      AssertJUnit.assertEquals(1, PlayerClass.WARRIOR.getClampedLevel(0));
      AssertJUnit.assertEquals(25, PlayerClass.WARRIOR.getClampedLevel(26));
      AssertJUnit.assertEquals(10, PlayerClass.WARRIOR.getClampedLevel(10));
   }
}
