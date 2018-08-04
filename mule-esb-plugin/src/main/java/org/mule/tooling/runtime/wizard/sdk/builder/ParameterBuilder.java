package org.mule.tooling.runtime.wizard.sdk.builder;

import static org.mule.tooling.runtime.wizard.sdk.builder.BuilderUtils.defaultValue;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.DISPLAY_NAME_ANNOTATION;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.OPTIONAL_ANNOTATION;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.PARAMETER_ANNOTATION;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

/**
 * Builder to create parameters
 */
public abstract class ParameterBuilder<T extends ParameterBuilder> {

    public T asOptional() {
        return withAnnotation(AnnotationSpec
                .builder(OPTIONAL_ANNOTATION)
                .build());
    }

    public T asOptional(String defaultValue) {
        return withAnnotation(defaultValue(defaultValue));
    }

    public T displayName(String displayName) {
        return withAnnotation(AnnotationSpec
                .builder(DISPLAY_NAME_ANNOTATION)
                .addMember("value", "$S", displayName)
                .build());
    }

    protected abstract T withAnnotation(AnnotationSpec annotation);

    public static class MethodParameterBuilder extends ParameterBuilder<MethodParameterBuilder> {

        private final String name;
        private final TypeName type;
        private ParameterSpec.Builder builder;

        public static MethodParameterBuilder builder(String name, TypeName type) {
            return new MethodParameterBuilder(name, type);
        }

        private MethodParameterBuilder(String name, TypeName type) {
            this.name = name;
            this.type = type;
            builder = ParameterSpec.builder(type, name);
        }

        @Override
        protected MethodParameterBuilder withAnnotation(AnnotationSpec annotation) {
            builder.addAnnotation(annotation);
            return this;
        }

        public ParameterSpec build() {
            return builder.build();
        }
    }


    public static class FieldParameterBuilder extends ParameterBuilder<FieldParameterBuilder> {

        private final String name;
        private final TypeName type;
        private FieldSpec.Builder builder;

        public FieldParameterBuilder(String name, TypeName type) {
            this.name = name;
            this.type = type;
            builder = FieldSpec.builder(type, name, Modifier.PRIVATE)
                    .addAnnotation(AnnotationSpec.builder(PARAMETER_ANNOTATION).build());
        }

        @Override
        protected FieldParameterBuilder withAnnotation(AnnotationSpec annotation) {
            builder.addAnnotation(annotation);
            return this;
        }

        public FieldSpec build() {
            return builder.build();
        }
    }

}
