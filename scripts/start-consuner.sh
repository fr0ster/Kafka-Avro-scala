#!/bin/sh

docker exec --interactive --tty broker \
kafka-console-consumer --bootstrap-server broker:9092 \
                       --topic $1 \
                       --from-beginning