package model

import play.api.libs.json.Json

/**
  * Created by tkugimoto on 06/06/16.
  */
case class SunInfo (
                   sunrise: String,
                   sunset: String
                   )

object SunInfo {
  implicit val writes = Json.writes[SunInfo]
}