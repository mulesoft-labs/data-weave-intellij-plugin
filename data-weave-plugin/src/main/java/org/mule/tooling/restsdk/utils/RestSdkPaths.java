package org.mule.tooling.restsdk.utils;

import org.mule.tooling.restsdk.datasense.RestSdkInputOutputTypesProvider;

public class RestSdkPaths {

  //Element Names
  public static final String QUERY_PARAMETERS = "queryParameters";
  public static final String PARAMETERS = "parameters";
  public static final String URI_PARAMETERS = "uriParameters";
  public static final String OPERATION_IDENTIFIER = "operationIdentifier";
  public static final String EXPRESSION = "expression";
  public static final String OPERATION_DISPLAY_NAME = "operationDisplayName";
  public static final String PAGINATIONS = "paginations";
  public static final String PAGING_RESPONSE = "pagingResponse";
  public static final String SECURITY = "security";
  public static final String RESPONSE_VALIDATION = "responseValidation";
  public static final String VALIDATION = "validation";
  public static final String REFRESH_TOKEN_CONDITION = "refreshTokenCondition";
  public static final String TEST_CONNECTION = "testConnection";
  public static final String TRIGGERS = "triggers";
  public static final String PATH = "path";
  public static final String SUMMARY = "summary";
  public static final String BINDING = "binding";
  public static final String EVENT = "event";
  public static final String DEFINITION = "definition";
  public static final String REQUEST = "request";
  public static final String KIND = "kind";
  public static final String HEADERS = "headers";
  public static final String BODY = "body";
  public static final String VALUE = "value";
  public static final String WATERMARK = "watermark";
  public static final String EXTRACTION = "extraction";
  public static final String ITEMS = "items";
  public static final String IDENTITY = "identity";
  public static final String SAMPLE_DATA = "sampleData";
  public static final String TRANSFORM = "transform";
  public static final String METHOD = "method";
  public static final String OPERATIONS = "operations";
  public static final String RESPONSE = "response";
  public static final String BASE = "base";
  public static final String OPERATION_ID = "operationId";
  public static final String VALUE_PROVIDERS = "valueProviders";
  public static final String DISPLAY_NAME = "displayName";
  public static final String REQUIRED = "required";
  public static final String DESCRIPTION = "description";
  public static final String MEDIA_TYPE = "mediaType";
  public static final String API_SPEC = "apiSpec";
  public static final String ENDPOINT = "endpoint";
  public static final String URL = "url";
  public static final String ENDPOINTS = "endpoints";
  public static final SelectionPath CONNECTOR_NAME_PATH = SelectionPath.DOCUMENT.child("connectorName");
  public static final SelectionPath OPERATION_IDENTIFIER_PATH = SelectionPath.DOCUMENT.child(OPERATION_IDENTIFIER).child(EXPRESSION);
  public static final SelectionPath OPERATION_DISPLAY_NAME_PATH = SelectionPath.DOCUMENT.child(OPERATION_DISPLAY_NAME).child(EXPRESSION);
  public static final SelectionPath PAGINATION_PATH = SelectionPath.DOCUMENT.child(PAGINATIONS).any().child(PAGING_RESPONSE).child(EXPRESSION);
  public static final SelectionPath PAGINATION_PARAMETERS = SelectionPath.DOCUMENT.child(PAGINATIONS).any().child(RestSdkInputOutputTypesProvider.PARAMETERS_KEY).any().child(EXPRESSION);
  public static final SelectionPath SECURITY_VALIDATION_PATH = SelectionPath.DOCUMENT.child(SECURITY).any().child(RESPONSE_VALIDATION).any().child(VALIDATION).child(EXPRESSION);
  public static final SelectionPath SECURITY_ERROR_TEMPLATE_PATH = SelectionPath.DOCUMENT.child(SECURITY).any().child(RESPONSE_VALIDATION).any().child(VALIDATION).child("errorTemplate");
  public static final SelectionPath SECURITY_REFRESH_PATH = SelectionPath.DOCUMENT.child(SECURITY).any().child(REFRESH_TOKEN_CONDITION);
  public static final SelectionPath TEST_CONNECTION_PATH = SelectionPath.DOCUMENT.child(TEST_CONNECTION).child(RESPONSE_VALIDATION).any().child(VALIDATION).child(VALUE).child(EXPRESSION);
  public static final SelectionPath TRIGGERS_PATH = SelectionPath.DOCUMENT.child(TRIGGERS);
  public static final SelectionPath TRIGGERS_PATH_PATH = TRIGGERS_PATH.any().child(PATH);
  public static final SelectionPath TRIGGERS_PARAMETER = TRIGGERS_PATH.any().child(PARAMETERS);
  public static final SelectionPath TRIGGERS_BINDING_BODY_EXPRESSION = TRIGGERS_PATH.any().child(BINDING).child(BODY).child(VALUE).child(EXPRESSION);
  public static final SelectionPath TRIGGERS_BINDING_QUERY_PARAMS_PATH = TRIGGERS_PATH.any().child(BINDING).child(QUERY_PARAMETERS);
  public static final SelectionPath TRIGGERS_BINDING_QUERY_PARAMS_EXPRESSION_PATH = TRIGGERS_BINDING_QUERY_PARAMS_PATH.any().child(VALUE).child(EXPRESSION);
  public static final SelectionPath TRIGGERS_BINDING_URI_PARAMETER_PATH = TRIGGERS_PATH.any().child(BINDING).child("headers");
  public static final SelectionPath TRIGGERS_BINDING_URI_PARAMETER_EXPRESSION_PATH = TRIGGERS_BINDING_URI_PARAMETER_PATH.any().child(VALUE);
  public static final SelectionPath TRIGGERS_BINDING_HEADER_PATH = TRIGGERS_PATH.any().child(BINDING).child(URI_PARAMETERS);
  public static final SelectionPath TRIGGERS_BINDING_HEADER_EXPRESSION_PATH = TRIGGERS_BINDING_HEADER_PATH.any().child(VALUE).child(EXPRESSION);
  public static final SelectionPath TRIGGERS_WATERMARK_PATH = TRIGGERS_PATH.any().child(RestSdkInputOutputTypesProvider.WATERMARK_KEY).child(VALUE).child(EXPRESSION);
  public static final SelectionPath TRIGGERS_EVENT_PATH = TRIGGERS_PATH.any().child(EVENT).child(VALUE).child(EXPRESSION);
  public static final SelectionPath TRIGGERS_ITEMS_PATH = TRIGGERS_PATH.any().child(ITEMS).child(VALUE).child(EXPRESSION);
  public static final SelectionPath TRIGGERS_IDENTITY_EXTRACTION_PATH = TRIGGERS_PATH.any().child(IDENTITY).child(VALUE).child(EXPRESSION);

