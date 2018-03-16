package org.mule.tooling.platform;

import java.net.URL;
import java.util.Set;


import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;

public class PlatformUrl {

    private static final String API_V1_VERSION = "v1";
    private String suffix;
    private String prefix;
    private PlatformRegion region;
    private String baseUrl;
    private String protocol;
    private String organizationId;
    private String environment;
    private String apiVersion;
    private String ending;

    private static final String STG_ENV = "stg";

    private static final String QA_ENV = "qa";

    private static final String DEV_ENV = "dev";

    private static final String SLASH = "/";

    private static final String API = "api";

    private static final String DOT = ".";

    private static final String URL_PROTOCOL_SEPARATOR = "://";

    private static final String PROD_ENV = "prod";

    private static final Set<String> X_ENVIRONMENTS = Sets.newHashSet(DEV_ENV, QA_ENV, STG_ENV);

    private PlatformUrl(Builder builder) {
        this.suffix = builder.suffix;
        this.prefix = builder.prefix;
        this.region = builder.region;
        this.baseUrl = builder.baseUrl;
        this.protocol = builder.protocol;
        this.organizationId = builder.organizationId;
        this.environment = builder.environment;
        this.apiVersion = builder.apiVersion;
        this.ending = builder.ending;
    }

    private static String getEnvironment(String environment) {
        return X_ENVIRONMENTS.contains(environment) ? environment + "x" : environment;
    }

    public String getUrl() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(protocol).append(URL_PROTOCOL_SEPARATOR);
        if (StringUtils.isNotEmpty(prefix)) {
            stringBuilder.append(prefix).append(DOT);
        }
        String transformedEnvironment = getEnvironment(environment);
        if (!PROD_ENV.equals(transformedEnvironment) && StringUtils.isNotEmpty(transformedEnvironment)) {
            stringBuilder.append(transformedEnvironment).append(DOT);
        }
        if (region != null && StringUtils.isNotEmpty(region.getPrefix())) {
            stringBuilder.append(region.getPrefix()).append(DOT);
        }
        stringBuilder.append(baseUrl).append(SLASH);
        if (StringUtils.isNotEmpty(suffix)) {
            stringBuilder.append(suffix).append(SLASH);
        }
        if (StringUtils.isNotEmpty(apiVersion)) {
            stringBuilder.append(API + SLASH + apiVersion + SLASH);
        }
        if (StringUtils.isNotEmpty(organizationId)) {
            stringBuilder.append("organizations" + SLASH + organizationId + SLASH);
        }
        stringBuilder.append(ending);
        return stringBuilder.toString();
    }

    /**
     * Creates builder to build {@link PlatformUrl}.
     * 
     * @return created builder
     */
    public static Builder builder() {
        return new Builder();
    }

    public static Builder mavenServiceBuilder(Builder builder) {
        return builder.withPrefix("maven").append("maven").withApiVersion(API_V1_VERSION);
    }

    public static Builder graphServiceBuilder(Builder builder) {
        return builder.withSuffix("graph").append("graphql").withApiVersion(API_V1_VERSION);
    }

    public static Builder exchangeXAPIBuilder(Builder builder) {
        return builder.withSuffix("exchange").withApiVersion(API_V1_VERSION);
    }

    /**
     * Builder to build {@link PlatformUrl}.
     */
    public static final class Builder {

        private String ending = "";
        private String suffix = "";
        private String prefix = "";
        private PlatformRegion region;
        private String baseUrl = PlatformUrls.BASE_PLATFORM_HOST;
        private String protocol = PlatformUrls.BASE_PLATFORM_PROTOCOL;
        private String organizationId = "";
        private String environment = "";
        private String apiVersion = "";

        private Builder() {
        }

        public Builder withSuffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        public Builder withPrefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public Builder withRegion(PlatformRegion region) {
            this.region = region;
            return this;
        }

        public Builder withBaseUrl(URL baseUrl) {
            this.baseUrl = baseUrl.getHost();
            this.protocol = baseUrl.getProtocol();
            return this;
        }

        public Builder withOrganizationId(String organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        public Builder withEnvironment(String environment) {
            this.environment = environment;
            return this;
        }

        public Builder withApiVersion(String apiVersion) {
            this.apiVersion = apiVersion;
            return this;
        }

        public Builder append(String ending) {
            if (!this.ending.isEmpty() && !this.ending.endsWith(SLASH) && !ending.startsWith(SLASH)) {
                ending = SLASH + ending;
            } else if (!this.ending.isEmpty() && this.ending.endsWith(SLASH) && ending.startsWith(SLASH)) {
                ending = ending.substring(1);
            }
            this.ending += ending;
            return this;
        }

        public PlatformUrl build() {
            return new PlatformUrl(this);
        }
    }
}