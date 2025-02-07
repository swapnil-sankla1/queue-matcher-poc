## Queue Matcher POC
- Create topic:
  `./kafka-topics.sh --create --topic conduct-ingestion-topic --bootstrap-server localhost:9092`
  `./kafka-topics.sh --create --topic surveillance-command-topic --bootstrap-server localhost:9092`
  `./kafka-topics.sh --create --topic policy-execution-audit-topic --bootstrap-server localhost:9092`
- Produce data:
  `./kafka-console-producer.sh  --topic conduct-ingestion-topic --bootstrap-server localhost:9092 --property key.separator="|"  --property parse.key=true`
   1|{ "gcid": "gcid1", "tenantId": "tenant1", "content": "a b c" }
   2|{ "gcid": "gcid1", "tenantId": "tenant1", "content": "a b c from hr@smarsh.com" }
   3|{ "gcid": "gcid1", "tenantId": "tenant1", "content": "a b c from hr@caizin.com" }
   4|{ "gcid": "gcid1", "tenantId": "tenant1", "content": "d" }

- Consume data:
  `./kafka-console-consumer.sh --topic policy-execution-audit-topic \
  --bootstrap-server localhost:9092 \
  --from-beginning` 