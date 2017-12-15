package services.events

import java.time.LocalDate
import javax.inject.Inject

import model.Query.{All, Past, Upcoming}
import model._
import play.api.libs.json.Json

import scala.io.Source

class ConferenceServiceImpl extends ConferenceService {

  private val allEvents =
    Json
      .parse(Source.fromResource("events/allScalaEvents.json")("UTF-8").mkString)
      .as[Seq[Conference]]
      .sortBy(_.startDate.toEpochDay)

  def search(query: Query): Seq[Conference] = query match {
    case Upcoming => allEvents.filter(_.startDate isAfter LocalDate.now())
    case Past     => allEvents.filter(_.startDate isBefore LocalDate.now())
    case All      => allEvents
  }

}
