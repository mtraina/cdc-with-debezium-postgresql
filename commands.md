inspired by https://dev.to/emtiajium/track-every-postgresql-data-change-using-debezium-5e19

## Docker
* docker-compose up
* docker ps

## PostgreSQL
* docker exec cdc-with-debezium-postgres psql --username=postgres --dbname=cdc-with-debezium --command='SHOW wal_level'
* docker exec cdc-with-debezium-postgres psql --username=postgres --dbname=cdc-with-debezium --command='SHOW shared_preload_libraries'

```sql
CREATE TABLE "User"
(
    "id"    uuid              NOT NULL DEFAULT gen_random_uuid(),
    "email" character varying NOT NULL,
    "name"  character varying,
    CONSTRAINT "UQ_User_email" UNIQUE ("email"),
    CONSTRAINT "PK_User_id" PRIMARY KEY ("id")
)
```

## Kafka
* curl localhost:8083 | jq '.'

## Debezium
* IP=$(ipconfig getifaddr en0)
* echo $IP

* curl -i -X DELETE localhost:8083/connectors/cdc-with-debezium-connector/
* JSON_STRING=$(jq -n --arg ip "$IP" '{"name": "cdc-with-debezium-connector", "config": {"connector.class": "io.debezium.connector.postgresql.PostgresConnector","database.hostname": $ip, "database.port": "5443","database.user": "postgres","database.password": "123","database.dbname": "cdc-with-debezium","database.server.id": "184054","table.include.list": "public.User","topic.prefix": "cdc-with-debezium-topic"}}')


```json
curl --location 'http://localhost:8083/connectors' \
   --header 'Accept: application/json' \
   --header 'Content-Type: application/json' \
   --data $JSON_STRING
```

## Kafka (II)
* server=localhost:9092
* docker exec cdc-with-debezium-kafka /opt/bitnami/kafka/bin/kafka-metadata-quorum.sh --bootstrap-server $server describe --status
* docker exec cdc-with-debezium-kafka /opt/bitnami/kafka/bin/kafka-topics.sh --bootstrap-server $server --list

## PostgreSQL (II)
```sql
INSERT INTO "User" (email, name)
VALUES ('ehasan+1@firecrackervocabulary.com', CONCAT('name_', (random() * 1000)::INTEGER::VARCHAR))
ON CONFLICT (email) DO UPDATE SET name = EXCLUDED.name
RETURNING *;
```

## Kafka (III)
docker exec cdc-with-debezium-kafka /opt/bitnami/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic cdc-with-debezium-topic.public.User --from-beginning | jq '.'
