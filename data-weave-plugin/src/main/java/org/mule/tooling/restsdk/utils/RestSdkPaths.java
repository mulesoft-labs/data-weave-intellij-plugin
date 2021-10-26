package org.mule.tooling.restsdk.utils;

import org.mule.tooling.restsdk.datasense.RestSdkInputOutputTypesProvider;

public class RestSdkPaths {
  public static final SelectionPath OPERATION_IDENTIFIER_PATH = SelectionPath.DOCUMENT.child("operationIdentifier").child("expression");
  public static final SelectionPath OPERATION_DISPLAY_NAME_PATH = SelectionPath.DOCUMENT.child("operationDisplayName").child("expression");

  public static final SelectionPath PAGINATION_PATH = SelectionPath.DOCUMENT.child("paginations").any().child("pagingResponse").child("expression");
  public static final SelectionPath PAGINATION_PARAMETERS = SelectionPath.DOCUMENT.child("paginations").any().child(RestSdkInputOutputTypesProvider.PARAMETERS_KEY).any().child("expression");

  public static final SelectionPath SECURITY_VALIDATION_PATH = SelectionPath.DOCUMENT.child("security").any().child("responseValidation").any().child("validation").child("expression");
  public static final SelectionPath SECURITY_ERROR_TEMPLATE_PATH = SelectionPath.DOCUMENT.child("security").any().child("responseValidation").any().child("validation").child("errorTemplate");
  public static final SelectionPath SECURITY_REFRESH_PATH = SelectionPath.DOCUMENT.child("security").any().child("refreshTokenCondition");
  public static final SelectionPath SECURITY_TEST_CONNECTION_PATH = SelectionPath.DOCUMENT.child("security").child("*").child("testConnection").child("responseValidation").any().child("expression");

  public static final SelectionPath TRIGGERS_PATH = SelectionPath.DOCUMENT.child("triggers");
  public static final SelectionPath TRIGGERS_PATH_PATH = TRIGGERS_PATH.any().child("path");
  public static final SelectionPath TRIGGERS_BINDING_BODY_EXPRESSION = TRIGGERS_PATH.any().child("binding").child("body").child("expression");
  public static final SelectionPath TRIGGERS_BINDING_QUERY_PARAMS_PATH = TRIGGERS_PATH.any().child("binding").child("queryParameter");
  public static final SelectionPath TRIGGERS_BINDING_URI_PARAMETER_PATH = TRIGGERS_PATH.any().child("binding").child("header");
  public static final SelectionPath TRIGGERS_BINDING_HEADER_PATH = TRIGGERS_PATH.any().child("binding").child("uriParameter");
  public static final SelectionPath TRIGGERS_WATERMARK_PATH = TRIGGERS_PATH.any().child(RestSdkInputOutputTypesProvider.WATERMARK_KEY).child("extraction").child("expression");
  public static final SelectionPath TRIGGERS_EVENT_PATH = TRIGGERS_PATH.any().child("event").child("extraction").child("expression");
  public static final SelectionPath TRIGGERS_ITEMS_PATH = TRIGGERS_PATH.any().child("items").child("extraction").child("expression");
  public static final SelectionPath TRIGGERS_IDENTITY_EXTRACTION_PATH = TRIGGERS_PATH.any().child("identity").child("extraction").child("expression");
  public static final SelectionPath TRIGGERS_SAMPLE_DATA_PATH = TRIGGERS_PATH.any().child("sampleData").child("transform").child("expression");

  public static final SelectionPath RELATIVE_TRIGGER_METHOD_FROM_BINDING_PATH = SelectionPath.PARENT.parent().child("method");
  public static final SelectionPath RELATIVE_TRIGGER_PATH_FROM_BINDING_PATH = SelectionPath.PARENT.parent().child("path");

  public static final SelectionPath RELATIVE_TRIGGER_METHOD_FROM_BINDING_BODY_PATH = SelectionPath.PARENT.parent().parent().child("method");
  public static final SelectionPath RELATIVE_TRIGGER_PATH_FROM_BINDING_BODY_PATH = SelectionPath.PARENT.parent().parent().child("path");




