package com.ondmoney.sn.history;

import com.ondmoney.sn.history.config.AsyncSyncConfiguration;
import com.ondmoney.sn.history.config.EmbeddedElasticsearch;
import com.ondmoney.sn.history.config.EmbeddedKafka;
import com.ondmoney.sn.history.config.EmbeddedMongo;
import com.ondmoney.sn.history.config.EmbeddedRedis;
import com.ondmoney.sn.history.config.JacksonConfiguration;
import com.ondmoney.sn.history.config.TestSecurityConfiguration;
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
