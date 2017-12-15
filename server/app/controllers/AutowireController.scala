package controllers

import javax.inject.{Inject, Singleton}

import autowire.Core.Request
import autowire.{Serializers, Server}
import play.api.libs.json._
import play.api.mvc.InjectedController
import services.events.{ConferenceService, GeolocationService}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class AutowireController @Inject()(eventsService: ConferenceService,
                                   geolocationService: GeolocationService)
    extends InjectedController {

  def api(path: String) = Action.async { implicit req =>

    val jsonBody = Json.parse(req.body.asText.getOrElse(""))

    val parameters = jsonBody.as[JsObject].value.toMap.mapValues(_.toString)

    val request = Request(path.split("/"), parameters)

    val conferenceServiceRouter = ApiServer.route(eventsService)
    val geolocationServiceRouter = ApiServer.route(geolocationService)

    val router = conferenceServiceRouter orElse geolocationServiceRouter

    for {
      resp <- router(request)
    } yield Ok(resp)
  }

  object ApiServer
      extends Server[String, Reads, Writes]
      with Serializers[String, Reads, Writes] {

    def read[Result: Reads](p: String) = Json.fromJson(Json.parse(p)) match {
      case JsSuccess(r, _) => r
      case JsError(errors) => throw new RuntimeException(errors.toString)
    }

    def write[Result: Writes](r: Result) = Json.stringify(Json.toJson(r))
  }
}
