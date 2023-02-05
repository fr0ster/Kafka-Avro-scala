import zio._
import zio.kafka.consumer._
import zio.kafka.producer.{Producer, ProducerSettings}
import zio.kafka.serde._
import zio.stream.ZStream

class ZioProducer[R, K, V](topic: String, urls: List[String], key: K, value: V, keySerializer: Serializer[R, K], valueSerializer:  Serializer[R, V]):
  def apply[R, K, V](topic: String, key: K, value: V, keySerializer: Serializer[R, K], valueSerializer:  Serializer[R, V]) = {
        Producer.produce[R, K, V](
          topic = topic,
          key = key,
          value = value,
          keySerializer = keySerializer,
          valueSerializer = valueSerializer
        )
      }
  def producerLayer =
    ZLayer.scoped(
      Producer.make(
        settings = ProducerSettings(urls)
      )
    )

// class ZioProducer(topic: String, urls: List[String], stream: ZStream[Any,Nothing,Int]):
//   val producer: ZStream[Producer, Throwable, Nothing] =
//     stream
//       .mapZIO { random =>
//         Producer.produce[Any, Long, String](
//           topic = topic,
//           key = random % 4,
//           value = random.toString,
//           keySerializer = Serde.long,
//           valueSerializer = Serde.string
//         )
//       }
//       .drain

  // def producerLayer =
  //   ZLayer.scoped(
  //     Producer.make(
  //       settings = ProducerSettings(urls)
  //     )
  //   )

  // def run =
  //   producer
  //     .runDrain
  //     .provide(producerLayer)
