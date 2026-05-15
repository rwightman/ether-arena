package org.tdod.ether.taimpl.player;

import org.testng.AssertJUnit;

import org.testng.annotations.Test;

public class DefaultManaTest {

   @Test(groups = { "unit" })
   public void testAddCurManaCapsAtMaxMana() {
      DefaultMana mana = new DefaultMana();
      mana.setMaxMana(10);
      mana.setCurMana(9);

      mana.addCurMana(5);

      AssertJUnit.assertEquals(10, mana.getCurMana());
   }

   @Test(groups = { "unit" })
   public void testAddCurManaPreservesManastoneOverflow() {
      DefaultMana mana = new DefaultMana();
      mana.setMaxMana(10);
      mana.setCurMana(20);

      mana.addCurMana(1);

      AssertJUnit.assertEquals(20, mana.getCurMana());
   }
}
