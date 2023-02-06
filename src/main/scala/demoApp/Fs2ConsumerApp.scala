import cats.effect.{IO, IOApp}
import fs2.kafka._
import scala.concurrent.duration._

object Fs2ConsumerApp extends IOApp.Simple:
  val run: IO[Unit] = (new Fs2AvroConsumer("topic", "zio-group", "localhost:9092", {(_, value) => println(value)})).run