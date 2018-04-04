import java.time.{LocalDate, Month}

import com.typesafe.sbt.packager.docker.DockerPlugin
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport._
import org.jsoup.Jsoup
import sbt.Keys.{resourceDirectory, streams, version}
import sbt._

import scala.io.Source
import scala.util.Try
import scala.collection.JavaConverters._
import play.api.libs.json._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object DataImportPlugin extends AutoPlugin {


  override def trigger = allRequirements

  object autoplugin {
    val importData = taskKey[Unit]("Import conference data")
  }

  import autoplugin._

  override def buildSettings = Seq(
    importData := {
      val baseDir = (resourceDirectory in(LocalProject("server"), Compile)).value / "events"
      def toJson[T: Writes](obj: T) = Json.prettyPrint(Json.toJson(obj))

      val events = EventScraper.allScalaEvents()
      println(s"Writing allScalaEvents.json")
      IO.write(baseDir / "allScalaEvents.json", toJson(events))

      val locations = Await.result(GoogleMaps.geocode(events), Duration.Inf)
      println("Writing locations.json")
      IO.write(baseDir / "locations.json", toJson(locations))
    }
  )

  case class Event(name: String, location: String, startDate: LocalDate, endDate: Option[LocalDate])
  object Event {
    implicit val formats = Json.format[Event]
  }

  private object EventScraper {


    val SingleDay = """(\d+) ([a-zA-Z]+) (20\d{2})(?: - ?)?""".r
    val MultiDay = """(\d+) ([a-zA-Z]+) (20\d{2}) - (\d+) ([a-zA-Z]+) (20\d{2})""".r

    def extractDates(s: String): (LocalDate, Option[LocalDate]) = s.trim match {
      case SingleDay(day, month, year) =>
        toDate(day, month, year) -> None
      case MultiDay(day1, month1, year1, day2, month2, year2) =>
        toDate(day1, month1, year1) -> Option(toDate(day2, month2, year2))
    }

    private def toDate(day: String, month: String, year: String) = {
      val monthString = month.replaceAll("Dec$", "December").toUpperCase
      LocalDate.of(year.toInt, Month.valueOf(monthString), day.toInt)
    }

    val upcomingScalaEventsUrl = "https://www.scala-lang.org/events/"
    val pastScalaEventsUrl = "https://www.scala-lang.org/pastevents/"

    private def trainingItems(url: String) = Jsoup.connect(url).get.select(".training-item").asScala.toList

    def allScalaEvents() = for {
      event <- trainingItems(upcomingScalaEventsUrl) ++ trainingItems(pastScalaEventsUrl)
      name = event.select("h4").first().text()
      location :: dates :: Nil = event.select("p").asScala.toList.map(_.text())
      (start, end) = extractDates(dates)
    } yield Event(name, location, start, end)
  }

  private object GoogleMaps {
    import com.koddi.geocoder.Geocoder, scala.concurrent.Future, scala.concurrent.duration._, scala.concurrent.ExecutionContext.Implicits.global
    val limiter = RateLimiter(1.second, 50)
    val geo = Geocoder.createAsync("AIzaSyD_Z5sjcBpYzO7dDVkUoCkMSyTbgSXaWaw")
    def geocode(events: Seq[Event]): Future[Map[String, (Double, Double)]] = {
      println(s"About to geocode ${events.length}, should take ~${events.length / 50} seconds")
      Future.traverse(events)(e =>
        limiter{
          geo.lookup(e.location).map { results =>
            println(s"Processing ${e.name} with ${results.headOption}")
            val coords = results.head.geometry.location
            (e.startDate.getYear+":"+e.name) -> (coords.latitude, coords.longitude)
          }
        }
      ).map(_.toMap)
    }
  }
}
