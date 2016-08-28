package controllers

import javax.inject.{Inject, Singleton}

import com.wrapper.spotify.exceptions.BadRequestException
import model.music.{Cache, Song}
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.{MongoConnection, MongoDriver}
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json.collection.JSONCollection

import collection.JavaConverters._
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/**
  *
  */
/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  *
  * TODO should retrieve all IDs single call,
  * if not in the cache then retrieve the audioAnalysis
  */
@Singleton
class HomeController @Inject()(implicit ec:ExecutionContext, val reactiveMongoApi: ReactiveMongoApi)
  extends Controller with MongoController with ReactiveMongoComponents {

  val mongoUri = "mongodb://heroku_pzmhfhvt:a6b5qlv64dmc19pghfuipejdcf@ds017896.mlab.com:17896/heroku_pzmhfhvt"

  /**
    * TODO THE WHOLE PROCESS OF RETRIEVING DATA SHOULD GO TO ITN OWN CLASS
    * I NEED MUSIC_UTIL.GetSONG(id) as well (now in Cache)
    *
    * Create an Action to render an HTML page with a welcome message.
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index = Action {
    Ok(views.html.index("GEN"))
  }

  def getSampleTracks = Action {
    val songs = Cache.extractSongs
    // TODO inject
    Ok(views.html.tracks("SAMPLE SONGS", Vector(("A list of unsorted tracks with different characteristics",
      songs))))
  }

  def getUserPlaylists = Action {
    try {
      val spotify = new SpotifyController
      val userName = spotify.getSpotifyName
      val playlists = spotify.getPlaylists

      //playlists.foreach(v => writeSongsToJSON(v._2))
      writeSongsToJSON(Vector())

      Ok(views.html.tracks(userName, playlists))
    } catch {
      // @see https://developer.spotify.com/web-api/user-guide/
      case _: BadRequestException => {
        BadRequest("That was a bad request.")
      } // TODO implement Button BACK to index
      case _: NullPointerException => BadRequest("Something went wrong.") // should return something else not badreq>
      // case _  => // some other exception handling
      // case 429 (too many requests) : maybe should be catched in SpotifyController
    }
  }

  /*
//TODO
def writePlaylistsToJSON(db: List[(SimplePlaylist, List[Song])]) = {
  db.foreach(p => {
    p._2.foreach(s => JsonController.writeJSON(s.id, s.preview_url, s.attributes))
  })
}
*/

  def writeSongsToJSON(songs: Vector[Song]) = {
    val document1 = BSONDocument(
      "firstName" -> "Stephane",
      "lastName" -> "Godbillon",
      "age" -> 29)

    val futureCollection: Future[BSONCollection] = {

      val drive = new MongoDriver
      reactiveMongoApi.database.map(_.collection[BSONCollection]("Tracks"))
    futureCollection.onComplete {
      case Failure(e) => {
        println("============== FUTURECOLLECTION FAILED! ================")
        e.printStackTrace()
      }
      case Success(coll) =>
        println(s"successfully got collection: $coll")
        insertDoc1(coll, document1)
    }
  }

    /*
    resolve()
    println("DB_RESOLVED")
    val database = connection("heroku_pzmhfhvt")
 //   val collection = database.collection[BSONCollection]("Tracks")
    val collection = database[BSONCollection]("Tracks")
    println("I GOT TRACK COLLECTION")
  */

  def insertDoc1(coll: BSONCollection, doc: BSONDocument): Future[Unit] = {
    val writeRes: Future[WriteResult] = coll.insert(doc)

    writeRes.onComplete { // Dummy callbacks
      case Failure(e) => {
        println("============== INSERTION FAILED! ================")
        e.printStackTrace()
      }
      case Success(writeResult) =>
        println(s"successfully inserted document with result: $writeResult")
    }

    writeRes.map(_ => {}) // in this example, do nothing with the success
  }

  /*
    /**
      * http://reactivemongo.org/releases/0.11/documentation/tutorial/connect-database.html
      */
    def resolve() = {
      val driver = new MongoDriver
      println("RESOLVING_DB...")
      val database = for {
        uri <- Future.fromTry(MongoConnection.parseURI(mongoUri))
        con = driver.connection(uri)
        dn <- Future(uri.db.get)
      } yield database

      database.onComplete {
        case resolution =>
          println(s"DB resolution: $resolution")
          driver.close()
      }
    }
    */

 //   songs.foreach(s => JsonController.writeJSON(s.id, s.attributes.asJava))
    val test = BSONDocument(
     "id" -> "ASDA"
    )

}
