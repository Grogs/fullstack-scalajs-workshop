package me.gregd.fullstack_workshop

import model.{Conference, Coordinates}
import org.scalajs.dom.Element

object GoogleMaps {
  def render(events: Seq[(Conference, Coordinates)], target: Element): Unit = {
    //Put Google Maps code here. See README.
    println("Google Map triggered.")
  }
}
