# kafka_stream
In this project our application kafka have two topics. Our application read records from there two topice using Spring kafka streaming streaming API.

It is reading two streams and comparing their values wit time frame using JoinWindows. Two outgoing streams are opened 1 for all records and 1 for records that are not matching.

create springboot jar and execute jar file using following command

java -Dspring.config.location=/home/kafka_test/kafka.properties -jar kafkastream.jar >logs.log

kafka.properties file is commited in project
