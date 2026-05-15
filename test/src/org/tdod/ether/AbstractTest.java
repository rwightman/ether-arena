package org.tdod.ether;

import org.testng.AssertJUnit;

import org.tdod.ether.util.TestUtil;

public class AbstractTest {

   public void baseSetUp() {
      try {
         TestUtil.initializeWorld();
      } catch (Exception e) {
         e.printStackTrace();
         AssertJUnit.fail(e.getMessage());
      }

   }

}
