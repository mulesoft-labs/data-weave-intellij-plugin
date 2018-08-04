package org.mule.tooling.runtime.wizard.sdk.builder;

import org.mule.tooling.runtime.wizard.sdk.builder.ParameterBuilder.FieldParameterBuilder;

import java.util.ArrayList;
import java.util.List;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

public abstract class TypeBasedBuilder implements JavaType {

    private TypeSpec.Builder typeSpec;
    protected List<FieldParameterBuilder> parameters = new ArrayList<>();

    private TypeSpec build;

    public TypeBasedBuilder(TypeSpec.Builder typeSpec) {
        this.typeSpec = typeSpec;
    }

    public FieldParameterBuilder withParameter(String name, TypeName type) {
        FieldParameterBuilder fieldParameterBuilder = new FieldParameterBuilder(name, type);
        parameters.add(fieldParameterBuilder);
        return fieldParameterBuilder;
    }

    public TypeBasedBuilder withField(FieldSpec field) {
        typeSpec.addField(field);
        return this;
    }

    public TypeBasedBuilder withAnnotation(AnnotationSpec annotationSpec) {
        typeSpec.addAnnotation(annotationSpec);
        return this;
    }

    public TypeSpec.Builder getTypeSpec() {
        return typeSpec;
    }

    public final TypeSpec build() {
        if(build == null) {
            build = doBuild();
        }
        return build;
    }

    @Override
    public TypeSpec getType() {
        return build();
    }

    protected abstract TypeSpec doBuild();

}
