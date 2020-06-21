import sbt._

object dependencies {
  val rocksDb = List("org.rocksdb" % "rocksdbjni" % versions.rocksDB)

  val cats = List(
    "org.typelevel" %% "cats-core"   % versions.cats,
    "org.typelevel" %% "cats-effect" % versions.catsEffect,
    "org.typelevel" %% "mouse"       % versions.mouse
  )

  val newtype = List(
    "io.estatico" %% "newtype"      % versions.newtype,
    "eu.timepit"  %% "refined"      % versions.refined,
    "eu.timepit"  %% "refined-cats" % versions.refined
  )

  val tofu = List(
    "ru.tinkoff" %% "tofu-core"           % versions.tofu,
    "ru.tinkoff" %% "tofu-concurrent"     % versions.tofu,
    "ru.tinkoff" %% "tofu-env"            % versions.tofu,
    "ru.tinkoff" %% "tofu-optics-core"    % versions.tofu,
    "ru.tinkoff" %% "tofu-optics-macro"   % versions.tofu,
    "ru.tinkoff" %% "tofu-derivation"     % versions.tofu,
    "ru.tinkoff" %% "tofu-logging"        % versions.tofu,
    "ru.tinkoff" %% "tofu-logging-layout" % versions.tofu
  )

}
