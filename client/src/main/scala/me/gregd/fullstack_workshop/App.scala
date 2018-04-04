package me.gregd.fullstack_workshop

import autowire._
import model.Sort.{Chronological, Geographical}
import model.{Conference, Query, Sort}
import org.scalajs.dom.ext.PimpedNodeList
import org.scalajs.dom.html.{Button, Select}
import org.scalajs.dom.{Event, document}
import services.events.{ConferenceService, GeolocationService}

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}


@JSExportTopLevel("App")
object App {
  @JSExport
  def main(): Unit = {
    println("Hello from Scala.js")
  }
}