name := "kafka-avro"

version := "1.0"

scalaVersion := "2.13.10"

libraryDependencies ++= Seq(
      "org.apache.avro" % "avro" % "1.11.1",
      "org.apache.kafka" % "kafka_2.11" % "0.10.0.0",
     // "io.confluent" % "kafka-avro-serializer" % "5.3.0",
      "ch.qos.logback" %  "logback-classic" % "1.4.5"
)

/*resolvers ++= Seq(
      Resolver.sonatypeRepo("public"),
      "Confluent Maven Repo" at "http://packages.confluent.io/maven/"
)*/
