package org.tdod.ether.taimpl.commands;

import java.text.MessageFormat;

import org.testng.AssertJUnit;

import org.tdod.ether.ta.manager.WorldManager;
import org.tdod.ether.ta.player.enums.PlayerClass;
import org.tdod.ether.ta.spells.Spell;
import org.tdod.ether.taimpl.spells.enums.SpellTarget;
import org.tdod.ether.util.TaMessageManager;
import org.tdod.ether.util.TestUtil;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DoCastTest extends AbstractCommandTest {

   /**
    * Sets up the unit test.
    */
   @BeforeClass
   public void setUp() {
      super.commandSetUp();
      setCommand(new DoCast());
   }

   /**
    * Runs the test case.
    */
   @Test(groups = { "unit" })
   public void testCase() {
      getPlayer().teleportToRoom(NORTH_PLAZA);
      getPlayerA().teleportToRoom(NORTH_PLAZA);
      clearAllOutput();

      getPlayer().setPlayerClass(PlayerClass.WARRIOR);
      cast();

      getPlayer().setPlayerClass(PlayerClass.ACOLYTE);
      getPlayer().getMana().setCurMana(100);
      getPlayer().getMana().setMaxMana(100);
      cast();
      
      Spell spell = WorldManager.getSpell("motu");
      getPlayer().getSpellbook().getSpells().add(spell);
      
      if (!getCommand().execute(getPlayer(), "cast motu " + getPlayer().getName())) {
         AssertJUnit.fail();
      }
      TestUtil.assertContains(getPlayerOutput(), "You intoned the spell for " + getPlayer().getName() + " which healed");
      String playerAOutput = MessageFormat.format(TaMessageManager.HELOTH.getMessage(), getPlayer().getName(), "a minor healing spell", getPlayer().getName());
      TestUtil.assertOutput(getPlayerAOutput(), playerAOutput);
      
   }

   /**
    * Misrouted charm spell data should fail as a normal targeted-spell input error.
    */
   @Test(groups = { "unit" })
   public void testMisroutedCharmDoesNotShowAdminError() {
      getPlayer().teleportToRoom(NORTH_PLAZA);
      getPlayer().setPlayerClass(PlayerClass.ACOLYTE);
      getPlayer().getMana().setCurMana(100);
      getPlayer().getMana().setMaxMana(100);
      getPlayer().setMentalExhaustionTicker(0);
      clearAllOutput();

      Spell spell = WorldManager.getSpell("novadidan");
      SpellTarget originalTarget = spell.getSpellTarget();
      spell.setSpellTarget(SpellTarget.ROOM_MOB);
      getPlayer().getSpellbook().getSpells().add(spell);

      try {
         if (!getCommand().execute(getPlayer(), "cast novadidan")) {
            AssertJUnit.fail();
         }
         TestUtil.assertOutput(getPlayerOutput(), TaMessageManager.SNDTRG.getMessage());
      } finally {
         spell.setSpellTarget(originalTarget);
         getPlayer().getSpellbook().getSpells().remove(spell);
      }
   }

   /**
    * Misrouted summon spell data should fail as a normal no-target spell input error.
    */
   @Test(groups = { "unit" })
   public void testMisroutedSummonDoesNotShowAdminError() {
      getPlayer().teleportToRoom(NORTH_PLAZA);
      getPlayer().setPlayerClass(PlayerClass.ACOLYTE);
      getPlayer().getMana().setCurMana(100);
      getPlayer().getMana().setMaxMana(100);
      getPlayer().setMentalExhaustionTicker(0);
      clearAllOutput();

      Spell spell = WorldManager.getSpell("muda");
      SpellTarget originalTarget = spell.getSpellTarget();
      spell.setSpellTarget(SpellTarget.SPECIFIED);
      getPlayer().getSpellbook().getSpells().add(spell);

      try {
         if (!getCommand().execute(getPlayer(), "cast muda " + getPlayerA().getName())) {
            AssertJUnit.fail();
         }
         TestUtil.assertOutput(getPlayerOutput(), TaMessageManager.SDNTRG.getMessage());
      } finally {
         spell.setSpellTarget(originalTarget);
         getPlayer().getSpellbook().getSpells().remove(spell);
      }
   }
   
   private void cast() {
      if (getCommand().execute(getPlayer(), "cast")) {
         AssertJUnit.fail();
      }

      if (!getCommand().execute(getPlayer(), "cast asdf")) {
         AssertJUnit.fail();
      }
      TestUtil.assertOutput(getPlayerOutput(), TaMessageManager.NOSPEL.getMessage());
      TestUtil.assertOutput(getPlayerAOutput(), "");
   }
}
