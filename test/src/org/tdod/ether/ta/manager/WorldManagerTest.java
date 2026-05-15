package org.tdod.ether.ta.manager;

import java.util.ArrayList;

import org.tdod.ether.ta.player.PlayerConnection;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

public class WorldManagerTest {

   @Test(groups = { "unit" })
   public void testPlayerSnapshotIsIndependentOfPlayerList() {
      ArrayList<PlayerConnection> players = new ArrayList<PlayerConnection>();
      players.add(null);
      WorldManager.setPlayers(players);

      ArrayList<PlayerConnection> snapshot = WorldManager.getPlayerSnapshot();
      players.clear();

      AssertJUnit.assertEquals(1, snapshot.size());
      AssertJUnit.assertEquals(0, WorldManager.getPlayers().size());
   }

}
