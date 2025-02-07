## Queue Matcher POC
- Create topic:
  `./kafka-topics.sh --create --topic conduct-ingestion-topic --bootstrap-server localhost:9092`
  `./kafka-topics.sh --create --topic surveillance-command-topic --bootstrap-server localhost:9092`
- Produce data:
  `./kafka-console-producer.sh  --topic conduct-ingestion-topic --bootstrap-server localhost:9092 --property key.separator="|"  --property parse.key=true`
   1|{ "gcid": "gcid1", "tenantId": "tenant1", "content": "a b c" }
- Consume data:
  `./kafka-console-consumer.sh --topic surveillance-command-topic \
  --bootstrap-server localhost:9092 \
  --from-beginning` 