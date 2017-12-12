import java.util.concurrent.ConcurrentLinkedQueue

import monix.execution.Scheduler.Implicits.global
import monix.execution.atomic.AtomicInt

import scala.concurrent.{Future, Promise}
import scala.concurrent.duration._

case class RateLimiter(duration: FiniteDuration, maxInvocations: Int) {

  @volatile var permits: Int = maxInvocations
  val queue = new ConcurrentLinkedQueue[() => Any]()

  global.scheduleAtFixedRate(duration, duration) {
    this synchronized {
      permits = maxInvocations

      while (!queue.isEmpty && permits > 0) {
        Option(queue.poll()).foreach { fun =>
          permits -= 1
          fun.apply()
        }
      }
    }
  }

  def apply[T](f: => Future[T]): Future[T] =
    this synchronized {
      if (permits > 0) {
        permits -= 1
        f
      } else {
        val res = Promise[T]()
        queue.add(() => { res.completeWith(f) })
        res.future
      }
    }
}