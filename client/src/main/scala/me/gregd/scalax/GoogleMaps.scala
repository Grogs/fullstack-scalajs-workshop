package me.gregd.scalax

import google.maps.InfoWindowOptions
import model.{Conference, Coordinates}
import org.scalajs.dom.Element

import scala.scalajs.js

object GoogleMaps {
  def render(events: Seq[(Conference, Coordinates)], target: Element): Unit = {
    //Put Google Maps code here. See README.
    println("Google Map triggered.")
  }
}
