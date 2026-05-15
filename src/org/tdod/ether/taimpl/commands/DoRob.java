/****************************************************************************
 * Ether Code base, version 1.0                                             *
 *==========================================================================*
 * Copyright (C) 2011 by Ron Kinney                                         *
 * All rights reserved.                                                     *
 *                                                                          *
 * This program is free software; you can redistribute it and/or modify     *
 * it under the terms of the GNU General Public License as published        *
 * by the Free Software Foundation; either version 2 of the License, or     *
 * (at your option) any later version.                                      *
 *                                                                          *
 * This program is distributed in the hope that it will be useful,          *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of           *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the            *
 * GNU General Public License for more details.                             *
 *                                                                          *
 * Redistribution and use in source and binary forms, with or without       *
 * modification, are permitted provided that the following conditions are   *
 * met:                                                                     *
 *                                                                          *
 * * Redistributions of source code must retain this copyright notice,      *
 *   this list of conditions and the following disclaimer.                  *
 * * Redistributions in binary form must reproduce this copyright notice    *
 *   this list of conditions and the following disclaimer in the            *
 *   documentation and/or other materials provided with the distribution.   *
 *                                                                          *
 *==========================================================================*
 * Ron Kinney (ronkinney@gmail.com)                                         *
 * Ether Homepage:   http://tdod.org/ether                                  *
 ****************************************************************************/

package org.tdod.ether.taimpl.commands;

import java.text.MessageFormat;

import org.tdod.ether.ta.Entity;
import org.tdod.ether.ta.EntityType;
import org.tdod.ether.ta.commands.Command;
import org.tdod.ether.ta.cosmos.Room;
import org.tdod.ether.ta.items.Item;
import org.tdod.ether.ta.player.Player;
import org.tdod.ether.ta.player.enums.InventoryFailCode;
import org.tdod.ether.ta.player.enums.PlayerClass;
import org.tdod.ether.taimpl.cosmos.RoomFlags;
import org.tdod.ether.util.Dice;
import org.tdod.ether.util.TaMessageManager;

/**
 * Handles rogue robbery attempts against other players.
 */
public class DoRob extends Command {

   /**
    * Minimum level needed to rob another player.
    */
   private static final int MIN_ROB_LEVEL = 2;

   /**
    * Base rob chance before stats, level, and target resistance.
    */
   private static final int BASE_ROB_CHANCE = 10;

   /**
    * Agility contribution multiplier.
    */
   private static final int AGILITY_MULTIPLIER = 2;

   /**
    * Level contribution multiplier.
    */
   private static final int LEVEL_MULTIPLIER = 2;

   /**
    * Minimum chance to rob.
    */
   private static final int MIN_ROB_CHANCE = 5;

   /**
    * Item robbery is harder than gold robbery.
    */
   private static final int ITEM_ROB_PENALTY = 35;

   /**
    * A successful gold robbery steals up to one fifth of the victim's gold.
    */
   private static final int GOLD_ROB_DIVISOR = 5;

   /**
    * Victims need at least this much gold before any can be stolen.
    */
   private static final int MIN_GOLD_TO_ROB = 5;

   /**
    * A successful robbery can still be noticed by the victim.
    */
   private static final int SUCCESS_SPOTTED_ROLL = 75;

   /**
    * The command grammar uses "of" between victim and target.
    */
   private static final String OF_KEYWORD = "of";

   /**
    * Keyword suffix for robbing gold.
    */
   private static final String GOLD_KEYWORD = "gold";

   /**
    * Executes the "rob" command.
    *
    * @param entity The entity executing the command.
    * @param input The input string.
    *
    * @return true if the command can be executed, false otherwise.
    */
   public final boolean execute(Entity entity, String input) {
      if (!entity.getEntityType().equals(EntityType.PLAYER)) {
         return false;
      }
      Player player = (Player) entity;

      String[] split = input.split(" ", FOUR_PARAMS);
      if (split.length < FOUR_PARAMS || !split[2].equalsIgnoreCase(OF_KEYWORD)) {
         return false;
      }

      String targetName = split[1];
      String targetObject = split[THREE_PARAMS].trim();
      if (targetObject.length() == 0) {
         return false;
      }

      String disabledRebuff = PlayerCombatPolicy.getRobDisabledRebuff();
      if (disabledRebuff != null) {
         player.print(disabledRebuff);
         return true;
      }

      Room room = player.getRoom();

      if (RoomFlags.SAFE.isSet(room.getRoomFlags())) {
         player.print(TaMessageManager.NOROBH.getMessage());
         return true;
      }

      if (!room.isIlluminated()) {
         player.print(TaMessageManager.TOODRK.getMessage());
         return true;
      }

      if (!player.getPlayerClass().equals(PlayerClass.ROGUE)) {
         String message = MessageFormat.format(TaMessageManager.CNTROB.getMessage(),
               player.getPlayerClass().getName().toLowerCase() + "s");
         player.print(message);
         return true;
      }

      if (player.getLevel() < MIN_ROB_LEVEL) {
         player.print(TaMessageManager.CRBYET.getMessage());
         return true;
      }

      if (player.isResting()) {
         player.print(TaMessageManager.ATTEXH.getMessage());
         return true;
      }

      if (player.isAttacking()) {
         player.print(TaMessageManager.LEVCBT.getMessage());
         return true;
      }

      Player target = room.getPlayerInRoom(targetName);
      if (target == null) {
         String message = MessageFormat.format(TaMessageManager.ARNNHR.getMessage(), targetName);
         player.print(message);
         return true;
      }

      if (player.equals(target)) {
         player.print(TaMessageManager.NORSLF.getMessage());
         return true;
      }

      String rebuff = PlayerCombatPolicy.getRobRebuff(player, target);
      if (rebuff != null) {
         player.print(rebuff);
         return true;
      }

      if (targetObject.equalsIgnoreCase(GOLD_KEYWORD)) {
         return handleGold(player, target);
      }

      return handleItem(player, target, targetObject);
   }

