package controllers

import org.jsoup.Jsoup
import org.scalatest.Assertion
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.collection.JavaConverters._

class EventsControllerTest extends PlaySpec with GuiceOneAppPerTest {

  def page = Jsoup.parse(route(app, FakeRequest(GET, "/events")).map(contentAsString).get)

  "Exercise 3 - Google Map of conferences" should {
    "add another button for showing the map" in {

      val buttons = page.select("button").asScala

      (buttons.map(_.id) must contain ("show-map")) orElse "We want a new button with the ID `show-map`."

      val mapButtonClasses = page.getElementById("show-map").classNames.asScala

      (mapButtonClasses must contain ("button")) orElse "We need to add the `button` class so Bulma styles it nicely."
      (mapButtonClasses must contain ("toggle-map-modal")) orElse "We need to add the `toggle-map-modal` so we know this button should open the modal"
    }

    "bulma card modal for the map" in {

      val maybeModal = page.select("div.modal").asScala.headOption

      (maybeModal mustBe 'defined) orElse "Now add a Bulma 'Modal card' as detailed at: https://bulma.io/documentation/components/modal/"

      val modal = maybeModal.get

      (modal.id mustBe "mapModal") orElse "Give the modal the ID 'mapModal' so we can reference it on the frontend"


      val modalBackground = modal.select(".modal-background").asScala.headOption
      (modalBackground mustBe 'defined) orElse "You need to add a div with the class 'modal-background' to the modal."

      val modalCard = modal.select(".modal-card").asScala.headOption
      (modalBackground mustBe 'defined) orElse "You also need to add a div with the class 'modal-card' to hold the map. Refer back to https://bulma.io/documentation/components/modal/"
    }

    "modal must include a container for the google map" in {

      val modal = page.select("div.modal > .modal-card").first

      val mapContainer = modal.getElementById("map")

      (mapContainer must not be null) orElse "You need to create a div within the modal card to put the map into"

      (mapContainer.attr("style") mustBe "height: 500px") orElse "You need to specify the height otherwise it will default to 0px high."
    }
  }

  "Exercise 2 - eventListings conference selection" should {
    "have a select inside a field inside a control" in {
      (page.select("div.field").size mustBe 1) orElse "We need 3 wrappers around the select. A div with the class `control`, with a div with the `field` class inside, and then inside the field a div with the `select` class. See https://bulma.io/documentation/form/general/"
      (page.select("div.field > div.control > div.select").asScala must not be empty) orElse "Still two more wrappers to go 😣. See https://bulma.io/documentation/form/general/"
      (page.select("div.field > div.control > div.select > select[id=query]").size mustBe 1) orElse "Now add a select element with an ID of `query` so we can prefer to it later from ScalaJS"

      val select = page.select("div.field > div.control > div.select > select#query").first
      val selectOptions = select.select("option").asScala.map(_.`val`).toList
      (selectOptions mustEqual List("upcoming", "past", "all")) orElse "Now add 3 options to the select. They should have the values `upcoming`, `past` and `all` corresponding to the possible values of `model.Query` that are used to call the ConferenceService."
    }
  }

  "eventListings template" should {

    "have a container div" in {

      val bodyDivs = page.select("body > div").asScala
      (bodyDivs.size mustBe 1) orElse "You need to add a div to the body of eventListings"

      val container = bodyDivs.head
      (container.className mustBe "container") orElse "You need to add the `container` class for Bulma to style it"
      (container.id mustBe "container") orElse "You need to give it the ID `container` too, so we can reference it later from the client"
    }

    "the container should have a div for the event listings" in {
      val eventListingContainerLookup = page.select("body > div.container > div#event-listings").asScala
      (eventListingContainerLookup must not be empty) orElse "You need to add a div with id `event-listings` inside your container"
    }

  }

  "eventsTable template" should {

    def eventsTable = page.select("div#event-listings > div").asScala.head
    def conferenceDivs = eventsTable.children.asScala

    "be included in the shared eventListings template" in {
      val table = page.select("div#event-listings > div").asScala
      (table must not be empty) orElse "You now need to include the eventsTable. See https://www.playframework.com/documentation/2.6.x/ScalaTemplateUseCases#layout"
      (table.size mustBe 1) orElse "There should only be one container div for the conferences"
    }

    "container an entry for every upcoming conference" in {
      (conferenceDivs.size mustEqual 13) orElse "Now add a div for each event. See https://www.playframework.com/documentation/2.6.x/ScalaTemplates#iterating"
    }

    "use Bulma's column layout" in {
      (eventsTable.classNames.asScala must contain ("columns")) orElse "We're going to use Bulma's column layout for the conference cards. See https://bulma.io/documentation/columns/basics/"

      for (confDiv <- conferenceDivs) {
        (confDiv.classNames.asScala must contain ("column")) orElse "See https://bulma.io/documentation/columns/basics/"
      }
    }

    "the conferences should span multiple lines" in {
      (eventsTable.classNames.asScala must contain ("is-multiline")) orElse "See https://bulma.io/documentation/columns/options/#multiline"
    }


    "there should be 4 conferences per row" in {
      for (confDiv <- conferenceDivs) {
        (confDiv.classNames.asScala must contain ("is-one-quarter")) orElse "See https://bulma.io/documentation/columns/sizes/"
      }
    }

    "each conference should be represented as a Bulma card" in {
      for (confDiv <- conferenceDivs) {
        val card = confDiv.select("div.column > div").first
        val cardDocs = "https://bulma.io/documentation/components/card/"
        (card.classNames.asScala must contain ("card")) orElse s"See $cardDocs"
        (card.select("div.card-image").asScala must not be empty) orElse "We want to include an image of each conference"
        (card.select("div.card-content").asScala must not be empty) orElse s"And bulma makes us wrap the details in 'card-content'. See $cardDocs"
        (card.select("div.card-content > div.content").asScala must not be empty) orElse s"And within card-content we need yet another content wrapper. See $cardDocs"
      }

      val scalaMatsuriCard = conferenceDivs(3).select("div.column > div").first.toString
      (scalaMatsuriCard must include ("SCALA MATSURI")) orElse "now include the name of each conference"
      (scalaMatsuriCard must include ("Tokyo, Japan")) orElse "now include the location of each conference"
      (scalaMatsuriCard must include ("2018-03-16 - 2018-03-18")) orElse "now include the dates of each conference using the provided `date` function"
    }
  }


  implicit class WithClueSyntax(a: => Assertion) {
    def orElse(clue:String) = withClue(clue)(a)
  }
}
