package consumer

import java.util.Properties
import scala.io.Source

import domain.User

import org.apache.avro.Schema
import org.apache.avro.io.{DatumReader, Decoder, DecoderFactory}
import org.apache.avro.specific.SpecificDatumReader
import org.apache.avro.generic.GenericRecord

import org.apache.kafka.clients.consumer.{KafkaConsumer, ConsumerConfig}


class MyKafkaConsumer() {
  private val props = new Properties()

  val groupId = "demo-topic-consumer"
  val topic = "demo-topic"

  props.put("group.id", groupId)
  props.put("zookeeper.connect", "localhost:2181")
  props.put("auto.offset.reset", "smallest")
  props.put("consumer.timeout.ms", "120000")
  props.put("auto.commit.interval.ms", "10000")

  private val consumerConfig = new ConsumerConfig(props)
  private val consumerConnector = new KafkaConsumer(props)
  val topics = consumerConnector.listTopics()
  // private val filterSpec = new Whitelist(topic)
  // private val streams = consumerConnector.createMessage StreamsByFilter(filterSpec, 1, new DefaultDecoder(), new DefaultDecoder())(0)

  // lazy val iterator = streams.iterator()

  val schemaString = Source.fromURL(getClass.getResource("/schema.avsc")).mkString
  // Initialize schema
  val schema: Schema = new Schema.Parser().parse(schemaString)

  private def getUser(message: Array[Byte]): Option[User] = {

    // Deserialize and create generic record
    val reader: DatumReader[GenericRecord] = new SpecificDatumReader[GenericRecord](schema)
    val decoder: Decoder = DecoderFactory.get().binaryDecoder(message, null)
    val userData: GenericRecord = reader.read(null, decoder)

    // Make user object
    val user = User(userData.get("id").toString.toInt, userData.get("name").toString, try {
      Some(userData.get("email").toString)
    } catch {
      case _ : Throwable => None
    })
    Some(user)
  }

  /**
    * Read message from kafka queue
    *
    * @return Some of message if exist in kafka queue, otherwise None
    */
  def read() =
    try {
      if (hasNext) {
        println("Getting message from queue.............")
        val message: Array[Byte] = Array() // iterator.next().message()
        getUser(message)
      } else {
        None
      }
    } catch {
      case ex: Exception => ex.printStackTrace()
        None
    }

  private def hasNext: Boolean = ???
    // try
    //    iterator.hasNext()
    // catch {
    //   case timeOutEx: ConsumerTimeoutException =>
    //     false
    //   case ex: Exception => ex.printStackTrace()
    //     println("Got error when reading message ")
    //     false
    // }

  def close(): Unit = consumerConnector.close()

}