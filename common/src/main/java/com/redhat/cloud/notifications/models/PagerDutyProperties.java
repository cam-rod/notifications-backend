package com.redhat.cloud.notifications.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.redhat.cloud.notifications.models.validation.ValidNonPrivateUrl;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import static com.redhat.cloud.notifications.Constants.PAGERDUTY_EVENT_V2_URL;

@Entity
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class) // TODO remove them once the transition to DTOs have been completed.
@Table(name = "pagerduty_properties")
// TODO integrate with everything else for PagerDuty
public class PagerDutyProperties extends EndpointProperties implements SourcesSecretable {
    /**
     * The PagerDuty API uses a single endpoint for requests from all users, distinguished by the Integration Key
     * stored in the {@link PagerDutyProperties#secretToken}. This URL does not need to be provided by end users, and is
     * not exposed in the API, but is included in this Entity for future migrations.
     */
    @NotNull
    @JsonIgnore // TODO remove them once the transition to DTOs have been completed.
    @ValidNonPrivateUrl
    private String url = PAGERDUTY_EVENT_V2_URL;

    @NotNull
    @Size(max = 255)
    @Transient
    private String secretToken;

    /**
     * The ID of the "secret token" secret in the Sources backend.
     */
    @Column(name = "secret_token_id")
    @JsonIgnore // TODO remove them once the transition to DTOs have been completed.
    private Long secretTokenSourcesId;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getSecretToken() {
        return secretToken;
    }

    @Override
    public void setSecretToken(String secretToken) {
        this.secretToken = secretToken;
    }

    @Override
    public Long getSecretTokenSourcesId() {
        return secretTokenSourcesId;
    }

    @Override
    public void setSecretTokenSourcesId(Long secretTokenSourcesId) {
        this.secretTokenSourcesId = secretTokenSourcesId;
    }

    @Override
    public BasicAuthentication getBasicAuthentication() {
        return null;
    }

    @Override
    public void setBasicAuthentication(BasicAuthentication basicAuthentication) {
        // Do nothing
    }

    @Override
    public Long getBasicAuthenticationSourcesId() {
        return null;
    }

    @Override
    public void setBasicAuthenticationSourcesId(Long basicAuthenticationSourcesId) {
        // do nothing
    }

    @Override
    public String getBearerAuthentication() {
        return null;
    }

    @Override
    public void setBearerAuthentication(String bearerAuthentication) {
        // do nothing
    }

    @Override
    public Long getBearerAuthenticationSourcesId() {
        return null;
    }

    @Override
    public void setBearerAuthenticationSourcesId(Long bearerAuthenticationSourcesId) {
        // do nothing
    }
}
