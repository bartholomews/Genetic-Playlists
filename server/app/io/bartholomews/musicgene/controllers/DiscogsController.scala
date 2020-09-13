package io.bartholomews.musicgene.controllers

import cats.data.EitherT
import cats.effect.{ContextShift, IO}
import com.google.inject.Inject
<<<<<<< HEAD
=======
<<<<<<< HEAD:server/app/io.bartholomews.musicgene/controllers/DiscogsController.scala
import io.bartholomews.musicgene.controllers.http.DiscogsCookies
import io.bartholomews.musicgene.controllers.http.codecs.FsClientCodecs._
=======
>>>>>>> 45d698521366e6ca02d3dfa73e89182be6a6da13:server/app/io/bartholomews/musicgene/controllers/DiscogsController.scala
>>>>>>> 45d698521366e6ca02d3dfa73e89182be6a6da13
import io.bartholomews.discogs4s.DiscogsClient
import io.bartholomews.discogs4s.entities.RequestToken
import io.bartholomews.fsclient.entities.oauth.{AccessTokenCredentials, SignerV1, TokenCredentials}
import io.bartholomews.fsclient.entities.oauth.v1.OAuthV1AuthorizationFramework.SignerType
import io.bartholomews.musicgene.controllers.http.DiscogsCookies
import javax.inject._
import org.http4s.Uri
import play.api.mvc._
import cats.implicits._
import io.bartholomews.discogs4s.endpoints.DiscogsAuthEndpoint

import scala.concurrent.ExecutionContext

/**
 *
 */
@Singleton
class DiscogsController @Inject() (cc: ControllerComponents)(implicit ec: ExecutionContext)
    extends AbstractControllerIO(cc) {

<<<<<<< HEAD
  import io.bartholomews.musicgene.controllers.http.codecs.FsClientCodecs._
=======
<<<<<<< HEAD:server/app/io.bartholomews.musicgene/controllers/DiscogsController.scala
=======
  import io.bartholomews.musicgene.controllers.http.codecs.FsClientCodecs._
>>>>>>> 45d698521366e6ca02d3dfa73e89182be6a6da13:server/app/io/bartholomews/musicgene/controllers/DiscogsController.scala
>>>>>>> 45d698521366e6ca02d3dfa73e89182be6a6da13
  import io.bartholomews.musicgene.controllers.http.DiscogsHttpResults._
  implicit val cs: ContextShift[IO] = IO.contextShift(ec)

  // TODO: load from config
  private val discogsCallback = Uri.unsafeFromString("http://localhost:9000/discogs/callback")

  val discogsClient: DiscogsClient[IO] =
    DiscogsClient.unsafeFromConfig(SignerType.BasicSignature)

  private def hello(signer: SignerV1)(implicit request: Request[AnyContent]): IO[Result] =
    discogsClient.auth.me(signer).map(_.toResult(me => Ok(views.html.discogs.hello(me))))

  def hello(): Action[AnyContent] = ActionIO.async { implicit request =>
    withToken(hello)
  }

  def callback: Action[AnyContent] = ActionIO.async { implicit request =>
    DiscogsCookies
      .extract[AccessTokenCredentials](request)
      .map(hello)
      .getOrElse {

        val maybeUri = Uri.fromString(s"${requestHost(request)}/${request.uri.stripPrefix("/")}")

        def extractSessionRequestToken: Either[Result, RequestToken] =
          DiscogsCookies
            .extract[RequestToken](request)
            .toRight(
              badRequest("There was a problem retrieving the request token")
            )

        (for {
          requestToken <- EitherT.fromEither[IO](extractSessionRequestToken)
          callbackUri <- EitherT.fromEither[IO](
            maybeUri.leftMap(parseFailure => badRequest(parseFailure.details))
          )
          maybeAccessToken <- EitherT.liftF(discogsClient.auth.fromUri(requestToken, callbackUri))
          accessTokenCredentials <- EitherT.fromEither[IO](maybeAccessToken.entity.leftMap(errorToResult))
        } yield accessTokenCredentials).value
          .flatMap(
            _.fold(
              errorResult => IO.pure(errorResult),
              signer => hello(signer).map(_.withCookies(DiscogsCookies.accessCookie(signer)))
            )
          )
      }
  }

  def logout(): Action[AnyContent] = ActionIO.async { implicit request =>
    IO.pure {
      DiscogsCookies.extract[AccessTokenCredentials](request) match {
        case None => BadRequest("Need to be token-authenticated to logout!")
        case Some(accessToken: TokenCredentials) =>
          redirect(DiscogsAuthEndpoint.revokeUri(accessToken))
            .discardingCookies(DiscogsCookies.discardCookies)
      }
    }
  }

  /**
   * Redirect a user to authenticate with Discogs and grant permissions to the application
   *
   * @return a Redirect Action (play.api.mvc.Action type is a wrapper around the type `Request[A] => Result`,
   */
  private def authenticate(implicit request: Request[AnyContent]): IO[Result] =
    discogsClient.auth
      .getRequestToken(discogsClient.temporaryCredentialsRequest(discogsCallback))
      .map(
        _.entity
          .fold(
            errorToResult,
            (requestToken: RequestToken) =>
              redirect(requestToken.callback)
                .withCookies(DiscogsCookies.accessCookie(requestToken))
          )
      )

  // http://pauldijou.fr/jwt-scala/samples/jwt-play/
  def withToken[A](f: SignerV1 => IO[Result])(implicit request: Request[AnyContent]): IO[Result] =
    DiscogsCookies.extract[AccessTokenCredentials](request) match {
      case None              => authenticate(request)
      case Some(accessToken) => f(accessToken)
    }
}