## Queue Matcher POC
#### Setup
- Prod nam supervision_library_list { type: 'LEXICON', name: 'oc_bem_xxx_resolve_complaint'}
- Set `AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY` in environment variables
- Test `AWS_ACCESS_KEY_ID=minioadmin AWS_SECRET_ACCESS_KEY=minioadmin aws --endpoint-url=http://localhost:9000 s3 ls`
- Login to Minio console and create `supervision` bucket. Drop indexable jsons. Make sure to give same path while raising event on topic `conduct-ingestion-topic`

#### Create topics
  `./kafka-topics.sh --create --topic conduct-ingestion-topic --bootstrap-server localhost:9092`
  
  `./kafka-topics.sh --create --topic surveillance-command-topic --bootstrap-server localhost:9092`
  
  `./kafka-topics.sh --create --topic policy-execution-audit-topic --bootstrap-server localhost:9092`
  
#### Produce data
  `./kafka-console-producer.sh  --topic conduct-ingestion-topic --bootstrap-server localhost:9092 --property key.separator="|"  --property parse.key=true`
  
  `default-ef884e9d7f47a5fd1178e23cf9f7bb17|{ "gcid": "default-ef884e9d7f47a5fd1178e23cf9f7bb17", "tenantId": "principal", "bucketName": "supervision", "absoluteFileName":"/bulk-indexing/2025-02-10/13/index_json_eg1" }`
  
  `journal-115370be8722854525a536733b7e6f92|{ "gcid": "journal-115370be8722854525a536733b7e6f92", "tenantId": "jpmc", "bucketName": "supervision", "absoluteFileName":"/bulk-indexing/2025-02-10/13/index_json_eg2" }`
  
  `journal-adf929992a375d5f3a7ecda450ad496a|{ "gcid": "journal-adf929992a375d5f3a7ecda450ad496a", "tenantId": "jpmc", "bucketName": "supervision", "absoluteFileName":"/bulk-indexing/2025-02-10/13/index_json_eg3" }`

#### Consume data
  `./kafka-console-consumer.sh --topic policy-execution-audit-topic --bootstrap-server localhost:9092` 
  
  `./kafka-console-consumer.sh --topic surveillance-command-topic --bootstrap-server localhost:9092` 
