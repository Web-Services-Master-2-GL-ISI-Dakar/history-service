package sn.ondmoney.history;

import sn.ondmoney.history.config.AsyncSyncConfiguration;
import sn.ondmoney.history.config.EmbeddedElasticsearch;
import sn.ondmoney.history.config.EmbeddedKafka;
import sn.ondmoney.history.config.EmbeddedMongo;
import sn.ondmoney.history.config.EmbeddedRedis;
import sn.ondmoney.history.config.JacksonConfiguration;
import sn.ondmoney.history.config.TestSecurityConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = { HistoryServiceApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class, TestSecurityConfiguration.class }
)
@EmbeddedRedis
@EmbeddedMongo
@EmbeddedElasticsearch
@EmbeddedKafka
public @interface IntegrationTest {
}
