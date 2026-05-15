package org.tdod.ether.taimpl.items.weapons;

import org.testng.AssertJUnit;

import org.tdod.ether.AbstractTest;
import org.tdod.ether.ta.items.weapons.Weapon;
import org.tdod.ether.ta.items.weapons.WeaponDatabase;
import org.tdod.ether.taimpl.items.weapons.DefaultWeaponDatabase;
import org.tdod.ether.util.InvalidFileException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DefaultWeaponDatabaseTest extends AbstractTest {

   private WeaponDatabase _weaponDatabase;
   private static final int DEFAULT_WEAPON = 0;

   @BeforeClass
   public void setUp() {
      super.baseSetUp();      
      _weaponDatabase = new DefaultWeaponDatabase();
      try {
         _weaponDatabase.initialize();         
      } catch (InvalidFileException e) {
         AssertJUnit.fail("Error reading file.  Message was " + e.getMessage());
      }
   }

   @Test(groups = { "unit" })
   public void testCase() {
      Weapon weapon = _weaponDatabase.getWeapon(DEFAULT_WEAPON);
      Weapon defaultWeapon = _weaponDatabase.getDefaultWeapon();
      if (!weapon.equals(defaultWeapon)) {
         AssertJUnit.fail("Default weapon is not correct.");
      }
      
      Weapon weaponString = _weaponDatabase.getWeapon(weapon.getName());
      if (!weapon.equals(weaponString)) {
         AssertJUnit.fail("Getting weapon by string did not work.");
      }

   }

}
