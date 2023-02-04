name := "kafka-avro"

version := "1.0"

scalaVersion := "2.13.10"

libraryDependencies ++= Seq(
      "org.apache.avro" % "avro" % "1.11.1",
      "com.github.fd4s" %% "fs2-kafka" % "3.0.0-M8",
      "org.apache.kafka" % "kafka_2.13" % "3.3.2",
     // "io.confluent" % "kafka-avro-serializer" % "5.3.0",
      "org.apache.kafka" % "kafka-examples" % "3.3.2",
      "ch.qos.logback" %  "logback-classic" % "1.4.5",
      "org.apache.kafka" % "kafka-streams" % "3.3.2",
      "org.scalatest" %% "scalatest" % "3.3.0-SNAP3" % Test
)