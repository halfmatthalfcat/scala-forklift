val repoKind = SettingKey[String]("repo-kind",
  "Maven repository kind (\"snapshots\" or \"releases\")")

lazy val slickVersion = "3.3.2"

def coreDependencies(scalaVersion: String) = List(
  "org.scala-lang" % "scala-compiler" % scalaVersion,
  "com.typesafe" % "config" % "1.3.0",
  "org.eclipse.jgit" % "org.eclipse.jgit" % "4.0.1.201506240215-r"
)

lazy val slickDependencies = List(
  "com.typesafe.slick" %% "slick" % slickVersion,
  "com.typesafe.slick" %% "slick-codegen" % slickVersion,
  "io.github.nafg" %% "slick-migration-api" % "0.7.0"
)

lazy val slickDependenciesWithTests = slickDependencies ++ List(
  "org.scalatest" %% "scalatest" % "3.1.0",
  "com.lihaoyi" %% "ammonite-ops" % "2.0.4",
  "commons-io" % "commons-io" % "2.6",
  "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
  "com.h2database" % "h2" % "1.4.200",
  "org.xerial" % "sqlite-jdbc" % "3.30.1",
  "mysql" % "mysql-connector-java" % "8.0.18",
  "org.postgresql" % "postgresql" % "42.2.9",
  "org.hsqldb" % "hsqldb" % "2.5.0",
  "org.apache.derby" % "derby" % "10.15.1.3"
).map(_ % "test")

lazy val commonSettings = Seq(
  organization := "com.liyaos",
  licenses := Seq("Apache 2.0" ->
    url("https://github.com/lastland/scala-forklift/blob/master/LICENSE")),
  homepage := Some(url("https://github.com/lastland/scala-forklift")),
  scalaVersion := "2.13.1",
  scalacOptions += "-deprecation",
  scalacOptions += "-feature",
  resolvers += Resolver.bintrayRepo("naftoligug", "maven"),
  publishMavenStyle := true,
  publishArtifact in Test := false,
  repoKind := { if (version.value.trim.endsWith("SNAPSHOT")) "snapshots"
                else "releases" },
  publishTo := { repoKind.value match {
    case "snapshots" => Some("snapshots" at
        "https://oss.sonatype.org/content/repositories/snapshots")
    case "releases" =>  Some("releases"  at
        "https://oss.sonatype.org/service/local/staging/deploy/maven2")
  }},
  credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
  pomExtra := (
    <scm>
      <url>git@github.com:lastland/scala-forklift.git</url>
      <connection>scm:git:git@github.com:lastland/scala-forklift.git</connection>
      </scm>
      <developers>
      <developer>
      <id>lastland</id>
      <name>Yao Li</name>
      </developer>
      </developers>))

lazy val root = Project(
  "scala-forklift", file(".")).settings(
  crossScalaVersions := Seq("2.13.1", "2.12.1"),
  publishArtifact := false).aggregate(
  coreProject, slickMigrationProject, plainMigrationProject, gitToolProject)

lazy val coreProject = Project(
  "scala-forklift-core", file("core")).settings(
  commonSettings:_*).settings {
  libraryDependencies ++= coreDependencies(scalaVersion.value)
}

lazy val slickMigrationProject = Project(
  "scala-forklift-slick", file("migrations/slick")).dependsOn(
  coreProject).settings(commonSettings:_*).settings {
  libraryDependencies ++= slickDependenciesWithTests
}

lazy val plainMigrationProject = Project(
  "scala-forklift-plain", file("migrations/plain")).dependsOn(
  coreProject).settings(commonSettings:_*)

lazy val gitToolProject = Project(
  "scala-forklift-git-tools", file("tools/git")).dependsOn(
  coreProject).settings(commonSettings:_*)
