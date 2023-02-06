import cats.effect.{IO, Resource, OutcomeIO}
import fs2.kafka._
import cats.effect.std.Queue
import domain._


implicit def s[T <: Base]: Serializer[IO, T] = Serializer[IO].contramap[T](setData[T])

class Fs2Producer[T: Serializer[IO, _]](topic: String, url: String, queue: Queue[IO, T]) {
  private val producerSettings = ProducerSettings[IO, Long, T].withBootstrapServers(url)

  def publish(value: T): IO[Unit] = queue.offer(value)

  
  private val prodstream =
    fs2.Stream.repeatEval(queue.take).zipWithIndex
      .map { case (data, idx) =>
        val record = ProducerRecord(topic, idx, data)
        ProducerRecords.one(record)

      }
      .through(KafkaProducer.pipe(producerSettings))

  def run: Resource[IO, IO[OutcomeIO[Unit]]] = prodstream.compile.drain.background
}

object Fs2Producer {
  def make[T: Serializer[IO, _]](topic: String, url: String): IO[Fs2Producer[T]] =
    Queue.bounded[IO, T](42).map { queue =>
      new Fs2Producer(topic, url, queue)
    }

  def make2[T: Serializer[IO, _]](topic: String, url: String): Resource[IO, (IO[OutcomeIO[Unit]], Fs2Producer[T])] =
    Resource.suspend(Queue.bounded[IO, T](42).map { queue =>
      val producer = new Fs2Producer(topic, url, queue)
      producer.run.map(waiter => (waiter, producer))
    })
}
