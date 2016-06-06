package filters

import actors.StatsActor
import akka.actor.ActorSystem
import akka.stream.Materializer
import play.api.Logger
import play.api.mvc.{Result, RequestHeader, Filter}

import scala.concurrent.Future

/**
  * Created by tkugimoto on 06/06/16.
  */
class StatsFilter(actorSystem: ActorSystem, implicit val mat: Materializer) extends Filter {
  override def apply(nextFilter: (RequestHeader) => Future[Result])
                    (header: RequestHeader): Future[Result] = {

    actorSystem.actorSelection(StatsActor.path) ! StatsActor.RequestReceived
    Logger.info(s"Serving another request: ${header.path}")
    nextFilter(header)
  }
}