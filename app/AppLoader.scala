/**
  * Created by tkugimoto on 06/06/16.
  */

import actors.StatsActor
import actors.StatsActor.Ping
import akka.actor.Props
import controllers.{Application, Assets}
import filters.StatsFilter
import play.api.ApplicationLoader.Context
import play.api._
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.routing.Router
import router.Routes
import com.softwaremill.macwire._
import services.{SunService, WeatherService}
import scala.concurrent.Future
import play.api.mvc.{Result, RequestHeader, Filter}

class AppApplicationLoader extends ApplicationLoader {
  def load(context: Context) = {
    LoggerConfigurator(context.environment.classLoader).foreach { configurator =>
      configurator.configure(context.environment)
    }
    (new BuiltInComponentsFromContext(context) with AppComponents).application
  }
}

trait AppComponents extends BuiltInComponents with AhcWSComponents {
  lazy val assets: Assets = wire[Assets]
  lazy val prefix: String = "/"
  lazy val router: Router = wire[Routes]
  lazy val applicationController = wire[Application]

  lazy val sunService = wire[SunService]
  lazy val weatherService = wire[WeatherService]

  lazy val statsFilter: Filter = wire[StatsFilter]
  override lazy val httpFilters = Seq(statsFilter)

  lazy val statsActor = actorSystem.actorOf(
    Props(wire[StatsActor]), StatsActor.name
  )

  applicationLifecycle.addStopHook{ () =>
    Logger.info("The app is about to stop")
    Future.successful(Unit)
  }

  val onStart = {
    Logger.info("The app is about to start")
    statsActor ! Ping
  }
}