import dependencies._
import sbt.Keys.scalaVersion

lazy val settings = List(
  organization := "timofeyGusev",
  scalaVersion := "2.13.0",
  version := "0.1.0",
  name := "rdb4cats",
  resolvers += Resolver.sonatypeRepo("public"),
  resolvers += Resolver.sonatypeRepo("snapshots"),
  scalacOptions ++= scalaOpts,
  libraryDependencies ++= compilerPlugins
)

lazy val scalaOpts = List(
  "-Ymacro-annotations",
  "-deprecation",
  "-feature",
  "-Xfatal-warnings",
  "-language:higherKinds",
  "-language:existentials",
  "-language:implicitConversions"
)

lazy val compilerPlugins = List(
  compilerPlugin(
    "org.typelevel"           %% "kind-projector"     % "0.11.0" cross CrossVersion.full
  ),
  compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
)

lazy val rdb4cats = project
  .in(file("."))
  .withId("rdb4cats")
  .settings(moduleName := "rdb4cats", name := "rdb4cats")
  .settings(settings)
  .aggregate(core, versioned, demo)

lazy val versioned =
  project
    .in(file("versioned"))
    .withId("versioned")
    .settings(moduleName := "rdb4catsVersioned", name := "rdb4catsVersioned")
    .settings(settings)
    .settings(libraryDependencies ++= cats ++ rocksDb ++ newtype ++ tofu)
    .dependsOn(core)

lazy val core =
  project
    .in(file("core"))
    .withId("core")
    .settings(moduleName := "rdb4catsCore", name := "rdb4catsCore")
    .settings(settings)
    .settings(libraryDependencies ++= cats ++ rocksDb ++ newtype ++ tofu)

lazy val demo =
  project
    .in(file("demo"))
    .withId("demo")
    .settings(moduleName := "rdb4catsDemo", name := "rdb4catsDemo")
    .settings(settings)
    .settings(libraryDependencies ++= cats ++ rocksDb ++ newtype ++ tofu)
    .dependsOn(core, versioned)