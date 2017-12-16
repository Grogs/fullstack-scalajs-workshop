package me.gregd.scalax

import google.maps.InfoWindowOptions
import model.{Conference, Coordinates}
import org.scalajs.dom.Element

import scala.scalajs.js

object GoogleMaps {
  def render(events: Seq[(Conference, Coordinates)], target: Element): Unit = {
    val opts = google.maps.MapOptions(
      center = new google.maps.LatLng(50, 0),
      zoom = 11
    )

    val gmap = new google.maps.Map(target, opts)

    val markers =
      for ((event, coords) <- events) yield {
        val Coordinates(lat, long) = coords

        val latLng = new google.maps.LatLng(lat, long)
        val marker = new google.maps.Marker(
          google.maps.MarkerOptions(
            position = latLng,
            map = gmap,
            title = event.name
          ))

        val infoWindow = new google.maps.InfoWindow(
          InfoWindowOptions(content = s"""
                 |<div>
                 |  <h2>${event.name}</h2>
                 |  <p>${event.location}</p>
                 |</div>
              """.stripMargin)
        )

        marker -> infoWindow
      }

    val markerBounds = new google.maps.LatLngBounds()
    var activeInfoWindow = new google.maps.InfoWindow

    for {
      (marker, infoWindow) <- markers
    } yield {
      marker.addListener("click", (_: js.Any) => {
        activeInfoWindow.close()
        activeInfoWindow = infoWindow
        infoWindow.open(gmap, marker)
      })
      markerBounds.extend(marker.getPosition())
    }

    gmap.fitBounds(markerBounds)

  }
}
