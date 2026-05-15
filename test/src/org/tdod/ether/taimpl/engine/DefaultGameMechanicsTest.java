package org.tdod.ether.taimpl.engine;

import org.testng.AssertJUnit;

import org.tdod.ether.output.MockOutput;
import org.tdod.ether.ta.cosmos.Trap;
import org.tdod.ether.ta.items.armor.Armor;
import org.tdod.ether.ta.items.Item;
import org.tdod.ether.ta.items.weapons.Weapon;
import org.tdod.ether.ta.manager.WorldManager;
import org.tdod.ether.ta.mobs.Mob;
import org.tdod.ether.ta.player.Player;
import org.tdod.ether.ta.player.enums.PlayerClass;
import org.tdod.ether.ta.player.enums.RaceEnum;
import org.tdod.ether.taimpl.cosmos.DefaultTrap;
import org.tdod.ether.taimpl.cosmos.enums.TrapType;
import org.tdod.ether.taimpl.mobs.DefaultMob;
import org.tdod.ether.taimpl.mobs.enums.SpecialAbilityEnum;
import org.tdod.ether.taimpl.player.DefaultPlayer;
import org.tdod.ether.util.TestUtil;
import org.testng.annotations.Test;

public class DefaultGameMechanicsTest {

   private static final int DEATH_ROOM_AWAKEN = -96;
   private static final int SOULSTONE_VNUM = 65;
   private static final int RETAINED_ITEM_VNUM = 25;

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

   @Test(groups = { "unit" })
   public void testDeathWithoutSoulstoneAppliesPenaltyAndRecoveryState() {
      DefaultGameMechanics mechanics = createGameMechanics();
      Player player = createWorldPlayer();
      player.setExperience(1000);
      player.setGold(500);
      player.getVitality().setCurVitality(-10);

      mechanics.handlePlayerDeath(player, "death");

      AssertJUnit.assertEquals(DEATH_ROOM_AWAKEN, player.getRoom().getRoomNumber());
      AssertJUnit.assertTrue(player.isResting());
      AssertJUnit.assertEquals(player.getVitality().getMaxVitality(), player.getVitality().getCurVitality());
      AssertJUnit.assertEquals(750L, player.getExperience());
      AssertJUnit.assertEquals(0, player.getGold());
      AssertJUnit.assertEquals(0, player.getInventory().size());
   }

   @Test(groups = { "unit" })
   public void testSoulstoneDeathConsumesSoulstoneWithoutInventoryOrExperiencePenalty() {
      DefaultGameMechanics mechanics = createGameMechanics();
      Player player = createWorldPlayer();
      Item retainedItem = WorldManager.getItem(RETAINED_ITEM_VNUM).clone("test retained item");
      Item soulstone = WorldManager.getItem(SOULSTONE_VNUM).clone("test soulstone");
      player.placeItemInInventory(retainedItem, true);
      player.placeItemInInventory(soulstone, true);
      player.setExperience(1000);
      player.setGold(500);

      mechanics.handlePlayerDeath(player, "death");

      AssertJUnit.assertEquals(DEATH_ROOM_AWAKEN, player.getRoom().getRoomNumber());
      AssertJUnit.assertTrue(player.isResting());
      AssertJUnit.assertEquals(1000L, player.getExperience());
      AssertJUnit.assertEquals(500, player.getGold());
      AssertJUnit.assertFalse(hasInventoryItem(player, SOULSTONE_VNUM));
      AssertJUnit.assertTrue(hasInventoryItem(player, RETAINED_ITEM_VNUM));
   }

   @Test(groups = { "unit" })
   public void testUtilitySpellExperienceUsesOriginalClassScale() {
      DefaultGameMechanics mechanics = createGameMechanics();

      AssertJUnit.assertEquals(341, mechanics.getExpForUtilitySpells(createPlayer(PlayerClass.WARRIOR, 10, false), null));
      AssertJUnit.assertEquals(495, mechanics.getExpForUtilitySpells(createPlayer(PlayerClass.SORCEROR, 10, false), null));
      AssertJUnit.assertEquals(330, mechanics.getExpForUtilitySpells(createPlayer(PlayerClass.ROGUE, 10, false), null));
      AssertJUnit.assertEquals(434, mechanics.getExpForUtilitySpells(createPlayer(PlayerClass.WARRIOR, 26, true), null));
   }