  public static final SelectionPath RELATIVE_TRIGGER_METHOD_FROM_BINDING_PATH = SelectionPath.PARENT.parent().parent().child(METHOD);
  public static final SelectionPath RELATIVE_TRIGGER_PATH_FROM_BINDING_PATH = SelectionPath.PARENT.parent().parent().child(PATH);

  public static final SelectionPath RELATIVE_TRIGGER_METHOD_FROM_BINDING_BODY_PATH = SelectionPath.PARENT.parent().parent().parent().child(METHOD);
  public static final SelectionPath RELATIVE_TRIGGER_PATH_FROM_BINDING_BODY_PATH = SelectionPath.PARENT.parent().parent().parent().child(PATH);


  public static final SelectionPath







          GLOBAL_SAMPLE_DATA_PATH = SelectionPath.DOCUMENT.child(SAMPLE_DATA);
  public static final SelectionPath GLOBAL_SAMPLE_DATA_PARAMETER = GLOBAL_SAMPLE_DATA_PATH.any().child(PARAMETERS);

  public static final SelectionPath GLOBAL_SAMPLE_DATA_BINDING_BODY_EXPRESSION = GLOBAL_SAMPLE_DATA_PATH.any().child(DEFINITION).child(REQUEST).child(BINDING).child(BODY).child(EXPRESSION);
  public static final SelectionPath GLOBAL_SAMPLE_DATA_BINDING_QUERY_PARAMS_PATH = GLOBAL_SAMPLE_DATA_PATH.any().child(DEFINITION).child(REQUEST).child(BINDING).child(QUERY_PARAMETERS);
  public static final SelectionPath GLOBAL_SAMPLE_DATA_BINDING_URI_PARAMETER_PATH = GLOBAL_SAMPLE_DATA_PATH.any().child(DEFINITION).child(REQUEST).child(BINDING).child(URI_PARAMETERS);

