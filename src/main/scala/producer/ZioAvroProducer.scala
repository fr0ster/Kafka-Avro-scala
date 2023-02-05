package producer

import zio._
import zio.kafka.producer.{Producer, ProducerSettings}
import zio.kafka.serde._
import zio.stream.ZStream
import domain._

class ZioAvroProducer(topic: String, urls: List[String], stream: ZStream[Any,Nothing,(Long, Base)]):
  val producer: ZStream[Producer, Throwable, Nothing] =
    stream
      .mapZIO { data =>
        Producer.produce[Any, Long, Array[Byte]](
          topic = topic,
          key = data(0),
          value = setData(data(1)),
          keySerializer = Serde.long,
          valueSerializer = Serde.byteArray
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
