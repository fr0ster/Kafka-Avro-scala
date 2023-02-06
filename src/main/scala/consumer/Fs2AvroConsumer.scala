import cats.effect.{IO, IOApp}
import fs2.kafka._
import scala.concurrent.duration._
import domain._

class Fs2AvroConsumer[T <: Base](topic: String, group: String, url: String, f: (key: Long, value: T) => Unit):
  val run: IO[Unit] = {
    def processRecord(record: ConsumerRecord[Long, Array[Byte]]): IO[(Long, Array[Byte])] =
      IO.pure(record.key -> record.value)

    val consumerSettings =
      ConsumerSettings[IO, Long, Array[Byte]]
        .withAutoOffsetReset(AutoOffsetReset.Earliest)
        .withBootstrapServers(url)
        .withGroupId(group)

    val rawstream =
      KafkaConsumer.stream(consumerSettings)

    val stream =
      rawstream
        .subscribeTo(topic)
        .records
        .mapAsync(25) { committable =>
          processRecord(committable.record)
            .map { case (key, value) =>
              f(key, getData[T](value))
            }
        }

    stream.compile.drain
  }