  public static final SelectionPath GLOBAL_SAMPLE_DATA_BINDING_HEADER_PATH = GLOBAL_SAMPLE_DATA_PATH.any().child(DEFINITION).child(REQUEST).child(BINDING).child(HEADERS);
  public static final SelectionPath GLOBAL_SAMPLE_DATA_PATH_PATH = GLOBAL_SAMPLE_DATA_PATH.any().child(DEFINITION).child(REQUEST).child(PATH);
  public static final SelectionPath GLOBAL_SAMPLE_DATA_BINDING_REQUEST_EXPRESSION_PATH = GLOBAL_SAMPLE_DATA_PATH.any().child(DEFINITION).child(REQUEST).child(BINDING).any().any().child(VALUE);
  public static final SelectionPath RELATIVE_GLOBAL_SAMPLE_DATA_BINDING_REQUEST_EXPRESSION_PARAMETERS_PATH = SelectionPath.PARENT.parent().parent().parent().parent().parent().child(PARAMETERS);
  public static final SelectionPath GLOBAL_SAMPLE_DATA_TRANSFORM_EXPRESSION_PATH = GLOBAL_SAMPLE_DATA_PATH.any().child(DEFINITION).child(TRANSFORM).child(VALUE).child(EXPRESSION);
  public static final SelectionPath RELATIVE_GLOBAL_SAMPLE_DATA_TRANSFORM_EXPRESSION_PARAMETERS_PATH = SelectionPath.PARENT.parent().parent().parent().child(PARAMETERS);

  public static final SelectionPath OPERATION_REQUEST_BODY_PATH = SelectionPath.DOCUMENT.child(OPERATIONS).any().child(REQUEST).child(BODY).child(VALUE).child(EXPRESSION);
  public static final SelectionPath OPERATION_PATH = SelectionPath.DOCUMENT.child(OPERATIONS);
  public static final SelectionPath OPERATION_PARAMETER = OPERATION_PATH.any().child(PARAMETERS);

  public static final SelectionPath OPERATION_URI_PARAMS_PATH = OPERATION_PATH.any().child(REQUEST).child(URI_PARAMETERS);
  public static final SelectionPath OPERATION_URI_PARAMS_EXPRESSION_PATH = OPERATION_URI_PARAMS_PATH.any().child(VALUE).child(EXPRESSION);
  public static final SelectionPath OPERATION_REQUEST_HEADER_PATH = OPERATION_PATH.any().child(REQUEST).child(HEADERS);
  public static final SelectionPath OPERATION_REQUEST_HEADER_EXPRESSION_PATH = OPERATION_REQUEST_HEADER_PATH.any().child(VALUE).child(EXPRESSION);
  public static final SelectionPath OPERATION_RESPONSE_BODY_PATH = OPERATION_PATH.any().child(RESPONSE).child(BODY).child(VALUE).child(EXPRESSION);

  public static final SelectionPath OPERATION_QUERY_PARAMS_PATH = OPERATION_PATH.any().child(REQUEST).child(QUERY_PARAMETERS);
  public static final SelectionPath OPERATION_QUERY_PARAMS_EXPRESSION_PATH = OPERATION_QUERY_PARAMS_PATH.any().child(VALUE).child(EXPRESSION);
  public static final SelectionPath OPERATION_BASE_PATH = OPERATION_PATH.any().child(BASE).child(OPERATION_ID);

  public static final SelectionPath VALUE_PROVIDERS_PATH = SelectionPath.DOCUMENT.child(VALUE_PROVIDERS);

  public static final SelectionPath ENDPOINTS_PATH = SelectionPath.DOCUMENT.child(ENDPOINTS);

