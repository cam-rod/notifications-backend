package com.redhat.cloud.notifications.connector.pagerduty;

import com.redhat.cloud.notifications.connector.EngineToConnectorRouteBuilder;
import com.redhat.cloud.notifications.connector.authentication.secrets.SecretsLoader;
import com.redhat.cloud.notifications.connector.http.HttpConnectorConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.http.conn.ssl.NoopHostnameVerifier;

import static com.redhat.cloud.notifications.connector.ConnectorToEngineRouteBuilder.SUCCESS;
import static com.redhat.cloud.notifications.connector.ExchangeProperty.ID;
import static com.redhat.cloud.notifications.connector.ExchangeProperty.ORG_ID;
import static com.redhat.cloud.notifications.connector.ExchangeProperty.TARGET_URL;
import static com.redhat.cloud.notifications.connector.http.SslTrustAllManager.getSslContextParameters;
import static com.redhat.cloud.notifications.connector.pagerduty.ExchangeProperty.ACCOUNT_ID;
import static com.redhat.cloud.notifications.connector.pagerduty.ExchangeProperty.TARGET_URL_NO_SCHEME;
import static org.apache.camel.Exchange.CONTENT_TYPE;
import static org.apache.camel.LoggingLevel.INFO;
import static org.apache.camel.builder.endpoint.dsl.HttpEndpointBuilderFactory.HttpEndpointBuilder;

// TODO: rewrite for PagerDuty
@ApplicationScoped
public class PagerDutyRouteBuilder extends EngineToConnectorRouteBuilder {

    private static final String APPLICATION_JSON = "application/json; charset=utf-8";

    static final String PAGERDUTY_RESPONSE_TIME_METRIC = "micrometer:timer:pagerduty.response.time";
    static final String TIMER_ACTION_START = "?action=start";
    static final String TIMER_ACTION_STOP = "?action=stop";

    @Inject
    HttpConnectorConfig connectorConfig;

    @Inject
    SecretsLoader secretsLoader;

    @Inject
    AuthenticationProcessor authenticationProcessor;

    @Override
    public void configureRoutes() {
        from(seda(ENGINE_TO_CONNECTOR))
            .setHeader(CONTENT_TYPE, constant(APPLICATION_JSON))
            .routeId(connectorConfig.getConnectorName())
            .process(secretsLoader)
            .process(authenticationProcessor)
            .to(PAGERDUTY_RESPONSE_TIME_METRIC + TIMER_ACTION_START)
            .toD(buildPagerDutyEndpoint(), connectorConfig.getEndpointCacheMaxSize())
            .to(PAGERDUTY_RESPONSE_TIME_METRIC + TIMER_ACTION_STOP)
            .log(INFO, getClass().getName(), "Delivered event ${exchangeProperty." + ID + "} " +
                "(orgId ${exchangeProperty." + ORG_ID + "} account ${exchangeProperty." + ACCOUNT_ID + "}) " +
                "to ${exchangeProperty." + TARGET_URL + "}")
            .to(direct(SUCCESS));
    }

    private HttpEndpointBuilder buildPagerDutyEndpoint() {
        return https("${exchangeProperty." + TARGET_URL_NO_SCHEME + "}").httpMethod("POST")
                .sslContextParameters(getSslContextParameters())
                .x509HostnameVerifier(NoopHostnameVerifier.INSTANCE);
    }
}