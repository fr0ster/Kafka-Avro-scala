import cats.effect.{IO, IOApp}
import fs2.kafka._
import scala.concurrent.duration._

object Fs2ProducerApp extends IOApp.Simple:
  // val run: IO[Unit] = (new Fs2Produser("topic", "zio-group", "localhost:9092", {(_, value) => println(value)})).run
  // def processRecord(record: ConsumerRecord[Long, String]): IO[(Long, String)] =
  //     IO.pure(record.key -> record.value)
  def run = IO.println("Helo, Vasya!!!")