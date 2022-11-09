package org.mule.tooling.restsdk.utils;

import amf.apicontract.client.platform.AMFBaseUnitClient;
import amf.apicontract.client.platform.AMFConfiguration;
import amf.apicontract.client.platform.WebAPIConfiguration;
import amf.apicontract.client.platform.model.domain.EndPoint;
import amf.apicontract.client.platform.model.domain.Operation;
import amf.apicontract.client.platform.model.domain.api.WebApi;
import amf.apicontract.client.platform.model.domain.security.HttpSettings;
import amf.apicontract.client.platform.model.domain.security.ParametrizedSecurityScheme;
import amf.apicontract.client.platform.model.domain.security.SecurityScheme;
import amf.core.client.platform.AMFParseResult;
import amf.core.client.platform.model.StrField;
import amf.core.client.platform.model.document.BaseUnit;
import amf.core.client.platform.model.document.Document;
import amf.core.client.platform.model.domain.DataNode;
import amf.core.client.platform.model.domain.DomainElement;
import amf.core.client.platform.model.domain.RecursiveShape;
import amf.core.client.platform.model.domain.Shape;
import amf.core.client.platform.resource.ResourceLoader;
import amf.core.client.scala.model.DataType;
import amf.shapes.client.platform.model.domain.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ObjectUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLFileType;
import org.jetbrains.yaml.psi.YAMLScalar;
import org.mule.tooling.lang.dw.util.ScalaUtils;
import org.mule.weave.v2.parser.ast.QName;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;
import org.mule.weave.v2.ts.*;
import scala.Option;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class RestSdkHelper {
  public static final Logger LOGGER = Logger.getInstance(RestSdkHelper.class);

  public static SelectionPath swaggerVersion = SelectionPath.DOCUMENT.child("swagger");
  public static SelectionPath openApiVersion = SelectionPath.DOCUMENT.child("openapi");

  final private static ConcurrentHashMap<Object, Object> dummyMapForSynchronizingPerKey = new ConcurrentHashMap<>();

  public static boolean isInRestSdkContextFile(@Nullable PsiFile psiFile) {
    if (psiFile == null) {
      return false;
    }
    PsiElement context = psiFile.getContext();
    if (context instanceof YAMLScalar) {
      PsiFile containingFile = context.getContainingFile();
      return isRestSdkDescriptorFile(containingFile);
    } else {
      return false;
    }
  }

  @Contract(pure = true)
  public static boolean isRestSdkDescriptorFile(@NotNull PsiFile psiFile) {
    if (psiFile.getFileType() == YAMLFileType.YML) {
      var comment = PsiTreeUtil.getChildOfType(psiFile, PsiComment.class);
      return comment != null && isRestSdkDescriptor(comment.getText());
    } else {
      return false;
    }
  }

  public static boolean isRestSdkDescriptor(String text) {
    return text.contains("#% Rest Connector Descriptor 1.0");
  }

  @Nullable
  public static Document parseWebApi(PsiFile restSdkFile) {
    return doParseWebApi(restSdkFile);
  }

  private static Document doParseWebApi(PsiFile restSdkFile) {
    Document[] result = new Document[1];
    dummyMapForSynchronizingPerKey.computeIfAbsent(restSdkFile, z ->  {
      final var virtualFile = apiVirtualFile(restSdkFile);
      if (virtualFile != null) {
        result[0] = CachedValuesManager.getCachedValue(restSdkFile, () ->
                CachedValueProvider.Result.create(parseWebApi(virtualFile), virtualFile));
      }
      // We don't store the parsed API in this map, we just use the per-key blocking capability of ConcurrentHashMap.
      return null;
    });
    return result[0];
  }

  public static PsiFile apiFile(PsiFile restSdkFile) {
    return ObjectUtils.doIfNotNull(apiVirtualFile(restSdkFile), vf -> PsiManager.getInstance(restSdkFile.getProject()).findFile(vf));
  }

  public static VirtualFile apiVirtualFile(PsiFile restSdkFile) {
    final PsiElement select = RestSdkPaths.API_PATH.selectYaml(restSdkFile);
    if (!(select instanceof YAMLScalar))
      return null;
    final String apiPath = ((YAMLScalar) select).getTextValue();
    final VirtualFile parent = restSdkFile.getOriginalFile().getVirtualFile().getParent();
    return parent.findFileByRelativePath(apiPath);
  }

  @Nullable
  private static Document parseWebApi(VirtualFile child) {
    long before = System.nanoTime();
    AMFConfiguration amfConfiguration = WebAPIConfiguration.WebAPI();
    ResourceLoader rl = new IntellijResourceLoader(amfConfiguration.getExecutionContext());
    final AMFBaseUnitClient client = amfConfiguration.withResourceLoader(rl).baseUnitClient();
    final AMFParseResult parseResult;
    try {
      parseResult = client.parse(child.getUrl()).get();
      final AMFBaseUnitClient newClient = WebAPIConfiguration.fromSpec(parseResult.sourceSpec()).baseUnitClient();
      final BaseUnit resolvedModel = newClient.transform(parseResult.baseUnit()).baseUnit();
      LOGGER.debug("Parsed " + child + " in " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - before) + "ms");
      return (Document) resolvedModel;
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static @Nullable Operation operationById(@Nullable WebApi webApi, String id) {
    if (webApi == null) {
      return null;
    }

    final List<EndPoint> endPoints = webApi.endPoints();
    for (EndPoint endPoint : endPoints) {
      final List<Operation> operations = endPoint.operations();
      for (Operation operation : operations) {
        final StrField name = operation.name();
        if (name != null) {
          final String value = name.value();
          if (value != null && value.equals(id)) {
            return operation;
          }
        }
      }
    }
    return null;
  }

  public static WeaveType toWeaveType(Shape shape, Document webApi) {
    return toWeaveType(shape, new WeaveReferenceTypeResolver(webApi));
  }

  public static WeaveType toWeaveType(Shape shape, WeaveReferenceTypeResolver referenceTypeResolver) {
    if (shape instanceof NodeShape) {
      final KeyValuePairType[] keyValuePairTypes = ((NodeShape) shape).properties().stream().map((property) -> {
        final QName propertyName = new QName(property.name().value(), Option.empty());
        return new KeyValuePairType(new KeyType(new NameType(Option.apply(propertyName)), ScalaUtils.toSeq()), toWeaveType(property.range(), referenceTypeResolver), property.minCount().value() == 0, property.maxCount().value() > 1);
      }).toArray(KeyValuePairType[]::new);
      return new ObjectType(ScalaUtils.toSeq(keyValuePairTypes), true, true);
    } else if (shape instanceof ArrayShape) {
      return new ArrayType(toWeaveType(((ArrayShape) shape).items(), referenceTypeResolver));
    } else if (shape instanceof ScalarShape) {
      final String value = ((ScalarShape) shape).dataType().value();
      final List<DataNode> values = shape.values();
      if (DataType.String().equals(value)) {
        if (!values.isEmpty()) {
          final WeaveType[] stringValues = values.stream()
                  .map((node) -> new StringType(Option.apply(node.name().value())))
                  .toArray(WeaveType[]::new);
          return new UnionType(ScalaUtils.toSeq(stringValues));
        } else {
          return new StringType(Option.empty());
        }
      } else if (
              DataType.Number().equals(value)
                      || DataType.Decimal().equals(value)
                      || DataType.Long().equals(value)
                      || DataType.Float().equals(value)
      ) {
        if (!values.isEmpty()) {
          final WeaveType[] numberValues = values.stream()
                  .map((node) -> new NumberType(Option.apply(node.name().value())))
                  .toArray(WeaveType[]::new);
          return new UnionType(ScalaUtils.toSeq(numberValues));
        } else {
          return new NumberType(Option.empty());
        }
      } else if (DataType.DateTime().equals(value)) {
        return new DateTimeType();
      } else if (DataType.Date().equals(value)) {
        return new LocalDateType();
      } else if (DataType.DateTimeOnly().equals(value)) {
        return new LocalDateTimeType();
      } else if (DataType.Any().equals(value)) {
        return new AnyType();
      } else if (DataType.Nil().equals(value)) {
        return new NullType();
      } else if (DataType.Link().equals(value)) {
        return new StringType(Option.empty());
      } else if (DataType.Boolean().equals(value)) {
        return new BooleanType(Option.empty(), VariableConstraints.emptyConstraints());
      } else if (DataType.Password().equals(value)) {
        return new StringType(Option.empty());
      } else if (DataType.File().equals(value) || DataType.Byte().equals(value)) {
        return new BinaryType();
      } else {
        return new AnyType();
      }
    } else if (shape instanceof NilShape) {
      return new NullType();
    } else if (shape instanceof RecursiveShape) {
      final String value = shape.name().value();
      return new ReferenceType(NameIdentifier.apply(value, Option.empty()), Option.empty(), new ReferenceTypeResolver() {
        WeaveType result = null;

        @Override
        public WeaveType resolveType() {
          if (result == null) {
            result = referenceTypeResolver.resolve(value);
          }
          if (result == null) {
            //This shouldn't happen but just in case
            result = new AnyType();
          }
          return result;
        }
      });
    } else if (shape instanceof UnionShape) {
      final List<Shape> shapes = ((UnionShape) shape).anyOf();
      final WeaveType[] weaveTypes = shapes.stream()
              .map((s) -> toWeaveType(s, referenceTypeResolver))
              .toArray(WeaveType[]::new);
      return new UnionType(ScalaUtils.toSeq(weaveTypes));
    } else {
      return new AnyType();
    }
  }

  public static EndPoint endpointByPath(@Nullable WebApi webApi, String methodText, String pathText) {
    if (webApi == null) {
      return null;
    }
    return webApi.endPoints().stream()
            .filter((endpoint) -> {
              return endpoint.path().value().equals(pathText);
            }).findFirst().orElse(null);
  }

  @Nullable
  public static Operation operationByMethodPath(@Nullable WebApi webApi, String methodText, String pathText) {
    if (webApi == null) {
      return null;
    }
    Stream<EndPoint> endPointStream = webApi.endPoints().stream()
            .filter((endpoint) -> {
              return endpoint.path().value().equals(pathText);
            });
    return
            endPointStream
                    .flatMap((endpoint) -> {
                      return endpoint.operations().stream().filter((operation) -> operation.method().value().equals(methodText));
                    })
                    .findFirst().orElse(null);
  }

  public static EndPoint endpointByPath(@Nullable WebApi webApi, String pathText) {
    if (webApi == null) {
      return null;
    }
    return
            webApi.endPoints().stream()
                    .filter((endpoint) -> {
                      return endpoint.path().value().equals(pathText);
                    })
                    .findFirst().orElse(null);
  }

  /** Returns a stream with every security configuration in use.
   *
   * @param webApi the web API
   * @return a stream of {@link ParametrizedSecurityScheme} objects
   */
  public static @NotNull Stream<ParametrizedSecurityScheme> getSecuritySchemes(@NotNull WebApi webApi) {
    return webApi.endPoints().stream().flatMap(e -> e.operations().stream())
            .flatMap(o -> o.security().stream().flatMap(r -> r.schemes().stream()))
            .distinct();
  }

  /** Returns a stream with the methods supported by an endpoint.
   *
   * @param endPoint the endpoint
   * @return a stream of method names
   */
  @Contract(pure = true)
  public static @NotNull Stream<String> getEndpointMethods(@NotNull EndPoint endPoint) {
    return endPoint.operations().stream().map(o -> o.method().value());
  }

  /** Gets the Rest SDK "kind" value for an API security scheme.
   */
  @Contract(pure = true)
  public static @Nullable String securitySchemeToKind(@NotNull SecurityScheme scheme) {
    String type = scheme.type().value().toLowerCase(Locale.ROOT);
    switch (type) {
      case "http":
        String httpScheme = getHttpSettingsScheme(scheme);
        return "bearer".equals(httpScheme) || "basic".equals(httpScheme) ? httpScheme : null;
      case "basic authentication":
        return "basic";
      case "oauth 2.0":
        return "oauth2";
      case "api key":
      case "apikey":
      case "x-amf-apikey":
      case "pass through":
        return "apiKey";
      case "digest authentication":
        return "digest";
      default:
        return type.startsWith("x-") ? "custom" : null;
    }
  }

  private static String getHttpSettingsScheme(@NotNull SecurityScheme securityScheme) {
    return ObjectUtils.doIfCast(securityScheme.settings(), HttpSettings.class, s -> s.scheme().value());
  }

  static class WeaveReferenceTypeResolver {

    private Map<String, WeaveType> types = new HashMap<>();
    private Document webApi;

    public WeaveReferenceTypeResolver(Document webApi) {
      this.webApi = webApi;
    }

    public WeaveType resolve(String name) {
      final List<DomainElement> declares = this.webApi.declares();
      WeaveType weaveType = types.get(name);
      if (weaveType == null) {
        for (DomainElement declare : declares) {
          if (declare instanceof Shape && name.equals(((Shape) declare).name().value())) {
            weaveType = RestSdkHelper.toWeaveType((Shape) declare, this);
            types.put(name, weaveType);
          }
        }
      }
      return weaveType;
    }
  }
}


