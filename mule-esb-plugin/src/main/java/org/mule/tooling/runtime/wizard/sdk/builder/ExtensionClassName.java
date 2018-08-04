package org.mule.tooling.runtime.wizard.sdk.builder;

import com.squareup.javapoet.ClassName;

public interface ExtensionClassName {

    String ANNOTATIONS_PACKAGE = "org.mule.runtime.extension.api.annotation";
    String CONNECTION_PACKAGE = "org.mule.runtime.api.connection";

    ClassName EXTENSION_ANNOTATION = ClassName.get(ANNOTATIONS_PACKAGE, "Extension");
    ClassName OPERATIONS_ANNOTATION = ClassName.get(ANNOTATIONS_PACKAGE, "Operations");
    ClassName CONNECTION_PROVIDERS_ANNOTATION = ClassName.get("org.mule.runtime.extension.api.annotation.connectivity", "ConnectionProviders");
    ClassName OPTIONAL_ANNOTATION = ClassName.get("org.mule.runtime.extension.api.annotation.param", "Optional");
    ClassName PARAMETER_ANNOTATION = ClassName.get("org.mule.runtime.extension.api.annotation.param", "Parameter");
    ClassName CONNECTION_ANNOTATION = ClassName.get("org.mule.runtime.extension.api.annotation.param", "Connection");
    ClassName CONFIG_ANNOTATION = ClassName.get("org.mule.runtime.extension.api.annotation.param", "Config");
    ClassName SOURCES_ANNOTATION = ClassName.get("org.mule.runtime.extension.api.annotation", "Sources");
    ClassName DISPLAY_NAME_ANNOTATION = ClassName.get("org.mule.runtime.extension.api.annotation.param.display", "DisplayName");
    ClassName MEDIA_TYPE_ANNOTATION = ClassName.get("org.mule.runtime.extension.api.annotation.param", "MediaType");

    ClassName CACHED_CONNECTION_PROVIDER = ClassName.get(CONNECTION_PACKAGE, "CachedConnectionProvider");
    ClassName POOLED_CONNECTION_PROVIDER = ClassName.get(CONNECTION_PACKAGE, "PooledConnectionProvider");
    ClassName NONE_CONNECTION_PROVIDER = ClassName.get(CONNECTION_PACKAGE, "ConnectionProvider");

    ClassName CONNECTION_VALIDATION_RESULT = ClassName.get(CONNECTION_PACKAGE, "ConnectionValidationResult");
    ClassName RESULT = ClassName.get("org.mule.runtime.extension.api.runtime.operation", "Result");

    ClassName CATEGORY_CLASS = ClassName.get("org.mule.runtime.api.meta", "Category");
    ClassName CONNECTION_EXCEPTION = ClassName.get(CONNECTION_PACKAGE, "ConnectionException");

    ClassName SOURCE = ClassName.get("org.mule.runtime.extension.api.runtime.source", "Source");
    ClassName SOURCE_CALLBACK = ClassName.get("org.mule.runtime.extension.api.runtime.source", "SourceCallback");
    ClassName SOURCE_CALLBACK_CONTEXT = ClassName.get("org.mule.runtime.extension.api.runtime.source", "SourceCallbackContext");
    ClassName SOURCE_RESULT = ClassName.get("org.mule.runtime.extension.api.runtime.source", "SourceResult");
    ClassName ON_SUCCESS = ClassName.get("org.mule.runtime.extension.api.annotation.execution", "OnSuccess");
    ClassName ON_ERROR = ClassName.get("org.mule.runtime.extension.api.annotation.execution", "OnError");
    ClassName ON_TERMINATE = ClassName.get("org.mule.runtime.extension.api.annotation.execution", "OnTerminate");

    ClassName SCHEDULER_SERVICE = ClassName.get("org.mule.runtime.api.scheduler", "SchedulerService");

    ClassName LOGGER = ClassName.get("org.slf4j", "Logger");
    ClassName LOGGER_FACTORY = ClassName.get("org.slf4j", "LoggerFactory");
}
