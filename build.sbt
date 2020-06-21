import dependencies._

organization := "com.github.timofeyGusev"
scalaVersion := "2.13.2"
version := "0.0.1"

libraryDependencies ++= cats ++ rocksDb

addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.11.0" cross CrossVersion.full)
addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1")

scalacOptions ++= Seq(
  "-Ymacro-annotations",
  "-deprecation",
  "-feature",
  "-Xfatal-warnings",
  "-language:higherKinds",
  "-language:existentials",
  "-language:implicitConversions"
)
