package org.mule.tooling.platform;

import org.mule.tooling.platform.PlatformUrl.Builder;
import org.mule.tooling.platform.settings.PlatformSettingsState;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.regex.Matcher;

public class PlatformUrls {

    private static final String STUDIO_CLIENT_SECRET = "studio123";
    private static final String STUDIO_CLIENT_ID = "studio";
    private static final String ANYPOINT_ENV = "anypoint.env";
    private static final String ANYPOINT_BASEURL = "anypoint.baseurl";
    public static final String BASE_PLATFORM_PROTOCOL = "https";
    public static final String BASE_PLATFORM_HOST = "anypoint.mulesoft.com";
    public static final String BASE_PLATFORM_URL = BASE_PLATFORM_PROTOCOL + "://" + BASE_PLATFORM_HOST + "/";
    private static final String CLOUDHUB_DEBUGGING = "cloudhub.debugging";
    private static final String CLOUDHUB_STUDIO = "cloudhub/#/studio";
    public static final String EXCHANGE_PATH = "exchange/";
    private static final String STUDIO_CS_REDIRECT = "https://studio-redirect/";

    public static final String DEFAULT_LOGIN_URL = "DEFAULT_LOGIN_URL";
    public static final String ORG_MULE_TOOLING_ONPREM_URL = "org.mule.tooling.onprem.url";
    public static final String ORG_MULE_TOOLING_ONPREM_ENABLED = "org.mule.tooling.onprem.enabled";

    private static Optional<String> getEnvironmentProperty() {
        return Optional.ofNullable(System.getProperty(ANYPOINT_ENV));
    }

    public static Optional<URL> getBaseUrl() {
        String baseUrlProperty = System.getProperty(ANYPOINT_BASEURL);
        URL baseUrl = null;
        try {
            if (baseUrlProperty != null) {
                baseUrl = new URL(baseUrlProperty);
            }
            boolean onpremEnabled = PlatformSettingsState.getInstance().isOnPremise();
            if (onpremEnabled) {
                baseUrl = new URL(PlatformSettingsState.getInstance().getCustomUrl());
            }
            baseUrl = new URL(BASE_PLATFORM_URL);
            return Optional.ofNullable(baseUrl);
        } catch (MalformedURLException wrongUrlException) {
            return Optional.empty();
        }

    }

    public static String getActivePlatformUrl() {
        return getActivePlatformBuilder().build().getUrl();
    }

    public static Builder getActivePlatformBuilder() {
        Builder platformUrlBuilder = PlatformUrl.builder();
        return createBaseUrlBuilder(platformUrlBuilder);
    }

    public static String getActiveExchangeUrl() {
        return getActivePlatformBuilder().append(EXCHANGE_PATH).build().getUrl();
    }

    public static String getMavenFacadeUrl() {
        return getBaseMavenFacadeUrl();
    }

    public static String getBaseMavenFacadeUrl() {
        return getBaseMavenFacadeBuilder().build().getUrl();
    }

    private static Builder getBaseMavenFacadeBuilder() {
        return PlatformUrl.mavenServiceBuilder(getActivePlatformBuilder());
    }

    public static String getOrganizationBasedMavenFacadeUrl(String orgId) {
        return getBaseMavenFacadeBuilder().withOrganizationId(orgId).build().getUrl();
    }

    public static String getActiveGraphServiceUrl() {
        Builder platformUrlBuilder = PlatformUrl.graphServiceBuilder(getActivePlatformBuilder());
        return platformUrlBuilder.build().getUrl();
    }

    public static String getActiveExchangeXAPIUrl() {
        Builder platformUrlBuilder = PlatformUrl.exchangeXAPIBuilder(getActivePlatformBuilder());
        return platformUrlBuilder.build().getUrl();
    }

    public static String getActiveCloudhubUrl() {
        String cloudhubDebugging = getCloudhubDebuggingUrl();
        return cloudhubDebugging == null ? getActivePlatformBuilder().append(CLOUDHUB_STUDIO).build().getUrl() : cloudhubDebugging;
    }

    private static Builder createBaseUrlBuilder(Builder platformUrlBuilder) {
        getBaseUrl().ifPresent(platformUrlBuilder::withBaseUrl);
        boolean onpremEnabled = PlatformSettingsState.getInstance().isOnPremise();
        if (!onpremEnabled) {
            getEnvironmentProperty().ifPresent(platformUrlBuilder::withEnvironment);
            platformUrlBuilder.withRegion(PlatformSettingsState.getInstance().getRegion());
        }
        return platformUrlBuilder;
    }

    public static String getCloudhubDebuggingUrl() {
        return System.getProperty(CLOUDHUB_DEBUGGING);
    }

    public static String getCsRedirectPattern() {
        return Matcher.quoteReplacement(getRedirectCsUrl());
    }

    public static String getRedirectCsUrl() {
        return STUDIO_CS_REDIRECT;
    }

    public static String getStudioClientId() {
        return STUDIO_CLIENT_ID;
    }

    public static String getStudioClientSecret() {
        return STUDIO_CLIENT_SECRET;
    }

    public static String getActiveGatewayAnalyticsUrl() {
        return getActivePlatformBuilder().withPrefix("analytics-ingest").build().getUrl();
    }

    public static String getActiveGatewayPlatformUrl() {
        return getActivePlatformBuilder().build().getUrl();
    }

    public static Builder mavenServiceBuilder() {
        return PlatformUrl.mavenServiceBuilder(getActivePlatformBuilder());
    }

    public static Builder graphServiceBuilder() {
        return PlatformUrl.graphServiceBuilder(getActivePlatformBuilder());
    }

    public static Builder exchangeXAPIBuilder() {
        return PlatformUrl.exchangeXAPIBuilder(getActivePlatformBuilder());
    }

}