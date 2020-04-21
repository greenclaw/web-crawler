name := "web-crawler"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.1.1",
  "org.scalatest" %% "scalatest" % "3.1.1" % "test",
  "org.scalatestplus" %% "junit-4-12" % "3.1.1.0",
  "org.scalatestplus" %% "scalatestplus-junit" % "1.0.0-M2",
  "com.novocode" % "junit-interface" % "0.11" % "test",
  "org.slf4j" % "slf4j-simple" % "1.7.25",
  "org.scalaj" %% "scalaj-http" % "2.4.2"
)
