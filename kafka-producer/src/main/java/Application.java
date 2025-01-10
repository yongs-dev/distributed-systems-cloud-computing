import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Application {

    private static final String TOPIC = "events";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9093,localhost:9094";

    public static void main(String[] args) {
        Producer<Long, String> kafkaProducer = createKafkaProducer(BOOTSTRAP_SERVERS);

        try {
            produceMessages(10, kafkaProducer);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            kafkaProducer.flush();
            kafkaProducer.close();
        }
    }

    public static void produceMessages(int numberOfMessages, Producer<Long, String> kafkaProducer) throws ExecutionException, InterruptedException {
//        int partition = 0;
//        int partition = 1;

        for (int i = 0; i < numberOfMessages; i++) {
//            ProducerRecord<Long, String> record = new ProducerRecord<>(TOPIC, partition, System.currentTimeMillis(), (long) i, String.format("event %d", i));

            // 라운드 로빈 방식을 통해 파티션 지정
//            ProducerRecord<Long, String> record = new ProducerRecord<>(TOPIC, (long) i, String.format("event %d", i));

            // key 해시를 통해 파티션 지정
            ProducerRecord<Long, String> record = new ProducerRecord<>(TOPIC, (long) i, String.format("event %d", i));
            RecordMetadata recordMetadata = kafkaProducer.send(record).get();

            System.out.printf("Record with (key: %s, value: %s), was sent to (partition: %d, offset: %d)%n",
                    record.key(), record.value(), recordMetadata.partition(), recordMetadata.offset());
        }
    }

    public static Producer<Long, String> createKafkaProducer(String bootstrapServers) {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "events-producer");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<>(properties);
    }
}
