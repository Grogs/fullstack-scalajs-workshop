package model

import play.api.libs.json.Json

case class Hotel(id: Long, name: String, coordinates: Coordinates, images: Seq[String], descriptionHtml: String)

object Hotel {
  implicit val writes = Json.format[Hotel]
}

