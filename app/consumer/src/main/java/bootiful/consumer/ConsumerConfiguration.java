package bootiful.consumer;

import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.converter.JsonMessageConverter;

@Configuration
public class ConsumerConfiguration {

//  @Bean
//  JsonMessageConverter jsonMessageConverter() {
//    return new JsonMessageConverter();
//  }

  @Bean
  Function<KStream<String, PageView>, KStream<String, Long>> counter() {
    return pvs -> pvs
        .filter((s, pageView) -> pageView.duration() > 100)
        .map((s, pageView) -> new KeyValue<>(pageView.page(), 0L))
        .groupByKey(Grouped.with(Serdes.String(), Serdes.Long()))
        .count(Materialized.as("pcmv"))
        .toStream();
  }


//        @Override
//        public KStream<String, Long> apply(KStream<String, PageView> pvs) {
//          return pvs
//              .filter(new Predicate<String, PageView>() {
//                @Override
//                public boolean test(String s, PageView pageView) {
//                  return pageView.duration() > 100;
//                }
//              })
//              .map(
//                  new KeyValueMapper<String, PageView, KeyValue<? extends String, ? extends Long>>() {
//                    @Override
//                    public KeyValue<? extends String, ? extends Long> apply(String s, PageView pageView) {
//                      return new KeyValue<>(pageView.page(), 0L);
//                    }
//                  })
//              .groupByKey(Grouped.with(Serdes.String(), Serdes.Long()))
//              .count(Materialized.as("pcmv"))
//              .toStream()
//              ;

  @Bean
  Consumer<KTable<String, Long>> systemPrinter() {
    return counts -> counts.toStream()
        .foreach((s, aLong) -> System.out.println("page: " + s + " count: " + aLong));
  }

  @Bean
  Consumer<KTable<String, Long>> logger() {
    return counts -> counts.toStream()
        .foreach((s, aLong) -> System.out.println("*** page: " + s + " count: " + aLong));
  }

//  @Bean
//  Consumer<KTable<String, Long>> logger() {
//    return new Consumer<KTable<String, Long>>() {
//      @Override
//      public void accept(KTable<String, Long> counts) {
//        counts.toStream()
//            .foreach(new ForeachAction<String, Long>() {
//              @Override
//              public void apply(String s, Long aLong) {
//                System.out.println("page: " + s + " count: " + aLong);
//              }
//            });
//      }
//    };
//  }

}
