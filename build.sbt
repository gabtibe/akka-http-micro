name := "akka-http-micro"

version := "0.1"

scalaVersion := "2.12.6"

enablePlugins(DockerPlugin)

val dockerRepo = sys.env.getOrElse("DOCKER_REPO", "tibertig")

val akkaVersion = "2.5.12"
val akkaHttpVersion = "10.1.3"
val circeVersion = "0.9.3"
val alpakkaVersion = "0.20"

resolvers ++= Seq(Resolver.bintrayRepo("hsseberger", "maven"),
                  Resolver.jcenterRepo)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "de.heikoseeberger" %% "akka-http-circe" % "1.21.0",
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "com.lightbend.akka" %% "akka-stream-alpakka-mongodb" % alpakkaVersion,
//  Test Dependencies
  "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  "org.pegdown" % "pegdown" % "1.6.0" % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
  "com.github.fakemongo" % "fongo" % "2.1.0" % Test
)

testOptions in Test ++= Seq(
  Tests.Argument(TestFrameworks.ScalaTest, "-u", "target/test-reports"),
  Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/test-reports")
)

lazy val root = (project in file("."))
  .configs(Configs.all: _*)
  .settings(Testing.settings: _*)
  .settings()

assemblyJarName in assembly := s"${organization.value}-${name.value}-${version.value}"

test in assembly := {}

dockerfile in docker := {
  val artifact = assembly.value
  val artifactTargetPath = s"/app/${artifact.name}"

  new Dockerfile {
    from("anapsix/alpine-java:latest")
    add(artifact, artifactTargetPath)
    entryPoint("java", "-jar", artifactTargetPath)
  }
}

imageNames in docker := Seq(
  ImageName(s"${organization.value}/${name.value}:latest"),
  ImageName(
    namespace = Some(organization.value),
    repository = name.value,
    tag = Some(s"v${version.value}")
  )
)

buildOptions in docker := BuildOptions(
  cache = false,
  removeIntermediateContainers = BuildOptions.Remove.Always,
  pullBaseImage = BuildOptions.Pull.Always
)
