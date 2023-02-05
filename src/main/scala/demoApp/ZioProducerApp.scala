import zio._
import zio.stream.ZStream
import zio.kafka.serde._
import org.apache.kafka.clients.producer.Producer

object ZioProducerApp extends ZIOAppDefault:
  val randomIntStream = 
    ZStream.repeatZIO(Random.nextIntBetween(0, Int.MaxValue)).schedule(Schedule.fixed(2.seconds))
  val randomStringStream = 
    ZStream.repeatZIO(Random.nextIntBetween(0, Int.MaxValue)).map("Hello, World").schedule(Schedule.fixed(2.seconds))
  val streamproducer = new ZioStreamProducer("topic", List("localhost:9092"), randomIntStream)
  // val streamproducer = new ZioStreamProducer("topic", List("localhost:9092"), randomStringStream)
  override def run =
    streamproducer.run
    // stream.run
  
  // val test = ZioProducer("topic", List("localhost:9092"), 0l, "Hello, World!!!", Serde.long, Serde.string)
  // val stream = ZStream(test).drain.runDrain.provide()