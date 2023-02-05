import cats.effect.{IO, IOApp}
import fs2.kafka._
import scala.concurrent.duration._

class Fs2Consumer(topic: String, group: String, url: String, f: (key: Long, value: String) => Unit):
  val run: IO[Unit] = {
    def processRecord(record: ConsumerRecord[Long, String]): IO[(Long, String)] =
      IO.pure(record.key -> record.value)

    val consumerSettings =
      ConsumerSettings[IO, Long, String]
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
              f(key, value)
            }
        }

    stream.compile.drain
  }
