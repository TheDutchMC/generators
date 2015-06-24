/*
 * Copyright (c) bdew, 2015
 * https://github.com/bdew/generators
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.generators.control

import net.bdew.lib.multiblock.tile.TileController

trait CIControl extends TileController {
  def availableControlActions: List[ControlAction]
  def onControlStateChanged(): Unit
}

trait CIControlCached extends CIControl {
  var controlState = Map.empty[ControlAction, ControlResult.Value].withDefaultValue(ControlResult.NEUTRAL)

  override def onControlStateChanged(): Unit = {
    val controlModules = modules.flatMap(_.getTile[MIControl](getWorldObject))
    val result = for (action <- availableControlActions) yield {
      val results = controlModules.map(_.getControlState(action))
      action -> (
        if (results.contains(ControlResult.ENABLED))
          ControlResult.ENABLED
        else if (results.contains(ControlResult.DISABLED))
          ControlResult.DISABLED
        else
          ControlResult.NEUTRAL
        )
    }
    controlState = result.toMap.withDefaultValue(ControlResult.NEUTRAL)
  }

  override def onModulesChanged(): Unit = {
    onControlStateChanged()
  }

}