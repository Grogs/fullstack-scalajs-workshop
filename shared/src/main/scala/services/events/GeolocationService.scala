package services.events
import model.{Conference, Coordinates}

trait GeolocationService {
  def location(event: Conference): Coordinates
}
