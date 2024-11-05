package bootiful.kafka;

import static bootiful.kafka.ProducerApplication.PAGE_VIEWS_TOPIC;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.MessageChannel;

@Configuration
public class IntegrationConfiguration {

  @Bean
  IntegrationFlow integrationFlow(MessageChannel channel, KafkaTemplate<Object, Object> kafkaTemplate) {
    var kafka = Kafka.outboundChannelAdapter(kafkaTemplate)
        .topic(PAGE_VIEWS_TOPIC)
        .getObject();

    return IntegrationFlow
        .from(channel)
        .handle(kafka)
        .get();
  }

  @Bean
  MessageChannel channel() {
    return MessageChannels.direct().getObject();
  }
}
