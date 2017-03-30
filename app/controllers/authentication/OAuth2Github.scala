package controllers.authentication

import javax.inject.Inject

import play.api.Application
import play.api.ApplicationLoader.Context
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global

class OAuth2Github @Inject() (implicit wsClient: WSClient, configuration: play.api.Configuration) extends Controller {

  val GITHUB = new OAuth2[GithubUser](OAuth2Settings(
    "your_client_id",
    "your_client_secret",
    "https://github.com/login/oauth/authorize",
    "https://github.com/login/oauth/access_token",
    "https://api.github.com/user"
  )){
    def user(body: String): GithubUser = Json.parse(body).validate[GithubUser](githubUserReads).get
  }

  case class GithubUser(
                         login: String,
                         email: String,
                         avatar_url: String,
                         name: String
                       )

  implicit val githubUserReads: Reads[GithubUser] = (
    (__ \ "login").read[String] and
      (__ \ "email").read[String] and
      (__ \ "avatar_url").read[String] and
      (__ \ "name").read[String]
    )(GithubUser.apply _)

  def signin() = Action { Redirect(GITHUB.signIn) }

  def signout() = Action { Redirect(controllers.routes.HomeController.index()).withSession() }

  def callback(code: String): Action[AnyContent] = Action.async { implicit request =>
    GITHUB.authenticate(code).map { user =>
      Redirect(controllers.routes.HomeController.index()).withSession("login" -> user.login)
    }
  }
}