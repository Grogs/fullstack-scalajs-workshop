package services.events

import model.{Conference, Coordinates}
import play.api.libs.json.Json

import scala.io.Source

class GeolocationServiceImpl extends GeolocationService {
  private val allLocations =
    Json
      .parse(Source.fromResource("events/locations.json").mkString)
      .as[Map[String, (Double, Double)]]

  def location(event: Conference): Coordinates = {
    val name = event.name
    val year = event.startDate.getYear
    val (lat, long) = allLocations(s"$year:$name")
    Coordinates(lat, long)
  }
}
