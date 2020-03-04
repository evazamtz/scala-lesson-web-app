name := "scala-web"

version := "1.0"

scalaVersion := "2.12.10"

val http4sVersion = "0.20.15"

// Only necessary for SNAPSHOT releases
resolvers += Resolver.sonatypeRepo("snapshots")

val circeVersion = "0.12.3"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  // Optional for string interpolation to JSON model

  "org.slf4j" % "slf4j-simple" % "1.7.22",
  "org.slf4j" % "slf4j-api" % "1.7.22",
  "org.scala-lang" % "scala-reflect" % scalaVersion.value ,
  "com.github.pureconfig" %% "pureconfig" % "0.12.3" 
)

scalacOptions ++= Seq("-Ypartial-unification")


