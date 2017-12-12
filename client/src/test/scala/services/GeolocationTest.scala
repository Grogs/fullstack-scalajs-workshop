package services

import model.Coordinates
import org.scalatest.{FunSuite, Matchers}

class GeolocationTest extends FunSuite with Matchers {
  test("distance works") {
    Geolocation.distance(Coordinates(0,0), Coordinates(0,0)) shouldBe 0.0
    Geolocation.distance(Coordinates(10,10), Coordinates(10,10)) shouldBe 0.0

    Geolocation.distance(Coordinates(0,0), Coordinates(10,10)) shouldBe 1568.963711248778
    Geolocation.distance(Coordinates(-9,-9), Coordinates(9,9)) shouldBe 2825.5134978128904
  }
}
