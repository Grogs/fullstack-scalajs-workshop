package model

import java.time.LocalDate

import play.api.libs.json._

import scala.util.{Failure, Success, Try}

case class Conference(name: String, location: String, startDate: LocalDate, endDate: Option[LocalDate])

object Conference {
  implicit val localDateFormat = new Format[LocalDate] {
    def reads(json: JsValue): JsResult[LocalDate] = Try{
      val raw = json.as[JsString].value
      val year = raw.take(4).toInt
      val month = raw.substring(5, 7).toInt
      val day = raw.substring(8, 10).toInt
      LocalDate.of(year, month, day)
    } match {
      case Success(date) => JsSuccess(date)
      case Failure(ex) => JsError(s"Failed to parse $json as a LocalDate: ${ex.getClass.getSimpleName} ${ex.getMessage}")
    }
    def writes(o: LocalDate): JsValue = JsString(o.toString)
  }
  implicit val formats = Json.format[Conference]
}
