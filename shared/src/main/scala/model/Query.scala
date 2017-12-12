package model

import enumeratum._
import play.api.libs.json._

sealed trait Query extends EnumEntry

object Query extends Enum[Query] {
  implicit val format = new Format[Query] {
    def writes(o: Query): JsValue = JsString(o.toString)
    def reads(json: JsValue): JsResult[Query] = JsSuccess(Query.withName(json.as[String]))
  }
  val values = findValues
  case object Upcoming extends Query
  case object Past extends Query
  case object All extends Query
}
