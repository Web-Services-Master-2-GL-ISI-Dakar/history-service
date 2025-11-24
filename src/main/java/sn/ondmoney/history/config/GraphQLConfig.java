package sn.ondmoney.history.config;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import graphql.scalars.ExtendedScalars;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Locale;

@Configuration
public class GraphQLConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        GraphQLScalarType instantScalar = GraphQLScalarType.newScalar()
            .name("Instant")
            .description("Java Instant Scalar")
            .coercing(new Coercing<Instant, String>() {

                @Override
                public String serialize(Object dataFetcherResult, GraphQLContext graphQLContext, Locale locale)
                    throws CoercingSerializeException {
                    if (dataFetcherResult instanceof Instant) {
                        return dataFetcherResult.toString();
                    }
                    throw new CoercingSerializeException("Expected Instant type, but got: " +
                        (dataFetcherResult != null ? dataFetcherResult.getClass().getSimpleName() : "null"));
                }

                @Override
                public Instant parseValue(Object input, GraphQLContext graphQLContext, Locale locale) {
                    if (input instanceof String) {
                        try {
                            return Instant.parse((String) input);
                        } catch (DateTimeParseException e) {
                            throw new CoercingSerializeException("Invalid Instant format: " + input +
                                ". Expected ISO-8601 format like '2024-01-15T10:30:00Z'");
                        }
                    }
                    throw new CoercingSerializeException("Expected String type, but got: " +
                        (input != null ? input.getClass().getSimpleName() : "null"));
                }
            })
            .build();

        return wiringBuilder -> wiringBuilder
            .scalar(instantScalar)
            .scalar(ExtendedScalars.GraphQLBigDecimal);
    }
}
