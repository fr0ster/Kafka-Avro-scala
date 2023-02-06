package domain

import com.sksamuel.avro4s.{AvroSchema, AvroOutputStream, AvroInputStream}
import java.io.{ByteArrayOutputStream}
import org.apache.avro.io.Encoder

sealed trait Base
case class User(id: Int, name: String, email: Option[String]) extends Base

def setData[T <: Base](data: T): Array[Byte] =
  // Serialize generic record into byte array
  val outputStream = new ByteArrayOutputStream()
  val os = AvroOutputStream.data[T].to(outputStream).build()
  os.write(data)
  os.flush()
  os.close()
  outputStream.toByteArray()

def getData[T <: Base](channel: Array[Byte]) =
  // Deserialize and create generic record
  val is = AvroInputStream.data[T].from(channel).build(AvroSchema[T])
  val data = is.iterator.toSeq
  is.close()
  data(0)