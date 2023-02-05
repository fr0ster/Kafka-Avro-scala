import cats.effect.{IO, IOApp}
import fs2.kafka._
import scala.concurrent.duration._

class Fs2Producer(topic: String, url: String, stream: fs2.Stream[cats.effect.IO, ProducerRecords[CommittableOffset[cats.effect.IO], Long, String]]):
  val run: IO[Unit] =
    // def processRecord(record: ConsumerRecord[Long, String]): IO[(Long, String)] =
    //   IO.pure(record.key -> record.value)

    val producerSettings =
      ProducerSettings[IO, Long, String]
        .withBootstrapServers(url)

    // val consumerSettings =
    //   ConsumerSettings[IO, Long, String]
    //     .withAutoOffsetReset(AutoOffsetReset.Earliest)
    //     .withBootstrapServers(url)
    //     .withGroupId(group)

    val prodstream =
    //   KafkaConsumer.stream(consumerSettings)
    //     .subscribeTo("topic")
    //     .records
    //     .mapAsync(25) { committable =>
    //       processRecord(committable.record)
    //         .map { case (key, value) =>
    //           val record = ProducerRecord("quickstart", key, value)
    //           ProducerRecords.one(record, committable.offset)
    //         }
    //     }
        stream
        .through(KafkaProducer.pipe(producerSettings))
        .map(_.passthrough)
        .through(commitBatchWithin(500, 15.seconds))

    prodstream.compile.drain
