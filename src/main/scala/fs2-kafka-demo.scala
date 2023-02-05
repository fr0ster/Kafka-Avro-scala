import cats.effect.{IO, IOApp}
import fs2.kafka._
import scala.concurrent.duration._

object Fs2MainApp extends IOApp.Simple {
  val run: IO[Unit] = {
    def processRecord(record: ConsumerRecord[Long, String]): IO[(Long, String)] =
      IO.pure(record.key -> record.value)

    val consumerSettings =
      ConsumerSettings[IO, Long, String]
        .withAutoOffsetReset(AutoOffsetReset.Earliest)
        .withBootstrapServers("localhost:9092")
        .withGroupId("fs2-group")

    val producerSettings =
      ProducerSettings[IO, Long, String]
        .withBootstrapServers("localhost:9092")

    val stream =
      KafkaConsumer.stream(consumerSettings)
        .subscribeTo("topic")
        .records
        .mapAsync(25) { committable =>
          processRecord(committable.record)
            .map { case (key, value) =>
              val record = ProducerRecord("quickstart", key, value)
              ProducerRecords.one(record, committable.offset)
            }
        }
        .through(KafkaProducer.pipe(producerSettings))
        .map(_.passthrough)
        .through(commitBatchWithin(500, 15.seconds))

    stream.compile.drain
  }
}
