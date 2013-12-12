name := "Fillable"

version := "1.0-SNAPSHOT"


libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.mockito" % "mockito-all" % "1.9.5",
  "org.elasticsearch" % "elasticsearch" % "0.90.7",
  "org.elasticsearch" % "elasticsearch" % "0.90.7" % "test" classifier "tests"
)
                        
play.Project.playScalaSettings