  public static final SelectionPath GLOBAL_SAMPLE_DATA_PATH = SelectionPath.DOCUMENT.child("sampleData");
  public static final SelectionPath GLOBAL_SAMPLE_DATA_BINDING_BODY_EXPRESSION = GLOBAL_SAMPLE_DATA_PATH.any().child("definition").child("request").child("binding").child("body").child("expression");
  public static final SelectionPath GLOBAL_SAMPLE_DATA_BINDING_QUERY_PARAMS_PATH = GLOBAL_SAMPLE_DATA_PATH.any().child("definition").child("request").child("binding").child("queryParameter");
  public static final SelectionPath GLOBAL_SAMPLE_DATA_BINDING_URI_PARAMETER_PATH = GLOBAL_SAMPLE_DATA_PATH.any().child("definition").child("request").child("binding").child("uriParameter");
  public static final SelectionPath GLOBAL_SAMPLE_DATA_BINDING_HEADER_PATH = GLOBAL_SAMPLE_DATA_PATH.any().child("definition").child("request").child("binding").child("header");
  public static final SelectionPath GLOBAL_SAMPLE_DATA_PATH_PATH = GLOBAL_SAMPLE_DATA_PATH.any().child("definition").child("request").child("path");
  public static final SelectionPath GLOBAL_SAMPLE_DATA_BINDING_REQUEST_EXPRESSION_PATH = GLOBAL_SAMPLE_DATA_PATH.any().child("definition").child("request").child("binding").any().any().child("value");
  public static final SelectionPath RELATIVE_GLOBAL_SAMPLE_DATA_BINDING_REQUEST_EXPRESSION_PARAMETERS_PATH = SelectionPath.PARENT.parent().parent().parent().parent().parent().child("parameters");
  public static final SelectionPath GLOBAL_SAMPLE_DATA_TRANSFORM_EXPRESSION_PATH = GLOBAL_SAMPLE_DATA_PATH.any().child("definition").child("transform").child("expression");
  public static final SelectionPath RELATIVE_GLOBAL_SAMPLE_DATA_TRANSFORM_EXPRESSION_PARAMETERS_PATH = SelectionPath.PARENT.parent().parent().child("parameters");


  public static final SelectionPath OPERATION_VALUE_PROVIDERS_PATH = SelectionPath.DOCUMENT.child("endpoints").any().child("operations").any().child("expects").child("body").any().any().child("valueProvider").child("items").any().any().child("expression");
  public static final SelectionPath OPERATION_REQUEST_BODY_PATH = SelectionPath.DOCUMENT.child("operations").any().child("request").child("body").child("expression");
  public static final SelectionPath OPERATION_PATH = SelectionPath.DOCUMENT.child("operations");
  public static final SelectionPath OPERATION_URI_PARAMS_PATH = OPERATION_PATH.any().child("request").child("uriParameter");
  public static final SelectionPath OPERATION_REQUEST_HEADER_PATH = OPERATION_PATH.any().child("request").child("header");
  public static final SelectionPath OPERATION_QUERY_PARAMS_PATH = OPERATION_PATH.any().child("request").child("queryParameter");
  public static final SelectionPath OPERATION_BASE_PATH = OPERATION_PATH.any().child("base");
  public static final SelectionPath OPERATION_REQUEST_QUERY_PARAM = OPERATION_PATH.any().child("request").any().any().child("value");

  public static final SelectionPath VALUE_PROVIDER_PATH = SelectionPath.DOCUMENT.child("valueProviders").child("*").child("items").any().child("expression");

  public static final SelectionPath API_PATH = SelectionPath.DOCUMENT.child("apiSpec").child("url");

  //Relative Paths Selectors
  public static final SelectionPath PARAMETERS_SELECTOR_FROM_BODY_REQUEST_PATH = SelectionPath.PARENT.parent().parent().child(RestSdkInputOutputTypesProvider.PARAMETERS_KEY);
  public static final SelectionPath PARAMETERS_SELECTOR_FROM_QUERY_PARAMETER_PATH = SelectionPath.PARENT.parent().parent().parent().child(RestSdkInputOutputTypesProvider.PARAMETERS_KEY);
  public static final SelectionPath RELATIVE_TRIGGER_PARAMETERS_SELECTOR_FROM_BINDING_BODY_PATH = SelectionPath.PARENT.parent().parent().child(RestSdkInputOutputTypesProvider.PARAMETERS_KEY);
  public static final SelectionPath PARAMETERS_SELECTOR_FROM_ITEMS_PATH = SelectionPath.PARENT.parent().parent().child(RestSdkInputOutputTypesProvider.PARAMETERS_KEY);
  public static final SelectionPath PARAMETERS_SELECTOR_FROM_ROOT_SAMPLE_DATA_PATH = SelectionPath.PARENT.parent().parent().parent().parent().parent().child(RestSdkInputOutputTypesProvider.PARAMETERS_KEY);
  public static final SelectionPath RELATIVE_OPERATION_BASE_FROM_REQUEST_PATH = SelectionPath.PARENT.parent().child("base");
  public static final SelectionPath RELATIVE_OPERATION_BASE_FROM_BODY_EXPRESSION_PATH = SelectionPath.PARENT.parent().parent().child("base");
}
