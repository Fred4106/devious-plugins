package com.fredplugins.gauntletV2.overlay

import com.github.zafarkhaja.semver.expr.CompositeExpression.Helper
import com.google.inject.Inject
import net.runelite.client.plugins.Plugin
import net.runelite.client.ui.overlay.OverlayLayer

import java.awt.{BasicStroke, Color, Graphics2D, Shape, Stroke}

object Overlay {
  trait OverlayDrawHelpers {
    def drawOutlineAndFill(graphics2D: Graphics2D, outlineColor: Color, fillColor: Color, strokeWidth: Float, shape: Shape): Unit = {
      val originalColor = graphics2D.getColor
      val originalStroke = graphics2D.getStroke
      graphics2D.setStroke(new BasicStroke(strokeWidth))
      graphics2D.setColor(outlineColor)
      graphics2D.draw(shape)
      graphics2D.setColor(fillColor)
      graphics2D.fill(shape)
      graphics2D.setColor(originalColor)
      graphics2D.setStroke(originalStroke)
    }
  }
}

abstract class Overlay(plugin: Plugin) extends net.runelite.client.ui.overlay.Overlay(plugin: Plugin) {
  def this(plugin: Plugin, layer: OverlayLayer) = {
    this(plugin)
    setLayer(layer)
  }

  def drawOutlineAndFill(graphics2D: Graphics2D, outlineColor: Color, fillColor: Color, strokeWidth: Float, shape: Shape): Unit = {
    val originalColor = graphics2D.getColor
    val originalStroke = graphics2D.getStroke
    graphics2D.setStroke(new BasicStroke(strokeWidth))
    graphics2D.setColor(outlineColor)
    graphics2D.draw(shape)
    graphics2D.setColor(fillColor)
    graphics2D.fill(shape)
    graphics2D.setColor(originalColor)
    graphics2D.setStroke(originalStroke)
  }
}