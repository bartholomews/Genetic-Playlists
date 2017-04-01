package controllers.wrapper.entities

sealed trait AlbumType extends Enumeration {

  case object Album extends AlbumType

  case object Single extends AlbumType

  case object Compilation extends AlbumType

}
