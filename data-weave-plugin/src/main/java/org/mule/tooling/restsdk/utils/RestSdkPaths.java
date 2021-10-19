package org.mule.tooling.restsdk.utils;

import org.mule.tooling.restsdk.datasense.RestSdkInputOutputTypesProvider;

public class RestSdkPaths {
  public static final YamlPath OPERATION_IDENTIFIER_PATH = YamlPath.DOCUMENT.child("operationIdentifier").child("expression");
  public static final YamlPath OPERATION_DISPLAY_NAME_PATH = YamlPath.DOCUMENT.child("operationDisplayName").child("expression");
  public static final YamlPath PAGINATION_PATH = YamlPath.DOCUMENT.child("paginations").any().child("pagingResponse").child("expression");
  public static final YamlPath PAGINATION_PARAMETERS = YamlPath.DOCUMENT.child("paginations").any().child(RestSdkInputOutputTypesProvider.PARAMETERS_KEY).any().child("expression");
  public static final YamlPath SECURITY_VALIDATION_PATH = YamlPath.DOCUMENT.child("security").any().child("responseValidation").any().child("validation").child("expression");
  public static final YamlPath SECURITY_ERROR_TEMPLATE_PATH = YamlPath.DOCUMENT.child("security").any().child("responseValidation").any().child("validation").child("errorTemplate");
  public static final YamlPath SECURITY_REFRESH_PATH = YamlPath.DOCUMENT.child("security").any().child("refreshTokenCondition");
  public static final YamlPath TRIGGERS_BINDING_VALUE = YamlPath.DOCUMENT.child("triggers").any().child("binding").any().any().child("value");
  public static final YamlPath TRIGGERS_BINDING_BODY_EXPRESSION = YamlPath.DOCUMENT.child("triggers").any().child("binding").any().child("expression");
  public static final YamlPath TRIGGERS_WATERMARK_PATH = YamlPath.DOCUMENT.child("triggers").any().child(RestSdkInputOutputTypesProvider.WATERMARK_KEY).child("extraction").child("expression");
  public static final YamlPath TRIGGERS_ITEMS_PATH = YamlPath.DOCUMENT.child("triggers").any().child("items").child("extraction").child("expression");
  public static final YamlPath TRIGGERS_SAMPLE_DATA_PATH = YamlPath.DOCUMENT.child("triggers").any().child("sampleData").child("transform").child("expression");
  public static final YamlPath SAMPLE_DATA_URI_PARAMETER = YamlPath.DOCUMENT.child("sampleData").any().child("definition").child("request").child("binding").any().any().child("value");
  public static final YamlPath OPERATION_VALUE_PROVIDERS = YamlPath.DOCUMENT.child("endpoints").any().child("operations").any().child("expects").child("body").any().any().child("valueProvider").child("items").any().any().child("expression");
  public static final YamlPath OPERATION_REQUEST_BODY = YamlPath.DOCUMENT.child("operations").any().child("request").child("body").child("expression");
  public static final YamlPath OPERATION_PATH = YamlPath.DOCUMENT.child("operations");
  public static final YamlPath OPERATION_URI_PARAMS_PATH = OPERATION_PATH.any().child("request").child("uriParameter");
  public static final YamlPath OPERATION_REQUEST_HEADER_PATH = OPERATION_PATH.any().child("request").child("header");
  public static final YamlPath OPERATION_QUERY_PARAMS_PATH = OPERATION_PATH.any().child("request").child("queryParameter");
  public static final YamlPath OPERATION_BASE_PATH = OPERATION_PATH.any().child("base");
  public static final YamlPath OPERATION_REQUEST_QUERY_PARAM = OPERATION_PATH.any().child("request").any().any().child("value");
  public static final YamlPath VALUE_PROVIDER_PATH = YamlPath.DOCUMENT.child("valueProviders").child("*").child("items").any().child("expression");
  public static final YamlPath TEST_CONNECTION_PATH = YamlPath.DOCUMENT.child("security").child("*").child("testConnection")
          .child("responseValidation").any().child("expression");
  public static final YamlPath PARAMETERS_SELECTOR_FROM_BODY_REQUEST = YamlPath.PARENT.parent().parent().child(RestSdkInputOutputTypesProvider.PARAMETERS_KEY);
  public static final YamlPath PARAMETERS_SELECTOR_FROM_QUERY_PARAMETER = YamlPath.PARENT.parent().parent().parent().child(RestSdkInputOutputTypesProvider.PARAMETERS_KEY);
  public static final YamlPath PARAMETERS_SELECTOR = YamlPath.PARENT.parent().parent().parent().child(RestSdkInputOutputTypesProvider.PARAMETERS_KEY);
  public static final YamlPath PARAMETERS_SELECTOR_FROM_ITEMS = YamlPath.PARENT.parent().parent().child(RestSdkInputOutputTypesProvider.PARAMETERS_KEY);
  public static final YamlPath PARAMETERS_SELECTOR_FROM_ROOT_SAMPLE_DATA = YamlPath.PARENT.parent().parent().parent().parent().parent().child(RestSdkInputOutputTypesProvider.PARAMETERS_KEY);
  public static final YamlPath API_PATH = YamlPath.DOCUMENT.child("apiSpec").child("url");
  public static final YamlPath RELATIVE_BASE_PATH = YamlPath.PARENT.parent().child("base");
}
