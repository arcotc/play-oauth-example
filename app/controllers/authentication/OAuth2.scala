package controllers.authentication

import javax.inject.Inject

import play.api.libs.ws.WSClient
import play.core.parsers.FormUrlEncodedParser
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

abstract class OAuth2[T] @Inject()(settings: OAuth2Settings)(implicit wsClient: WSClient, configuration: play.api.Configuration) {
  def user(body: String): T

  import settings._

  lazy val signIn: String = signInUrl + "?client_id=" + clientId

  def authenticate(code: String): Future[T] = {
    val url = accessTokenUrl + "?client_id=" + clientId + "&client_secret=" + clientSecret + "&code=" + code

    for {
      accessToken <- wsClient.url(url).get()
      userInfo <- wsClient.url(userInfoUrl + "?access_token=" + FormUrlEncodedParser.parse(accessToken.body).get("access_token").flatMap(_.headOption).get).get()
    } yield {
      user(userInfo.body)
    }
  }
}
