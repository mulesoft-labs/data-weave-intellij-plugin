package org.mule.tooling.runtime.wizard.sdk.builder;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;

public interface JavaType {

    String getName();

    String getPackage();

    TypeSpec getType();

    static JavaType create(TypeSpec typeSpec, String javaPackage) {
        return new DefaultExtensionJavaType(typeSpec, javaPackage);
    }

    default ClassName getClassName() {
        return ClassName.get(getPackage(), getName());
    }

    class DefaultExtensionJavaType implements JavaType {

        private final TypeSpec typeSpec;
        private final String javaPackage;

        DefaultExtensionJavaType(TypeSpec typeSpec, String javaPackage) {

            this.typeSpec = typeSpec;
            this.javaPackage = javaPackage;
        }

        @Override
        public String getName() {
            return typeSpec.name;
        }

        @Override
        public String getPackage() {
            return javaPackage;
        }

        @Override
        public TypeSpec getType() {
            return typeSpec;
        }
    }
}
