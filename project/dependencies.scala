import sbt._

object dependencies {
  val rocksDb = List("org.rocksdb" % "rocksdbjni" % versions.rocksDB)

  val cats = List(
    "org.typelevel" %% "cats-core"   % versions.cats,
    "org.typelevel" %% "cats-effect" % versions.catsEffect,
    "org.typelevel" %% "mouse"       % versions.mouse
  )
}
