package producer

import cats.effect.kernel.{ Async, Ref, Resource }
import cats.syntax.all.*
import cats.{ Applicative, Parallel, Show }
import fs2.kafka.{ KafkaProducer, ProducerSettings }


trait Producer[F[_], A]:
  def send(a: A): F[Unit]
  def send(a: A, properties: Map[String, String]): F[Unit]

object Producer:
    def kafka[F[_]: Async, A](
        settings: ProducerSettings[F, String, A],
        topic: String
    ): Resource[F, Producer[F, A]] =
    KafkaProducer.resource(settings).map { p =>
        new:
            def send(a: A): F[Unit] =
                p.produceOne_(topic, "key", a).flatten.void
            def send(a: A, properties: Map[String, String]): F[Unit] = send(a)
    }