   @Test(groups = { "unit" })
   public void testMonsterExperienceUsesOriginalDamagePercentFormula() {
      DefaultGameMechanics mechanics = createGameMechanics();
      Player player = createPlayer(PlayerClass.WARRIOR, 10, false);
      Mob mob = createMob(5, 100, 1000);

      AssertJUnit.assertEquals(100L, mechanics.calculateCombatExperience(player, mob, 10));
   }

   @Test(groups = { "unit" })
   public void testMonsterExperienceUsesOriginalMinimumAndCap() {
      DefaultGameMechanics mechanics = createGameMechanics();
      Player highLevelPlayer = createPlayer(PlayerClass.WARRIOR, 100, false);
      Player lowLevelPlayer = createPlayer(PlayerClass.WARRIOR, 2, false);

      AssertJUnit.assertEquals(5L, mechanics.calculateCombatExperience(highLevelPlayer, createMob(5, 100, 20), 10));
      AssertJUnit.assertEquals(6000L, mechanics.calculateCombatExperience(lowLevelPlayer, createMob(50, 100, 100000), 100));
   }

   @Test(groups = { "unit" })
   public void testMonsterExperiencePoolUsesOriginalGenerationFormula() {
      DefaultGameMechanics mechanics = createGameMechanics();
      Mob mob = createMob(10, 100, 0);
      mob.setHitDice(3);
      mob.getGeneralAttack().setMaxDamage(5);
      mob.getGeneralAttack().setNumAttacks(2);
      mob.getSpecialAttack().setMaxSpecialDamage(12);

      AssertJUnit.assertEquals(211L, mechanics.calculateMobExperiencePool(mob));

      mob.getSpecialAbility().setSpecialAbility(SpecialAbilityEnum.STEAL);
      AssertJUnit.assertEquals(1211L, mechanics.calculateMobExperiencePool(mob));
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

   private Player createPlayer(PlayerClass playerClass, int level, boolean promoted) {
      Player player = createPlayer(playerClass);
      player.setLevel(level);
      player.setPromoted(promoted);
      return player;
   }

   private Mob createMob(int level, int maxVitality, long experiencePool) {
      Mob mob = new DefaultMob();
      mob.setLevel(level);
      mob.getVitality().setCurVitality(maxVitality);
      mob.getVitality().setMaxVitality(maxVitality);
      mob.setExperiencePool(experiencePool);
      mob.getSpecialAbility().setSpecialAbility(SpecialAbilityEnum.NONE);
      return mob;
   }

   private Player createWorldPlayer() {
      TestUtil.initializeWorld();
      Player player = new DefaultPlayer();
      player.setIgnoreTrip(true);
      player.setName("DeathTest" + System.nanoTime());
      player.setLevel(20);
      player.setOutput(new MockOutput());
      player.setPlayerClass(PlayerClass.WARRIOR);
      player.setRace(RaceEnum.HUMAN);
      player.setWeapon((Weapon) WorldManager.getDefaultWeapon().clone("test default weapon"));
      player.setArmor((Armor) WorldManager.getDefaultArmor().clone("test default armor"));
      player.getVitality().setCurVitality(1000);
      player.getVitality().setMaxVitality(1000);
      player.setRoom(TestUtil.NORTH_PLAZA_ROOM);
      WorldManager.getRoom(TestUtil.NORTH_PLAZA_ROOM).addPlayer(player);
      return player;
   }

   private boolean hasInventoryItem(Player player, int vnum) {
      for (Item item : player.getInventory()) {
         if (item.getVnum() == vnum) {
            return true;
         }
      }
      return false;
   }

   private Trap createTrap(TrapType trapType) {
      Trap trap = new DefaultTrap();
      trap.setTrapType(trapType);
      return trap;
   }
}
