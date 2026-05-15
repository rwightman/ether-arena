package org.tdod.ether.taimpl.commands;

import org.tdod.ether.ta.player.Player;
import org.tdod.ether.util.PropertiesManager;
import org.tdod.ether.util.TaMessageManager;

/**
 * Applies the original user-to-user combat configuration gates.
 */
final class PlayerCombatPolicy {

   /**
    * Private constructor to enforce utility class behavior.
    */
   private PlayerCombatPolicy() {
   }

   /**
    * Gets a rebuff message for an attack, or null if it is allowed.
    * @param attacker the attacking player.
    * @param target the target player.
    * @return the rebuff message, or null.
    */
   static String getAttackRebuff(Player attacker, Player target) {
      TaMessageManager message = getAttackRebuffMessage(attacker, target, Settings.fromProperties());
      if (message == null) {
         return null;
      }
      return message.getMessage();
   }

   /**
    * Gets a rebuff message for a robbery, or null if it is allowed.
    * @param robber the robbing player.
    * @param target the target player.
    * @return the rebuff message, or null.
    */
   static String getRobRebuff(Player robber, Player target) {
      TaMessageManager message = getRobRebuffMessage(robber, target, Settings.fromProperties());
      if (message == null) {
         return null;
      }
      return message.getMessage();
   }

   /**
    * Gets the global robbery rebuff message, or null if robbery is enabled.
    * @return the rebuff message, or null.
    */
   static String getRobDisabledRebuff() {
      if (Settings.fromProperties().isUserRobEnabled()) {
         return null;
      }
      return TaMessageManager.NORUSR.getMessage();
   }

   /**
    * Gets a rebuff message for an attack.
    * @param attacker the attacking player.
    * @param target the target player.
    * @param settings combat settings.
    * @return the rebuff message, or null.
    */
   static TaMessageManager getAttackRebuffMessage(Player attacker, Player target, Settings settings) {
      if (!settings.isUserAttackEnabled()) {
         return TaMessageManager.NOAUSR;
      }
      return getCombatRuleRebuff(attacker, target, settings);
   }

   /**
    * Gets a rebuff message for a robbery.
    * @param robber the robbing player.
    * @param target the target player.
    * @param settings combat settings.
    * @return the rebuff message, or null.
    */
   static TaMessageManager getRobRebuffMessage(Player robber, Player target, Settings settings) {
      if (!settings.isUserRobEnabled()) {
         return TaMessageManager.NORUSR;
      }
      return getCombatRuleRebuff(robber, target, settings);
   }

   /**
    * Applies the original level and promotion combat restrictions.
    * @param actor the player starting combat.
    * @param target the target player.
    * @param settings combat settings.
    * @return the rebuff message, or null.
    */
   private static TaMessageManager getCombatRuleRebuff(Player actor, Player target, Settings settings) {
      if (isExempt(actor, target, settings.getCombatExemptionLevel())) {
         return null;
      }

      if (!settings.isPromotedCombatEnabled() && actor.isPromoted() != target.isPromoted()) {
         if (actor.isPromoted()) {
            return TaMessageManager.UTOOHI;
         }
         return TaMessageManager.UTOOLO;
      }

      if (settings.getNoCombatMaxLevel() > 0) {
         if (target.getLevel() <= settings.getNoCombatMaxLevel()) {
            return TaMessageManager.UTOOHI;
         }
         if (actor.getLevel() <= settings.getNoCombatMaxLevel()) {
            return TaMessageManager.UTOOLO;
         }
      }

      if (settings.getUserCombatLevelDifference() > 0) {
         int difference = actor.getLevel() - target.getLevel();
         if (Math.abs(difference) >= settings.getUserCombatLevelDifference()) {
            if (difference > 0) {
               return TaMessageManager.UTOOHI;
            }
            return TaMessageManager.UTOOLO;
         }
      }

      return null;
   }

   /**
    * Determines if both players are outside configured combat protection.
    * @param actor the player starting combat.
    * @param target the target player.
    * @param exemptionLevel the exemption level.
    * @return true if both players are exempt.
    */
   private static boolean isExempt(Player actor, Player target, int exemptionLevel) {
      if (exemptionLevel <= 0) {
         return false;
      }
      return actor.getLevel() >= exemptionLevel && target.getLevel() >= exemptionLevel;
   }

   /**
    * Combat settings.
    */
   static final class Settings {
      private boolean _userAttackEnabled;
      private boolean _userRobEnabled;
      private int     _userCombatLevelDifference;
      private boolean _promotedCombatEnabled;
      private int     _combatExemptionLevel;
      private int     _noCombatMaxLevel;

      /**
       * Constructor.
       * @param userAttackEnabled allows user attack.
       * @param userRobEnabled allows user rob.
       * @param userCombatLevelDifference maximum allowed level difference, or zero.
       * @param promotedCombatEnabled allows promoted versus non-promoted combat.
       * @param combatExemptionLevel level where combat protection stops, or zero.
       * @param noCombatMaxLevel maximum protected level, or zero.
       */
      Settings(boolean userAttackEnabled, boolean userRobEnabled, int userCombatLevelDifference,
            boolean promotedCombatEnabled, int combatExemptionLevel, int noCombatMaxLevel) {
         _userAttackEnabled = userAttackEnabled;
         _userRobEnabled = userRobEnabled;
         _userCombatLevelDifference = userCombatLevelDifference;
         _promotedCombatEnabled = promotedCombatEnabled;
         _combatExemptionLevel = combatExemptionLevel;
         _noCombatMaxLevel = noCombatMaxLevel;
      }

      /**
       * Gets settings from the active properties file.
       * @return configured settings.
       */
      static Settings fromProperties() {
         PropertiesManager properties = PropertiesManager.getInstance();
         return new Settings(
               getBoolean(properties, PropertiesManager.USER_ATTACK_ENABLED),
               getBoolean(properties, PropertiesManager.USER_ROB_ENABLED),
               getInt(properties, PropertiesManager.USER_COMBAT_LEVEL_DIFFERENCE),
               getBoolean(properties, PropertiesManager.PROMOTED_COMBAT_ENABLED),
               getInt(properties, PropertiesManager.COMBAT_EXEMPTION_LEVEL),
               getInt(properties, PropertiesManager.NO_COMBAT_MAX_LEVEL));
      }

      boolean isUserAttackEnabled() {
         return _userAttackEnabled;
      }

      boolean isUserRobEnabled() {
         return _userRobEnabled;
      }

      int getUserCombatLevelDifference() {
         return _userCombatLevelDifference;
      }

      boolean isPromotedCombatEnabled() {
         return _promotedCombatEnabled;
      }

      int getCombatExemptionLevel() {
         return _combatExemptionLevel;
      }

      int getNoCombatMaxLevel() {
         return _noCombatMaxLevel;
      }

      private static boolean getBoolean(PropertiesManager properties, String key) {
         String value = properties.getProperty(key);
         return Boolean.valueOf(value).booleanValue() || "yes".equalsIgnoreCase(value) || "1".equals(value);
      }

      private static int getInt(PropertiesManager properties, String key) {
         return Integer.valueOf(properties.getProperty(key)).intValue();
      }
   }
}
