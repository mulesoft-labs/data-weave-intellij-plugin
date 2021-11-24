package org.mule.tooling.restsdk.utils;

import com.intellij.psi.PsiFile;
import org.everit.json.schema.*;
import org.everit.json.schema.loader.SchemaLoader;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.mule.tooling.lang.dw.util.ScalaUtils;
import org.mule.weave.v2.parser.ast.QName;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;
import org.mule.weave.v2.ts.*;
import scala.Option;

import java.util.*;

public class JsonSchemaConverter {

  public static WeaveType toWeaveType(PsiFile schemaContent) {
    String schemaContentText = schemaContent.getText();
    return toWeaveType(schemaContentText);

  }

  @NotNull
  public static WeaveType toWeaveType(String schemaContentText) {
    final JSONObject rawSchema = new JSONObject(new JSONTokener(schemaContentText));
    final SchemaLoader.SchemaLoaderBuilder schemaLoaderBuilder = new SchemaLoader.SchemaLoaderBuilder();
    schemaLoaderBuilder.schemaJson(rawSchema);
    final SchemaLoader schemaLoader = schemaLoaderBuilder.build();
    Schema build = schemaLoader.load().build();
    return toWeaveType(build, new HashMap<>());
  }

  public static WeaveType toWeaveType(Schema build) {
    return toWeaveType(build, new HashMap<>());
  }
  private static WeaveType toWeaveType(Schema build, Map<String, WeaveType> references) {
    final WeaveType weaveType = mapToWeaveType(build, references);
    weaveType.withDocumentation(Option.apply(build.getDescription()));
    weaveType.label(Option.apply(build.getTitle()));
    return weaveType;
  }

  @NotNull
  private static WeaveType mapToWeaveType(Schema build, Map<String, WeaveType> references) {
    if (build instanceof ObjectSchema) {
      final Set<Map.Entry<String, Schema>> properties = ((ObjectSchema) build).getPropertySchemas().entrySet();
      final KeyValuePairType[] kvps = properties.stream().map((prop) -> {
        final QName qName = new QName(prop.getKey(), Option.empty());
        final KeyType keyType = new KeyType(new NameType(Option.apply(qName)), ScalaUtils.toSeq());
        return new KeyValuePairType(keyType, toWeaveType(prop.getValue(), references), true, false);
      }).toArray(KeyValuePairType[]::new);
      return new ObjectType(ScalaUtils.toSeq(kvps), false, false);
    } else if (build instanceof ArraySchema) {
      ArraySchema arraySchema = (ArraySchema) build;
      List<Schema> itemSchemas = arraySchema.getItemSchemas();

      if (itemSchemas != null && !itemSchemas.isEmpty()) {
        final WeaveType[] weaveTypes = itemSchemas.stream().map((schema) -> {
          return toWeaveType(schema, references);
        }).toArray(WeaveType[]::new);
        final WeaveType weaveType = TypeHelper$.MODULE$.unify(ScalaUtils.toSeq(weaveTypes));
        return new ArrayType(weaveType);
      } else {
        final Schema arrayType = arraySchema.getAllItemSchema();
        if (arrayType != null) {
          return new ArrayType(toWeaveType(arrayType, references));
        } else {
          return new ArrayType(new AnyType());
        }
      }
    } else if (build instanceof EnumSchema) {
      List<Object> possibleValuesAsList = ((EnumSchema) build).getPossibleValuesAsList();
      StringType[] options = possibleValuesAsList.stream().map((v) -> new StringType(Option.apply(v.toString()))).toArray(StringType[]::new);
      return new UnionType(ScalaUtils.toSeq(options));
    } else if (build instanceof StringSchema) {
      return new StringType(Option.empty());
    } else if (build instanceof NumberSchema) {
      return new NumberType(Option.empty());
    } else if (build instanceof BooleanSchema) {
      return new BooleanType(Option.empty(), VariableConstraints.emptyConstraints());
    } else if (build instanceof NullSchema) {
      return new NullType();
    } else if (build instanceof ReferenceSchema) {
      String id = Optional.ofNullable(build.getId()).orElse(Optional.ofNullable(build.getSchemaLocation()).orElse(UUID.randomUUID().toString()));
      NameIdentifier nameIdentifier = NameIdentifier.apply(id, Option.empty());
      return new ReferenceType(nameIdentifier, Option.empty(), new ReferenceTypeResolver() {
        @Override
        public WeaveType resolveType() {
          if (references.containsKey(id)) {
            return references.get(id);
          } else {
            WeaveType weaveType = toWeaveType(((ReferenceSchema) build).getReferredSchema(), references);
            references.put(id, weaveType);
            return weaveType;
          }
        }
      });
    } else if (build instanceof CombinedSchema) {
      final CombinedSchema combinedSchema = (CombinedSchema) build;
      final Collection<Schema> subschemas = combinedSchema.getSubschemas();
      WeaveType[] weaveTypes = subschemas.stream().map((s) -> toWeaveType(s, references)).toArray(WeaveType[]::new);
      if (combinedSchema.getCriterion() == CombinedSchema.ANY_CRITERION
              || combinedSchema.getCriterion() == CombinedSchema.ONE_CRITERION) {
        return new UnionType(ScalaUtils.toSeq(weaveTypes));
      } else if (combinedSchema.getCriterion() == CombinedSchema.ALL_CRITERION) {
        return new IntersectionType(ScalaUtils.toSeq(weaveTypes));
      }

    } else if (build instanceof EmptySchema) {
      return new AnyType();
    }
    return new AnyType();
  }
}
