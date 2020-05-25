package views.common

import controllers.routes
import play.api.mvc.Call

sealed trait Tab {
  def name: String
  def href: Call
}

object Tab {

  case object Home extends Tab {
    override val name: String = "Home"
    override val href: Call = routes.HomeController.index()
  }

  case object Spotify extends Tab {
    override def name: String = "Spotify"
    override def href: Call = routes.SpotifyController.hello()
  }

  case object Discogs extends Tab {
    override def name: String = "Discogs"
    override def href: Call = routes.DiscogsController.hello()
  }
}