import zio._
// import zio.kafka.consumer._
import zio.kafka.producer.{Producer, ProducerSettings}
import zio.kafka.serde._
import zio.stream.ZStream

class ZioStreamProducer(topic: String, urls: List[String], stream: ZStream[Any,Nothing,Int]):
  val producer: ZStream[Producer, Throwable, Nothing] =
    stream
      .mapZIO { random =>
        Producer.produce[Any, Long, String](
          topic = topic,
          key = random % 4,
          value = random.toString,
          keySerializer = Serde.long,
          valueSerializer = Serde.string
        )
      }
      .drain

  def producerLayer =
    ZLayer.scoped(
      Producer.make(
        settings = ProducerSettings(urls)
      )
    )

  def run =
    producer
      .runDrain
      .provide(producerLayer)
