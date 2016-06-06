package services

import org.joda.time.{DateTime, DateTimeZone}
import org.joda.time.format.DateTimeFormat
import play.api._
import play.api.libs.ws.{WSClient, WS}
import play.api.mvc._
import model.SunInfo
import java.util.Date
import java.text.SimpleDateFormat
import scala.concurrent.Future
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by tkugimoto on 06/06/16.
  */
class WeatherService(wsClient: WSClient) {
  def getTemperature(lat: Double, lon: Double): Future[Double] = {
    val weatherResponseF = wsClient.url("http://api.openweathermap.org/data/2.5/weather?" + s"lat=$lat&lon=$lon&units=metric&APPID=6fd925cb9e599b75335b7357d00b1a8c").get()
    weatherResponseF.map { weatherResponse =>
      val weatherJson = weatherResponse.json
      val temperature = (weatherJson \ "main" \ "temp").as[Double]
      temperature
    }
  }
}

class SunService(wsClient: WSClient) {
  def getSunInfo(lat: Double, lon: Double): Future[SunInfo] = {
    val responseF = wsClient.url("http://api.sunrise-sunset.org/json?" + s"lat=$lat&lng=$lon&formatted=0").get()
    responseF.map { response =>
      val json = response.json
      val sunriseTimeStr = (json \ "results" \ "sunrise").as[String]
      val sunsetTimeStr = (json \ "results" \ "sunset").as[String]
      val sunriseTime = DateTime.parse(sunriseTimeStr)
      val sunsetTime = DateTime.parse(sunsetTimeStr)
      val formatter = DateTimeFormat.forPattern("HH:mm:ss").
        withZone(DateTimeZone.forID("Australia/Sydney"))
      val sunInfo = SunInfo(formatter.print(sunriseTime), formatter.print(sunsetTime))
      sunInfo
    }
  }
}