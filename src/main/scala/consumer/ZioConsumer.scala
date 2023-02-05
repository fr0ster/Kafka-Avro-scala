import zio._
import zio.kafka.consumer._
import zio.kafka.producer.{Producer, ProducerSettings}
import zio.kafka.serde._
import zio.stream.ZStream

class ZioConsumer(topic: String, group: String, urls: List[String]):
  val consumer: ZStream[Consumer, Throwable, Nothing] =
    Consumer
      .subscribeAnd(Subscription.topics(topic))
      .plainStream(Serde.long, Serde.string)
      .tap(r => Console.printLine(r.value))
      .map(_.offset)
      .aggregateAsync(Consumer.offsetBatches)
      .mapZIO(_.commit)
      .drain

  def consumerLayer =
    ZLayer.scoped(
      Consumer.make(
        ConsumerSettings(urls).withGroupId(group)
      )
    )

  def run =
    consumer
      .runDrain
      .provide(consumerLayer)
