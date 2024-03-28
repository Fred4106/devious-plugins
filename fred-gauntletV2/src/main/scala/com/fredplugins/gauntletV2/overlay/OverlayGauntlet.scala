package com.fredplugins.gauntletV2.overlay

import com.fredplugins.gauntletV2.{GauntletPluginV3, GauntletV2Config}
import com.google.inject.Singleton
import com.google.inject.{Inject, Provides}
import net.runelite.api.Client
import net.runelite.client.ui.overlay.OverlayLayer
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer

import java.awt.{Dimension, Graphics2D}

@Singleton
class OverlayGauntlet @Inject (client: Client, plugin: GauntletPluginV3, config: GauntletV2Config, modelOutlineRenderer: ModelOutlineRenderer) extends Overlay(plugin, OverlayLayer.UNDER_WIDGETS) {

  override def render(graphics2D: Graphics2D): Dimension = {
    null
  }
}
