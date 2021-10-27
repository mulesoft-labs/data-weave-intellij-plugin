package org.mule.tooling.restsdk.utils;

import amf.apicontract.client.platform.AMFBaseUnitClient;
import amf.apicontract.client.platform.WebAPIConfiguration;
import amf.apicontract.client.platform.model.domain.EndPoint;
import amf.apicontract.client.platform.model.domain.Operation;
import amf.apicontract.client.platform.model.domain.api.WebApi;
import amf.core.client.platform.AMFParseResult;
import amf.core.client.platform.model.document.BaseUnit;
import amf.core.client.platform.model.document.Document;
import amf.core.client.platform.model.domain.DataNode;
import amf.core.client.platform.model.domain.DomainElement;
import amf.core.client.platform.model.domain.RecursiveShape;
import amf.core.client.platform.model.domain.Shape;
import amf.core.client.scala.model.DataType;
import amf.shapes.client.platform.model.domain.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
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
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RestSdkHelper {

  public static SelectionPath swaggerVersion = SelectionPath.DOCUMENT.child("swagger");
  public static SelectionPath openApiVersion = SelectionPath.DOCUMENT.child("openapi");


  public static boolean isInRestSdkContextFile(PsiFile psiFile) {
    PsiElement context = psiFile.getContext();
    if (context instanceof YAMLScalar) {
      PsiFile containingFile = context.getContainingFile();
      return isRestSdkDescriptorFile(containingFile);
    } else {
      return false;
    }
  }

  public static boolean isRestSdkDescriptorFile(PsiFile containingFile) {
    if (containingFile.getFileType() instanceof YAMLFileType) {
      String text = containingFile.getText();
      return isRestSdkDescriptor(text);
    } else {
      return false;
    }
  }

  public static boolean isRestSdkDescriptor(String text) {
    return text.contains("#% Rest Connector Descriptor 1.0");
  }

  @Nullable
  public static Document parseWebApi(PsiFile restSdkFile) {
    Document result = null;
    final PsiElement select = RestSdkPaths.API_PATH.selectYaml(restSdkFile);
    if (select instanceof YAMLScalar) {
      String apiPath = ((YAMLScalar) select).getTextValue();
      //
      final VirtualFile parent = restSdkFile.getVirtualFile().getParent();
      final VirtualFile child = parent.findFileByRelativePath(apiPath);
      if (child != null) {
        result = parseWebApi(restSdkFile.getProject(), child);
      }
    }
    return result;
  }

  public static Document parseWebApi(Project project, VirtualFile child) {
    final AMFBaseUnitClient client = WebAPIConfiguration.WebAPI().baseUnitClient();

    final AMFParseResult parseResult;
    try {
      parseResult = client.parse(child.getUrl()).get();
      final AMFBaseUnitClient newClient = WebAPIConfiguration.fromSpec(parseResult.sourceSpec()).baseUnitClient();
      final BaseUnit resolvedModel = newClient.transform(parseResult.baseUnit()).baseUnit();
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

    List<EndPoint> endPoints = webApi.endPoints();
    for (EndPoint endPoint : endPoints) {
      List<Operation> operations = endPoint.operations();
      for (Operation operation : operations) {
        if (operation.name().value().equals(id)) {
          return operation;
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
      return new ReferenceType(NameIdentifier.apply(shape.name().value(), Option.empty()), Option.empty(), new ReferenceTypeResolver() {
        WeaveType result = null;

        @Override
        public WeaveType resolveType() {
          if (result == null) {
            result = referenceTypeResolver.resolve(shape.name().value());
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

  public static Operation operationByMethodPath(WebApi webApi, String methodText, String pathText) {
    return
            webApi.endPoints().stream()
                    .filter((endpoint) -> {
                      return endpoint.path().value().equals(pathText);
                    })
                    .flatMap((endpoint) -> {
                      return endpoint.operations().stream().filter((operation) -> operation.method().value().equals(methodText));
                    })
                    .findFirst().orElse(null);
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


