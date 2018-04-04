package me.gregd.fullstack_workshop

import autowire._
import model.Sort.{Chronological, Geographical}
import model.{Conference, Query, Sort}
import org.scalajs.dom.ext.PimpedNodeList
import org.scalajs.dom.html.{Button, Select}
import org.scalajs.dom.{Event, document}
import services.{Geolocation, GoogleMaps}
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
  //Sort
  def sortSelect() = document.getElementById("sort").asInstanceOf[Select]
  def currentSort() = Sort.withNameInsensitive(sortSelect().value)

  //Map related elements
  def mapModal() = document.getElementById("mapModal")
  def mapContainer() = document.getElementById("map")
  def showMapButton() = document.getElementById("show-map").asInstanceOf[Button]
  def elementsThatShowOrHideTheMap() = document.querySelectorAll(".toggle-map-modal")
  def toggleMapModal() = mapModal().classList.toggle("is-active")

  @JSExport
  def main(): Unit = {
    println("Hello from Scala.js")

    querySelect().onchange = (e) => reload(currentQuery(), currentSort())
    sortSelect().onchange = (e) => reload(currentQuery(), currentSort())


    showMapButton().addEventListener("click", onMapOpen(_))

    elementsThatShowOrHideTheMap().foreach(node =>
      node.addEventListener("click", (_: Event) => toggleMapModal())
    )

    def onMapOpen(e: Event) = {
      Client[ConferenceService].search(currentQuery()).call().foreach { events =>
        GoogleMaps.render(events, mapContainer())
      }
    }
  }

  def reload(query: Query, sort: Sort) = {
    for {
      events <- Client[ConferenceService].search(query).call() //Note the .call()
      sortedEvents <- sortConferences(sort)(events)
      table = views.html.eventsTable(sortedEvents).body //Yay, reused code across frontend and backend!
    } {
      eventListings().innerHTML = table
    }
  }

  def sortConferences(sort: Sort)(conferences: Seq[Conference]): Future[Seq[Conference]] = {
    sort match {
      case Chronological => Future.successful(conferences.sortBy(_.startDate.toEpochDay))
      case Geographical => {

        val eventualLocations = Future.traverse(conferences) { conf =>
          Client[GeolocationService].location(conf).call().map(conf -> _)
        }.map(_.toMap)

        for {
          clientLocation <- Geolocation.getCurrentPosition()
          locations <- eventualLocations
        } yield
          conferences.sortBy(conf => Geolocation.distance(locations(conf), clientLocation))
      }
    }

  }
}


