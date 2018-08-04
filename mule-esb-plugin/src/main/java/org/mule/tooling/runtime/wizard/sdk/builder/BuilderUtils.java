package org.mule.tooling.runtime.wizard.sdk.builder;

import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.MEDIA_TYPE_ANNOTATION;
import static org.mule.tooling.runtime.wizard.sdk.builder.ExtensionClassName.OPTIONAL_ANNOTATION;

import com.squareup.javapoet.AnnotationSpec;

public class BuilderUtils {

    private BuilderUtils() {

    }

    public static  AnnotationSpec mediaTypeOf(String mediaType) {
        return AnnotationSpec
                .builder(MEDIA_TYPE_ANNOTATION)
                .addMember("value", "$S", mediaType)
                .build();
    }

    public static AnnotationSpec defaultValue(String defaultValue) {
        return AnnotationSpec
                .builder(OPTIONAL_ANNOTATION)
                .addMember("defaultValue", "$S", defaultValue)
                .build();
    }
}
