package controllers

import javax.inject.{Inject, Singleton}

import model.Query
import play.api.mvc.InjectedController
import services.events.{ConferenceService, ImageService}

@Singleton
class EventsController @Inject()(conferenceService: ConferenceService,
                                 webJarAssets: WebJarAssets)
    extends InjectedController {

  def search(rawQuery: String) = Action {

    val query = Query.withNameInsensitive(rawQuery)

    val conferences = conferenceService.search(query)

    Ok(
      views.html.eventListings(
        query.entryName,
        conferences
      )(webJarAssets)
    )
  }

  def image(conferenceName: String) = Action {
    val imageName = ImageService.imageName(conferenceName)
    Ok.sendResource(s"images/$imageName")
  }
}
