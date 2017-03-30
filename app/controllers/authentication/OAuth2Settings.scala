package controllers.authentication

/**
  * Created by marhunter on 3/30/17.
  */
case class OAuth2Settings(
                           clientId: String,
                           clientSecret: String,
                           signInUrl: String,
                           accessTokenUrl: String,
                           userInfoUrl: String
                         )
