/****************************************************************************
 * Ether Code base, version 1.0                                             *
 *==========================================================================*
 * Copyright (C) 2011 by Ron Kinney                                         *
 * All rights reserved.                                                     *
 ****************************************************************************/

package org.tdod.ether.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Factory for XStream instances used to load Ether's XML data files.
 */
public final class XStreamFactory {

   private static final String[] ALLOWED_TYPES = new String[] {
      "org.tdod.ether.**"
   };

   /**
    * Utility class.
    */
   private XStreamFactory() {
   }

   /**
    * Creates an XStream instance backed by the default DOM driver.
    *
    * @return an XStream instance.
    */
   public static XStream create() {
      return create(new DomDriver());
   }

   /**
    * Creates an XStream instance backed by the specified driver.
    *
    * @param driver the stream driver.
    *
    * @return an XStream instance.
    */
   public static XStream create(HierarchicalStreamDriver driver) {
      XStream xstream = new XStream(driver);
      configureSecurity(xstream);
      return xstream;
   }

   /**
    * Configures XStream's explicit type allow-list when running with newer XStream versions.
    *
    * @param xstream the XStream instance.
    */
   private static void configureSecurity(XStream xstream) {
      try {
         XStream.class.getMethod("setupDefaultSecurity", new Class[] { XStream.class }).invoke(null, new Object[] { xstream });
         XStream.class.getMethod("allowTypesByWildcard", new Class[] { String[].class }).invoke(xstream, new Object[] { ALLOWED_TYPES });
      } catch (NoSuchMethodException exception) {
         // Ether ships XStream 1.3.1, before the explicit security API existed.
      } catch (Exception exception) {
         throw new IllegalStateException("Unable to configure XStream security.", exception);
      }
   }
}
