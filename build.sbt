name := """musicgene"""
organization := "io.bartholomews"
homepage := Some(url("https://github.com/bartholomews/musicgene"))

version := "0.0.1-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, homepage, scalaVersion, sbtVersion),
    buildInfoPackage := "musicgene"
  )

scalaVersion := "2.13.2"

routesImport ++= Seq(
  "views.spotify.responses._",
  "io.bartholomews.spotify4s.entities._",
  "views.spotify.responses.GeneratedPlaylistResultId",
  "io.bartholomews.musicgene.controllers.http.session.SpotifySessionUser"
)

resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies += guice
libraryDependencies ++=  Seq(
  "io.bartholomews" %% "spotify4s" % "0.0.0+122-87a13c22+20201228-1221-SNAPSHOT",
  "io.bartholomews" %% "discogs4s" % "0.0.1+23-46464d2f+20201228-1223-SNAPSHOT",
  "com.softwaremill.sttp.client" %% "async-http-client-backend-cats" % "2.2.9",
  // https://github.com/lloydmeta/enumeratum/releases
  "com.beachape" %% "enumeratum-play" % "1.6.1",
  // https://github.com/pauldijou/jwt-scala/releases
  "com.pauldijou" %% "jwt-play" % "4.2.0",
  // https://mvnrepository.com/artifact/com.adrianhurt/play-bootstrap
  "com.adrianhurt" %% "play-bootstrap" % "1.6.1-P28-B4"
)

// https://github.com/playframework/scalatestplus-play/releases
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "io.bartholomews.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "io.bartholomews.binders._"

// https://stackoverflow.com/a/58456468
ThisBuild / useCoursier := false