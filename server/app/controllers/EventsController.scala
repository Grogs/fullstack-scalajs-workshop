package controllers

import javax.inject.{Inject, Singleton}

import model.Query
import play.api.mvc.InjectedController
import services.events.{ConferenceService, ImageService}

@Singleton
class EventsController @Inject()(eventsService: ConferenceService,
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
}
