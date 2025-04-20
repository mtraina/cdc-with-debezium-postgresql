inspired by
* https://dev.to/emtiajium/track-every-postgresql-data-change-using-debezium-5e19
* https://medium.com/@arijit.mazumdar/beyond-the-basics-of-debezium-for-postgresql-part-1-d1c6952ae110
* https://medium.com/@techphile/using-postgres-and-debezium-connector-for-cdc-9fea124f124a
* https://dev.to/thegroo/simplest-spring-kafka-producer-and-consumer-kotlin-version-dn8

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
* Config arguments
  * Basic: JSON_STRING=$(jq -n --arg ip "$IP" '{"name": "cdc-with-debezium-connector", "config": {"connector.class": "io.debezium.connector.postgresql.PostgresConnector","database.hostname": $ip, "database.port": "5443","database.user": "postgres","database.password": "123","database.dbname": "cdc-with-debezium","database.server.id": "184054","table.include.list": "public.User","topic.prefix": "cdc-with-debezium-topic"}}')
  * Simplified payload: JSON_STRING=$(jq -n --arg ip "$IP" '{"name": "cdc-with-debezium-connector", "config": {"connector.class": "io.debezium.connector.postgresql.PostgresConnector","database.hostname": $ip, "database.port": "5443","database.user": "postgres","database.password": "123","database.dbname": "cdc-with-debezium","database.server.id": "184054","table.include.list": "public.User","topic.prefix": "cdc-with-debezium-topic","transforms": "unwrap","transforms.unwrap.type": "io.debezium.transforms.ExtractNewRecordState","transforms.unwrap.drop.tombstones": "true", "transforms.unwrap.delete.handling.mode": "rewrite", "transforms.unwrap.add.fields": "op,table,lsn"}}')
  * Simplified payload, no schema: JSON_STRING=$(jq -n --arg ip "$IP" '{"name": "cdc-with-debezium-connector", "config": {"connector.class": "io.debezium.connector.postgresql.PostgresConnector", "database.hostname": $ip, "database.port": "5443", "database.user": "postgres", "database.password": "123", "database.dbname": "cdc-with-debezium", "database.server.id": "184054", "table.include.list": "public.User", "topic.prefix": "cdc-with-debezium-topic", "transforms": "unwrap", "transforms.unwrap.type": "io.debezium.transforms.ExtractNewRecordState", "transforms.unwrap.drop.tombstones": "true", "transforms.unwrap.delete.handling.mode": "rewrite", "transforms.unwrap.add.fields": "op,table,lsn", "key.converter": "org.apache.kafka.connect.json.JsonConverter", "key.converter.schemas.enable": "false", "value.converter": "org.apache.kafka.connect.json.JsonConverter", "value.converter.schemas.enable": "false"}}')

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
VALUES ('test@test.com', 'name_0');

UPDATE "User"
SET name = 'name_1'
WHERE email = 'test@test.com';

DELETE
FROM "User"
WHERE email = 'test@test.com';
```

## Kafka (III)
* docker exec cdc-with-debezium-kafka /opt/bitnami/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic cdc-with-debezium-topic.public.User --from-beginning | jq '.'
