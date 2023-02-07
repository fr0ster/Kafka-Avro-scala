import cats.effect.{IO, IOApp}
import fs2.kafka._
import scala.concurrent.duration._
import domain.User
import cats.effect.std.Queue
import cats.effect.unsafe.implicits.global
import fs2._

object Fs2ProducerApp extends IOApp.Simple:
  def run =
    val _stream = Stream.range(1,99999).mapAsync(25){ num => IO.pure(User(num, "one", None)) }
    val _producer = new Fs2AvroProducer[User]("topic", "localhost:9092", _stream)
    _producer.run
    // val stream = Stream.range(1,42).mapAsync(25){ num => IO.pure(User(num, "one", None)) }.through(producer.flatMap(x => x.publish()))
    // producer.flatMap(x => x.compile.drain)
  
  val producer = Fs2Producer.make[User]("topic", "localhost:9092")
  val stream = Stream(1,42).mapAsync(25){ num => producer.flatMap(x => x.publish(User(num, "one", None))) }
  val resurce = producer.map(x => x.run)
