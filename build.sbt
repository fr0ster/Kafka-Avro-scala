name := "kafka-avro"

version := "1.0"

scalaVersion := "3.2.2"

libraryDependencies ++= Seq(
      "com.sksamuel.avro4s" %% "avro4s-core" % "5.0.3",
      "com.github.fd4s" %% "fs2-kafka" % "3.0.0-M8",
      "dev.zio" %% "zio-kafka" % "2.0.6",
      "org.slf4j" % "slf4j-api" % "1.7.5",
      "org.slf4j" % "slf4j-simple" % "1.7.5"
)
