package consumer

import zio._
import zio.kafka.consumer._
import zio.kafka.producer.{Producer, ProducerSettings}
import zio.kafka.serde._
import zio.stream.ZStream
import domain._
import org.apache.kafka.clients.consumer.ConsumerRecord

class ZioAvroConsumer[T <: Base](
    topic: String,
    group: String,
    urls: List[String],
    f: Base => zio.ZIO[zio.kafka.consumer.Consumer, Throwable, Any]
):
  val consumer: ZStream[Consumer, Throwable, Nothing] =
    Consumer
      .subscribeAnd(Subscription.topics(topic))
      .plainStream(Serde.long, Serde.byteArray)
      .tap(r => f(getData(r.value)))
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
    consumer.runDrain
      .provide(consumerLayer)
