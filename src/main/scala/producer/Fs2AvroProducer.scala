import cats.effect.IO
import fs2.kafka._
import scala.concurrent.duration._
import domain._

class Fs2AvroProducer[T <: Base](topic: String, url: String, stream: fs2.Stream[IO, T]):
    val producerSettings =
      ProducerSettings[IO, Long, Array[Byte]].withBootstrapServers(url)

    def processRecord(record: (T, Long)): IO[(Long, Array[Byte])] =
      IO.pure(record(1) -> setData[T](record(0)))

    val prodstream =
        stream.zipWithIndex
        .mapAsync(25) { data =>
          processRecord(data)
            .map { case (key, value) =>
              val record = ProducerRecord(topic, key, value)
              ProducerRecords.one(record)
            }
        }
        .through(KafkaProducer.pipe(producerSettings))
        .map(_.passthrough)
        // .through(commitBatchWithin(500, 15.seconds))

    def run = prodstream.compile.drain
