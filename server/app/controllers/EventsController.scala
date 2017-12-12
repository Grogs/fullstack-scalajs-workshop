package controllers

import javax.inject.{Inject, Singleton}

import autowire.Core.Request
import model.Query
import play.api.libs.json._
import play.api.mvc.InjectedController
import services.events.{ConferenceService, GeolocationService, ImageService}

import scala.concurrent.ExecutionContext.Implicits.global
@Singleton
class EventsController @Inject()(eventsService: ConferenceService,
                                 geolocationService: GeolocationService,
                                 webJarAssets: WebJarAssets)
    extends InjectedController {

  def search(rawQuery: String) = Action {

    val query = Query.withNameInsensitive(rawQuery)

    val conferences = eventsService.search(query)

    Ok(
      views.html.eventListings(
        "London",
        conferences
      )(webJarAssets)
    )
  }

  def image(conferenceName: String) = Action {
    val imageName = ImageService.imageName(conferenceName)
    Ok.sendResource(s"images/$imageName")
  }

  object ApiServer
      extends autowire.Server[String, Reads, Writes]
      with autowire.Serializers[String, Reads, Writes] {
    def read[Result: Reads](p: String) = Json.fromJson(Json.parse(p)) match {
      case JsSuccess(r, _) => r
      case JsError(errors) => throw new RuntimeException(errors.toString)
    }

    def write[Result: Writes](r: Result) = Json.stringify(Json.toJson(r))
  }

  def api(path: String) = Action.async { implicit req =>
    val body = req.body.asText.getOrElse("")

    val parameters =
      Json.parse(body).as[JsObject].value.toMap.mapValues(_.toString)

    val request = Request(path.split("/"), parameters)

    val conferenceServiceRouter = ApiServer.route[ConferenceService](eventsService)
    val geolocationServiceRouter = ApiServer.route[GeolocationService](geolocationService)

    val router = conferenceServiceRouter orElse geolocationServiceRouter

    for {
      resp <- router(request)
    } yield Ok(resp)
  }

}
