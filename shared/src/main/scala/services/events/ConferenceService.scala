package services.events

import model.{Conference, Query}

trait ConferenceService {
  def search(query: Query): Seq[Conference]
}
