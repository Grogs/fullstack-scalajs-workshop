package model

import play.api.libs.json.Json

case class Coordinates(lat: Double, long: Double)

object Coordinates {
  implicit val coordinates = Json.format[Coordinates]
}