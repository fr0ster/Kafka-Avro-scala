
import consumer.ZioAvroConsumer
import zio._
import zio.kafka.consumer.CommittableRecord
import domain.{User,Base}

object ZioConsumerApp extends ZIOAppDefault:
  val f = (r: Base) => Console.printLine(r)
  val consumer = new ZioAvroConsumer[User]("topic", "zio-group", List("localhost:9092"), f)
  override def run =
    consumer.run
