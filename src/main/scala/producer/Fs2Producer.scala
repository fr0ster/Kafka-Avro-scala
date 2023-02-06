// Thanks to Alexey Shcherbakov @ajIeks for this code
// https://scastie.scala-lang.org/D7ftQ4iISbSXtHPbuANruQ
// вот так будет каноничнее, вынести сериализацию в соответствующий
// класс + никаких var внутри, а делаеv zipWithIndex
// https://scastie.scala-lang.org/PTf8gY2kSCyOjTHg6EfW7Q
// вот так можешь, создаешь методом make продьюсера в эффекте,
// он создает очередь, в которую через метод publish можно заталкивать T
// быть запустить еше run, чтоб очередь начала разгребаться
// либо как тут https://scastie.scala-lang.org/DoLvEJ1xRiO8shbIlw5S6Q
// make2, сразу завернуть все в ресурс
// https://scastie.scala-lang.org/eJzdASDpSeqXHvd39ZFxEA
// попрвил зависимости и даже не краснит )

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
