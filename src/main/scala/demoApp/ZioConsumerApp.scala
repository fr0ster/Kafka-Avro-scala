import zio._

object ZioConsumerApp extends ZIOAppDefault:
  val consumer = new ZioConsumer("topic", "zio-group", List("localhost:9092"))

  override def run =
    consumer.run
