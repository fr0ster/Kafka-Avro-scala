# Kafka-Avro-scala

Usage

Start kafka in docker
https://developer.confluent.io/quickstart/kafka-docker/
```docker compose up -d -f ./kafka-single.yml``` or ```docker compose up -d -f ./kafka-cluster.yml```

Create a topic
docker exec broker \
kafka-topics --bootstrap-server broker:9092 \
             --create \
             --topic quickstart

Write messages to the topic
docker exec --interactive --tty broker \
kafka-console-producer --bootstrap-server broker:9092 \
                       --topic quickstart

Read messages from the topic
docker exec --interactive --tty broker \
kafka-console-consumer --bootstrap-server broker:9092 \
                       --topic quickstart \
                       --from-beginning

Write some more messages
docker exec --interactive --tty broker \
kafka-console-producer --bootstrap-server broker:9092 \
                       --topic quickstart

Stop the Kafka broker
docker-compose down
