package com.fredplugins.gauntletV2.overlay

import java.awt.geom.AffineTransform
import java.awt.{Polygon, Rectangle, Shape}
import scala.util.chaining.*

object OverlayUtils {
  def rectangleToPolygon(rect: Rectangle): Polygon = {
    new Polygon(
      Array(rect.x, rect.x + rect.width, rect.x + rect.width, rect.x),
      Array(rect.y, rect.y, rect.y + rect.height, rect.y + rect.height),
      4
    )
  }
}
