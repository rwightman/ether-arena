package org.tdod.ether.taimpl.commands.handler;

import org.testng.AssertJUnit;

import org.tdod.ether.output.MockOutput;
import org.tdod.ether.ta.items.armor.Armor;
import org.tdod.ether.ta.items.weapons.Weapon;
import org.tdod.ether.ta.manager.WorldManager;
import org.tdod.ether.ta.player.Player;
import org.tdod.ether.ta.player.enums.PlayerClass;
import org.tdod.ether.ta.player.enums.RaceEnum;
import org.tdod.ether.taimpl.player.DefaultPlayer;
import org.tdod.ether.util.TestUtil;
import org.testng.annotations.Test;

public class HandlePromotionTest {

   @Test(groups = { "unit" })
   public void testPromotionUsesOriginalLevelNumberingAndCost() {
      Player player = createPromotionReadyPlayer();

      HandlePromotion.execute(player);

      AssertJUnit.assertTrue(player.isPromoted());
      AssertJUnit.assertEquals(PlayerClass.KNIGHT, player.getPromotedClass());
      AssertJUnit.assertEquals(HandlePromotion.PROMOTION_LEVEL + 1, player.getLevel());
      AssertJUnit.assertEquals(1, player.getPromotedLevel());
      AssertJUnit.assertEquals(0L, player.getExperience());
      AssertJUnit.assertEquals(0, player.getGold());
      AssertJUnit.assertTrue(player.getVitality().getMaxVitality() > 0);
      AssertJUnit.assertEquals(player.getVitality().getMaxVitality(), player.getVitality().getCurVitality());
   }

   private Player createPromotionReadyPlayer() {
      TestUtil.initializeWorld();
      Player player = new DefaultPlayer();
      player.setIgnoreTrip(true);
      player.setName("PromotionTest" + System.nanoTime());
      player.setLevel(HandlePromotion.PROMOTION_LEVEL);
      player.setOutput(new MockOutput());
      player.setPlayerClass(PlayerClass.WARRIOR);
      player.setRace(RaceEnum.HUMAN);
      player.setWeapon((Weapon) WorldManager.getDefaultWeapon().clone("test default weapon"));
      player.setArmor((Armor) WorldManager.getDefaultArmor().clone("test default armor"));
      player.setGold(1000);
      player.setExperience(12345);
      player.getVitality().setCurVitality(100);
      player.getVitality().setMaxVitality(100);
      player.setRoom(TestUtil.NORTH_PLAZA_ROOM);
      WorldManager.getRoom(TestUtil.NORTH_PLAZA_ROOM).addPlayer(player);
      return player;
   }
}
