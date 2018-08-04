package org.mule.tooling.runtime.wizard.sdk.builder;

import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static javax.lang.model.element.Modifier.PUBLIC;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

/**
 * Builder to write generic methods
 */
public class MethodBuilder<T extends MethodBuilder> {

    final String name;
    protected final MethodSpec.Builder builder;

    public MethodBuilder(String name) {
        this.name = name;
        builder = methodBuilder(name).addModifiers(PUBLIC);
    }

    public T returns(TypeName type) {
        builder.returns(type);
        return (T) this;
    }

    public T withParameter(String name, ClassName type) {
        builder.addParameter(type, name);
        return (T) this;
    }

    public T withParameter(ParameterBuilder.MethodParameterBuilder methodParameterBuilder) {
        builder.addParameter(methodParameterBuilder.build());
        return (T) this;
    }

    public T withStatement(CodeBlock codeBlock) {
        builder.addStatement(codeBlock);
        return (T) this;
    }

    public MethodSpec build() {
        return builder.build();
    }
}
