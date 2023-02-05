import zio._
import zio.stream.ZStream
import zio.kafka.serde._
import org.apache.kafka.clients.producer.Producer
import domain.User
import producer._

object ZioProducerApp extends ZIOAppDefault:
  val randomAvroStream = 
    ZStream.repeatZIO(Random.nextIntBetween(0, Int.MaxValue)).map(x => (x.toLong,User(x,"Hello",None))).schedule(Schedule.fixed(2.seconds))
  val streamproducer = new ZioAvroProducer("topic", List("localhost:9092"), randomAvroStream)
  override def run =
    streamproducer.run