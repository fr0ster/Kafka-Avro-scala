import cats.effect.{IO, IOApp}
import fs2._
import fs2.kafka._
import scala.concurrent.duration._
import domain.User

object Fs2AvroProducerApp extends IOApp.Simple:
  def run =
    val stream = Stream.range(1,99999).mapAsync(25){ num => IO.pure(User(num, "one", None)) }
    val producer = new Fs2AvroProducer[User]("topic", "localhost:9092", stream)
    producer.run