package bootiful.kafka;

import static bootiful.kafka.ProducerApplication.PAGE_VIEWS_TOPIC;

import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.messaging.Message;

@Configuration
public class KafkaConfiguration {

  Logger logger = LoggerFactory.getLogger(KafkaConfiguration.class);


  @KafkaListener(topics = PAGE_VIEWS_TOPIC, groupId = "pv_topic_group")
  public void onNewPageView(Message<PageView> pageView) {
    System.out.println("------------------------");
    System.out.println("new page view: " + pageView.getPayload());
    pageView.getHeaders().forEach((s, o) -> System.out.println(s + " = " + o));

    logger.info("------------------------");
    logger.info("new page view: " + pageView.getPayload());
    pageView.getHeaders().forEach((s, o) -> logger.info(s + " = " + o));
  }

  @Bean
  NewTopic pageViewsTopic() {
    return new NewTopic(PAGE_VIEWS_TOPIC, 1, (short) 1);
  }

  @Bean
  JsonMessageConverter jsonMessageConverter() {
    return new JsonMessageConverter();
  }

  @Bean
  KafkaTemplate<Object, Object> kafkaTemplate(ProducerFactory<Object, Object> producerFactory) {
    return new KafkaTemplate<>(producerFactory,
        Map.of(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class));
  }

}