  public static final SelectionPath ENDPOINTS_METHOD_PATH = SelectionPath.DOCUMENT.child(ENDPOINTS).any().child(OPERATIONS);

  public static final SelectionPath VALUE_PROVIDERS_DEFINITION = VALUE_PROVIDERS_PATH.any().child(DEFINITION);
  public static final SelectionPath VALUE_PROVIDERS_ITEMS_EXTRACTION_EXPRESSION_PATH = VALUE_PROVIDERS_DEFINITION.child(ITEMS).child(EXTRACTION).child(VALUE).child(EXPRESSION);

  public static final SelectionPath VALUE_PROVIDERS_ITEMS_DISPLAY_NAME_EXPRESSION_PATH = VALUE_PROVIDERS_DEFINITION.child(ITEMS).child(DISPLAY_NAME).child(VALUE).child(EXPRESSION);
  public static final SelectionPath VALUE_PROVIDERS_ITEMS_VALUE_EXPRESSION_PATH = VALUE_PROVIDERS_DEFINITION.child(ITEMS).child(VALUE).child(EXPRESSION);
  public static final SelectionPath VALUE_PROVIDERS_PARAMETERS_PATH = VALUE_PROVIDERS_PATH.any().child(PARAMETERS);
  public static final SelectionPath VALUE_PROVIDERS_REQUEST = VALUE_PROVIDERS_DEFINITION.child(REQUEST).child(BINDING).any().any().child(VALUE).child(EXPRESSION);

  public static final SelectionPath API_PATH = SelectionPath.DOCUMENT.child(API_SPEC).child(URL);

  //Relative Paths Selectors
  public static final SelectionPath PARAMETERS_SELECTOR_FROM_BODY_REQUEST_PATH = SelectionPath.PARENT.parent().parent().parent().child(RestSdkInputOutputTypesProvider.PARAMETERS_KEY);
  public static final SelectionPath PARAMETERS_SELECTOR_FROM_QUERY_PARAMETER_PATH = SelectionPath.PARENT.parent().parent().parent().child(RestSdkInputOutputTypesProvider.PARAMETERS_KEY);
  public static final SelectionPath RELATIVE_TRIGGER_PARAMETERS_SELECTOR_FROM_BINDING_BODY_PATH = SelectionPath.PARENT.parent().parent().child(RestSdkInputOutputTypesProvider.PARAMETERS_KEY);
  public static final SelectionPath PARAMETERS_SELECTOR_FROM_ITEMS_PATH = SelectionPath.PARENT.parent().parent().child(RestSdkInputOutputTypesProvider.PARAMETERS_KEY);

  public static final SelectionPath PARAMETERS_SELECTOR_FROM_ROOT_SAMPLE_DATA_PATH = SelectionPath.PARENT.parent().parent().parent().parent().parent().child(RestSdkInputOutputTypesProvider.PARAMETERS_KEY);
  public static final SelectionPath RELATIVE_OPERATION_BASE_FROM_REQUEST_PATH = SelectionPath.PARENT.parent().child(BASE).child(OPERATION_ID);
  public static final SelectionPath RELATIVE_OPERATION_BASE_PATH_FROM_REQUEST_PATH = SelectionPath.PARENT.parent().child(BASE).child(PATH);
  public static final SelectionPath RELATIVE_OPERATION_BASE_METHOD_FROM_REQUEST_PATH = SelectionPath.PARENT.parent().child(BASE).child(METHOD);
  public static final SelectionPath RELATIVE_OPERATION_ID_BASE_FROM_BODY_EXPRESSION_PATH = SelectionPath.PARENT.parent().parent().parent().child(BASE).child(OPERATION_ID);
  public static final SelectionPath RELATIVE_METHOD_BASE_FROM_BODY_EXPRESSION_PATH = SelectionPath.PARENT.parent().parent().parent().child(BASE).child(METHOD);
  public static final SelectionPath RELATIVE_PATH_BASE_FROM_BODY_EXPRESSION_PATH = SelectionPath.PARENT.parent().parent().parent().child(BASE).child(PATH);
}
