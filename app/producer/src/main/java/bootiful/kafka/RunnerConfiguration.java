package bootiful.kafka;

import static bootiful.kafka.ProducerApplication.PAGE_VIEWS_TOPIC;

import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

@Configuration
public class RunnerConfiguration {

  void stream(StreamBridge streamBridge) {
    streamBridge.send("pageViews-out-0", random("stream"));
  }

  void kafka(KafkaTemplate<Object, Object> kafkaTemplate) {
    var pageView = (PageView) random("kafka");
    kafkaTemplate.send(PAGE_VIEWS_TOPIC, pageView);
  }

  private PageView random(String source) {

    var names = "alice,bob,chalie,delta".split(",");
    var pages = "blog.html,about.html,contact.html,news.html,index.html".split(",");

    var random = new Random();
    var name = names[random.nextInt(names.length)];
    var page = pages[random.nextInt(pages.length)];
    return new PageView(page, Math.random() > 0.5 ? 100 : 1000, name, source);
  }

  void integration(MessageChannel channel) {
    var message = MessageBuilder
        .withPayload(random("integration"))
//        .copyHeadersIfAbsent(Map.of(KafkaHeaders.TOPIC, PAGE_VIEWS_TOPIC))
        .build();
    channel.send(message);
  }

  @Bean
  ApplicationListener<ApplicationReadyEvent> runnerListener(
      KafkaTemplate<Object, Object> kafkaTemplate,
      MessageChannel channel,
      StreamBridge streamBridge) {
    return new ApplicationListener<ApplicationReadyEvent>() {
      @Override
      public void onApplicationEvent(ApplicationReadyEvent event) {
        for (int i = 0; i < 10; i++) {
          kafka(kafkaTemplate);
          integration(channel);
          stream(streamBridge);
        }
//        kafka(kafkaTemplate);
//        integration(channel);
//        stream(streamBridge);
      }
    };
  }

}
