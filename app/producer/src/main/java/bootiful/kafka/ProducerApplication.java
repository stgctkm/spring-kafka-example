package bootiful.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProducerApplication {

  public static void main(String[] args) {
    SpringApplication.run(ProducerApplication.class, args);
  }

  /**
   * topic in apache kafka broker
   */
  public final static String PAGE_VIEWS_TOPIC = "page_views_topic";

}