   /**
    * Handles a gold robbery attempt.
    *
    * @param player The player issuing the command.
    * @param target The target player.
    *
    * @return true if the command can be executed.
    */
   private synchronized boolean handleGold(Player player, Player target) {
      int roll = Dice.roll(1, Dice.MAX_PERCENTAGE);
      if (roll > getRobChance(player, target, 0)) {
         displayCaughtTrying(player, target);
         return true;
      }

      if (target.getGold() < MIN_GOLD_TO_ROB) {
         String message = MessageFormat.format(TaMessageManager.NOGOLD.getMessage(), target.getName());
         player.print(message);
         return true;
      }

      int amount = Dice.roll(1, target.getGold() / GOLD_ROB_DIVISOR);
      int overflow = player.addGold(amount);
      if (overflow > 0) {
         player.subtractGold(amount - overflow);
         player.print(TaMessageManager.BNKNCA.getMessage());
         return true;
      }

      target.subtractGold(amount);

      String message = MessageFormat.format(TaMessageManager.GOTGLD.getMessage(), amount, target.getName());
      player.print(message);
      displayCaughtOnSuccess(player, target, roll);

      return true;
   }

   /**
    * Handles an item robbery attempt.
    *
    * @param player The player issuing the command.
    * @param target The target player.
    * @param itemName The item name.
    *
    * @return true if the command can be executed.
    */
   private synchronized boolean handleItem(Player player, Player target, String itemName) {
      int roll = Dice.roll(1, Dice.MAX_PERCENTAGE);
      if (roll > getRobChance(player, target, ITEM_ROB_PENALTY)) {
         displayCaughtTrying(player, target);
         return true;
      }

      Item item = target.getItem(itemName);
      if (item == null) {
         String message = MessageFormat.format(TaMessageManager.NOITEM.getMessage(), target.getName());
         player.print(message);
         return true;
      }

      InventoryFailCode failCode = player.placeItemInInventory(item, false);
      if (failCode.equals(InventoryFailCode.SPACE)) {
         player.print(TaMessageManager.INVFUL.getMessage());
         return true;
      }
      if (failCode.equals(InventoryFailCode.ENCUMBRANCE)) {
         player.print(TaMessageManager.TOOHVY.getMessage());
         return true;
      }

      target.removeItemFromInventory(item);

      String message = MessageFormat.format(TaMessageManager.GOTOBJ.getMessage(), item.getName(), target.getName());
      player.print(message);
      displayCaughtOnSuccess(player, target, roll);

      return true;
   }

   /**
    * Gets the rob chance for this attempt.
    *
    * @param player The player issuing the command.
    * @param target The target player.
    * @param itemPenalty The penalty to apply.
    *
    * @return the robbery chance.
    */
   private int getRobChance(Player player, Player target, int itemPenalty) {
      int chance = BASE_ROB_CHANCE
         + player.getStats().getKnowledge().getModifiedStat()
         + player.getStats().getAgility().getModifiedStat() * AGILITY_MULTIPLIER
         + player.getLevel() * LEVEL_MULTIPLIER
         - target.getStats().getIntellect().getModifiedStat()
         - itemPenalty;

      if (chance < MIN_ROB_CHANCE) {
         return MIN_ROB_CHANCE;
      }
      return chance;
   }

   /**
    * Displays messages for a failed robbery attempt.
    *
    * @param player The player issuing the command.
    * @param target The target player.
    */
   private void displayCaughtTrying(Player player, Player target) {
      Room room = player.getRoom();
      String messageToPlayer = MessageFormat.format(TaMessageManager.GOTCOT.getMessage(), target.getName());
      String messageToTarget = MessageFormat.format(TaMessageManager.CAUGHTT.getMessage(), player.getName());
      String messageToRoom = MessageFormat.format(TaMessageManager.OTHCOT.getMessage(),
            player.getName(), target.getName());

      player.print(messageToPlayer);
      target.print(messageToTarget);
      room.print(player, target, messageToRoom, false);
   }

   /**
    * Displays the target-only caught message for a spotted successful robbery.
    *
    * @param player The player issuing the command.
    * @param target The target player.
    * @param roll The successful robbery roll.
    */
   private void displayCaughtOnSuccess(Player player, Player target, int roll) {
      if (roll >= SUCCESS_SPOTTED_ROLL) {
         String messageToTarget = MessageFormat.format(TaMessageManager.CAUGHTS.getMessage(), player.getName());
         target.print(messageToTarget);
      }
   }
}
