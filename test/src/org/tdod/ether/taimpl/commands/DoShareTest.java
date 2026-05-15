package org.tdod.ether.taimpl.commands;

import junit.framework.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DoShareTest extends AbstractCommandTest {

   /**
    * Sets up the unit test.
    */
   @BeforeClass
   public void setUp() {
      super.commandSetUp();
      setCommand(new DoShare());
   }

   /**
    * Tests sharing gold with a group member in another room.
    */
   @Test(groups = { "unit" })
   public void testShareGoldAcrossRooms() {
      getPlayer().teleportToRoom(TEST_ROOM_1);
      getPlayerA().teleportToRoom(TEST_ROOM_2);
      clearAllOutput();

      getPlayer().setLevel(10);
      getPlayer().setGold(1000);
      getPlayerA().setGold(0);
      getPlayer().setGroupLeader(getPlayer());
      getPlayerA().setGroupLeader(getPlayer());
      getPlayer().getGroupList().add(getPlayerA());

      if (!getCommand().execute(getPlayer(), "share 100")) {
         Assert.fail();
      }

      Assert.assertEquals(950, getPlayer().getGold());
      Assert.assertEquals(50, getPlayerA().getGold());
   }
}
