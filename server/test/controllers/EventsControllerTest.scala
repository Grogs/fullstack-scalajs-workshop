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
    "add a button to the form to open the map" in {

      val buttons = page.select("button").asScala

      (buttons.map(_.id) must contain ("show-map")) orElse "You need to an a 'button' element to the form, and give it the ID `show-map`."

      val mapButtonClasses = page.getElementById("show-map").classNames.asScala

      (mapButtonClasses must contain ("button")) orElse "You need to add the `button` class to the 'button' element. This feels redundant, but is need for Bulma to make it look nice."
      (mapButtonClasses must contain ("toggle-map-modal")) orElse "You also need to add the `toggle-map-modal` which we will use from the frontend ScalaJS code."
    }

    "bulma card modal for the map" in {

      val maybeModal = page.select("div.modal").asScala.headOption

      (maybeModal mustBe 'defined) orElse "Now add a Bulma 'Modal card' which will contain the map. You can copy the markup from an example at: https://bulma.io/documentation/components/modal/"

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

      (mapContainer.attr("style") mustBe "height: 500px") orElse "You need to specify the height otherwise it will default to 0px high. You should do this will an inline style declaration by adding a 'style' attribute to the modal."
    }
  }

  "Exercise 2 - add a dropdown to allow the user to change between past/upcoming/all conferences" should {
    "have a select element inside a field inside a control. (a 'select' is how we represent a dropdown in HTML)" in {
      (page.select("div.field").size mustBe 1) orElse "To make it look pretty the select will be in a BUlma form which requires 3 wrappers. A div with the class `control`, with a div with the `field` class inside, and then inside the field a div with the `select` class. See https://bulma.io/documentation/form/general/"
      (page.select("div.field > div.control > div.select").asScala must not be empty) orElse "Still two more wrappers to go 😣. See https://bulma.io/documentation/form/general/"
      (page.select("div.field > div.control > div.select > select[id=query]").size mustBe 1) orElse "Now add a select element with an ID of `query` so we can interact with it later from the ScalaJS frontend"

      val select = page.select("div.field > div.control > div.select > select#query").first
      val selectOptions = select.select("option").asScala.map(_.`val`).toList
      (selectOptions mustEqual List("upcoming", "past", "all")) orElse "Now add 3 options to the select. They should have the values `upcoming`, `past` and `all` corresponding to the possible values of shareed `model.Query` enum that are used to call the ConferenceService."
    }
  }

  "server/views/eventListings.scala.html template" should {

    "have a container div" in {

      val bodyDivs = page.select("body > div").asScala
      (bodyDivs.size mustBe 1) orElse "You need to add a div to the body of server/views/eventListings.scala.html"

      val container = bodyDivs.head
      (container.className mustBe "container") orElse "You need to add the `container` class for Bulma to style it"
      (container.id mustBe "container") orElse "You need to give it the ID `container` too, so we can reference it later from the client"
    }

    "the container should have a div for the event listings" in {
      val eventListingContainerLookup = page.select("body > div.container > div#event-listings").asScala
      (eventListingContainerLookup must not be empty) orElse "You need to add a div with id `event-listings` inside your container"
    }

  }

  "shared/views/eventsTable.scala.html template" should {

    def eventsTable = page.select("div#event-listings > div").asScala.head
    def conferenceDivs = eventsTable.children.asScala

    "THe eventListings template should call the shared eventsTable template, where will put the markup for the conference cards" in {
      val table = page.select("div#event-listings > div").asScala
      (table must not be empty) orElse "You now need to call the shared eventsTable. It is shared so we can re-use it on the frontend in later exercises. For an example of how to call one template from another, see https://www.playframework.com/documentation/2.6.x/ScalaTemplateUseCases#layout"
      (table.size mustBe 1) orElse "There should only be one container div for the conferences"
    }

    "container an entry for every upcoming conference" in {
      (conferenceDivs.size mustEqual 18) orElse "Now add a div for each event. You'll need to iterate over each event and return a div, see https://www.playframework.com/documentation/2.6.x/ScalaTemplates#iterating"
    }

    "use Bulma's column layout" in {
      (eventsTable.classNames.asScala must contain ("columns")) orElse "We're going to use a popular CSS framework called Bulma. We will use a column layout for the conference cards. For this, Bulma requires the markup to be a certain way; you will need to add the 'columns' and 'column' class to your divs elements. See https://bulma.io/documentation/columns/basics/"

      for (confDiv <- conferenceDivs) {
        (confDiv.classNames.asScala must contain ("column")) orElse "You've added the outer 'columns' class, now add the inner 'column' class. See https://bulma.io/documentation/columns/basics/"
      }
    }

    "the conferences should span multiple lines" in {
      (eventsTable.classNames.asScala must contain ("is-multiline")) orElse "You need to add the 'is-multiline' class so the cards can span multiple lines. See https://bulma.io/documentation/columns/options/#multiline"
    }


    "there should be 4 conferences per row" in {
      for (confDiv <- conferenceDivs) {
        (confDiv.classNames.asScala must contain ("is-one-quarter")) orElse "We're going to display 4 conferences per row, so you need to add the 'is-one-quarter' class. See https://bulma.io/documentation/columns/sizes/"
      }
    }

    "each conference should be represented as a Bulma card" in {
      for (confDiv <- conferenceDivs) {
        val card = confDiv.select("div.column > div").first
        val cardDocs = "https://bulma.io/documentation/components/card/"
        (card.classNames.asScala must contain ("card")) orElse s"We're going to display each conference as a card. Inside the div for each conference, you need to add a card. You can copy a example Bulma card from $cardDocs"
        (card.select("div.card-image").asScala must not be empty) orElse "We each card to have an image."
        (card.select("div.card-content").asScala must not be empty) orElse s"And bulma makes us wrap the text in 'card-content'. See $cardDocs"
        (card.select("div.card-content > div.content").asScala must not be empty) orElse s"And within card-content we need yet another content wrapper. See $cardDocs"
      }

      val scalaMatsuriCard = conferenceDivs(3).select("div.column > div").first.toString
      (scalaMatsuriCard must include ("FLATMAP(OSLO)")) orElse "now include the name of each conference inside the card"
      (scalaMatsuriCard must include ("Oslo, Norway")) orElse "now include the location of each conference inside the card"
      (scalaMatsuriCard must include ("2018-05-03 - 2018-05-04")) orElse "now include the dates of each conference inside the card. You can use the provided `date` function inside 'eventsTable'."
    }
  }


  implicit class WithClueSyntax(a: => Assertion) {
    def orElse(clue:String) = withClue(clue)(a)
  }
}
