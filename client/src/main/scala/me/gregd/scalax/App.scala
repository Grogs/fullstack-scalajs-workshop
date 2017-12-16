package me.gregd.scalax

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

  //Conference cards container
  def eventListings() = document.getElementById("event-listings")

  //Query
  def querySelect() = document.getElementById("query").asInstanceOf[Select]
  def currentQuery() = Query.withNameInsensitive(querySelect().value)

  //Map related elements
  def mapModal() = document.getElementById("mapModal")
  def mapContainer() = document.getElementById("map")
  def showMapButton() = document.getElementById("show-map").asInstanceOf[Button]
  def elementsThatShowOrHideTheMap() = document.querySelectorAll(".toggle-map-modal")
  def toggleMapModal() = mapModal().classList.toggle("is-active")

  @JSExport
  def main(): Unit = {
    println("Hello from Scala.js")

    querySelect().onchange = (e) => refreshConferences(currentQuery())


    showMapButton().addEventListener("click", onMapOpen(_))

    elementsThatShowOrHideTheMap().foreach(node =>
      node.addEventListener("click", (_: Event) => toggleMapModal())
    )

    def onMapOpen(e: Event) = {

      val eventualConferences = Client[ConferenceService].search(currentQuery()).call()

      val eventualConferencesAndCoordinates = eventualConferences.flatMap(conferences =>
        Future.traverse(conferences) { conf =>
          val coordinates = Client[GeolocationService].location(conf).call()
          coordinates.map( coord =>
            conf -> coord
          )
        }
      )
      eventualConferencesAndCoordinates.foreach { conferences =>
        GoogleMaps.render(conferences, mapContainer())
      }
    }

  }

  def refreshConferences(query: Query): Unit = {
    for {
      events <- Client[ConferenceService].search(query).call() //Note the .call()
      table = views.html.eventsTable(events).body //Yay, reused code across frontend and backend!
    } {
      eventListings().innerHTML = table
    }
  }
}