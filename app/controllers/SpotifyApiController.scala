package controllers

import com.google.inject.Inject
import controllers.wrapper.entities._
import controllers.wrapper.{BaseApi, PlaylistsApi, ProfilesApi, TracksApi}
import logging.AccessLogging
import model.music.Song
import play.api.mvc.{Action, AnyContent, Controller, Result}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  *
  */
class SpotifyApiController @Inject() (api: BaseApi,
                                      playlistsApi: PlaylistsApi,
                                      profilesApi: ProfilesApi,
                                      tracksApi: TracksApi) extends Controller with AccessLogging {

  /**
    * Redirect a user to authenticate with Spotify and grant permissions to the application
    * @return a Redirect Action (play.api.mvc.Action type is a wrapper around the type `Request[A] => Result`,
    */
  def auth = Action { Redirect(api.authoriseURL(state = None, scopes = List(), showDialog = false)) }

  /**
    *
    * @return
    */
  def callback: Action[AnyContent] = Action.async {
    request => request.getQueryString("code") match {
      case Some(code) => try {
        api.callback(code) { _ => debug() }
      } catch { case e: Exception => handleException(e) }
      case None => request.getQueryString("error") match {
        case Some("access_denied") => Future(BadRequest("You need to authorize permissions in order to use the App."))
        case Some(error) => Future(BadRequest(error))
        case _ => Future(BadRequest("Something went wrong."))
      }
    }
  }

  private def hello() = profilesApi.me map {
    me => Ok(views.html.callback(s"Hello, ${me.id}"))
  }

  private def debug() = {

    /* CAN'T PARSE TRACKS? :(
    val p1: Future[(String, List[Track])] = for {
      playlist1 <- playlistsApi.myPlaylists.map(p => p.items.head)
      myTracks <- tracksApi.getTracks(playlist1.tracks.href)
    } yield (playlist1.name, myTracks.items)
    */

    val playlist: Future[SimplePlaylist] = playlistsApi.myPlaylists.map(p => p.items.head)

    playlist.foreach(p => accessLogger.debug(p.name))

    val tracks: Future[Page[PlaylistTrack]] = playlist.flatMap {
      p => tracksApi.getPlaylistTracks(p.tracks.href)
    }

    // Future(Ok(views.html.callback("Ah1")))
    tracks.map { p => Ok(views.html.callback(s"${p.items.map(pl => pl.track.name).toString}")) }
  }


  private def handleException(e: Exception): Future[Result] = {
    accessLogger.debug(e.getMessage)
    Future(BadRequest(s"There was a problem loading this page. Please try again.\n${e.getMessage}"))
  }

  def getSampleTracks(): Action[AnyContent] = { Action.async(
    Future(Ok(views.html.callback("")))
  )}

  def allPlaylistsAction(): Action[AnyContent] = {
    Action.async(Future(Ok(views.html.callback(""))))
  }

}
