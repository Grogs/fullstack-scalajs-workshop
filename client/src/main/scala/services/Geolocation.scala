package services

import model.Coordinates
import org.scalajs.dom.raw.Position
import org.scalajs.dom.window.navigator
import scala.concurrent.{Future, Promise}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

object Geolocation {
  def getCurrentPosition(): Future[Coordinates] = {
    val location = Promise[Position]()
    navigator.geolocation.getCurrentPosition(p => location.success(p), err => location.failure(new Exception(err.message)))
    location.future.map(pos =>
      Coordinates(pos.coords.latitude, pos.coords.longitude)
    )
  }

  def distance(a: Coordinates, b: Coordinates): Double =
    haversine(a.lat, a.long, b.lat, b.long)

  def haversine(lat1:Double, lon1:Double, lat2:Double, lon2:Double)={

    val R = 6372.8

    import math._

    val dLat=(lat2 - lat1).toRadians
    val dLon=(lon2 - lon1).toRadians

    val a = pow(sin(dLat/2),2) + pow(sin(dLon/2),2) * cos(lat1.toRadians) * cos(lat2.toRadians)
    val c = 2 * asin(sqrt(a))
    R * c
  }

}
