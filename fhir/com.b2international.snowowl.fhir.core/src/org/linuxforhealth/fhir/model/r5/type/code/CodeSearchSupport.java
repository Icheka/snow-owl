/*
 * (C) Copyright IBM Corp. 2019, 2022
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.linuxforhealth.fhir.model.r5.type.code;

import org.linuxforhealth.fhir.model.annotation.System;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.String;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Generated;

@System("http://hl7.org/fhir/code-search-support")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class CodeSearchSupport extends Code {
    /**
     * In Compose
     * 
     * <p>The search for code on ValueSet returns ValueSet resources where the code is included in the extensional definition 
     * of the ValueSet.
     */
    public static final CodeSearchSupport IN_COMPOSE = CodeSearchSupport.builder().value(Value.IN_COMPOSE).build();

    /**
     * In Expansion
     * 
     * <p>The search for code on ValueSet returns ValueSet resources where the code is contained in the ValueSet expansion.
     */
    public static final CodeSearchSupport IN_EXPANSION = CodeSearchSupport.builder().value(Value.IN_EXPANSION).build();

    /**
     * In Compose Or Expansion
     * 
     * <p>The search for code on ValueSet returns ValueSet resources where the code is included in the extensional definition 
     * or contained in the ValueSet expansion.
     */
    public static final CodeSearchSupport IN_COMPOSE_OR_EXPANSION = CodeSearchSupport.builder().value(Value.IN_COMPOSE_OR_EXPANSION).build();

    private volatile int hashCode;

    private CodeSearchSupport(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this CodeSearchSupport as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating CodeSearchSupport objects from a passed enum value.
     */
    public static CodeSearchSupport of(Value value) {
        switch (value) {
        case IN_COMPOSE:
            return IN_COMPOSE;
        case IN_EXPANSION:
            return IN_EXPANSION;
        case IN_COMPOSE_OR_EXPANSION:
            return IN_COMPOSE_OR_EXPANSION;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating CodeSearchSupport objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static CodeSearchSupport of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating CodeSearchSupport objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static String string(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating CodeSearchSupport objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static Code code(java.lang.String value) {
        return of(Value.from(value));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CodeSearchSupport other = (CodeSearchSupport) obj;
        return Objects.equals(id, other.id) && Objects.equals(extension, other.extension) && Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = Objects.hash(id, extension, value);
            hashCode = result;
        }
        return result;
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends Code.Builder {
        private Builder() {
            super();
        }

        @Override
        public Builder id(java.lang.String id) {
            return (Builder) super.id(id);
        }

        @Override
        public Builder extension(Extension... extension) {
            return (Builder) super.extension(extension);
        }

        @Override
        public Builder extension(Collection<Extension> extension) {
            return (Builder) super.extension(extension);
        }

        @Override
        public Builder value(java.lang.String value) {
            return (value != null) ? (Builder) super.value(Value.from(value).value()) : this;
        }

        /**
         * Primitive value for code
         * 
         * @param value
         *     An enum constant for CodeSearchSupport
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public CodeSearchSupport build() {
            CodeSearchSupport codeSearchSupport = new CodeSearchSupport(this);
            if (validating) {
                validate(codeSearchSupport);
            }
            return codeSearchSupport;
        }

        protected void validate(CodeSearchSupport codeSearchSupport) {
            super.validate(codeSearchSupport);
        }

        protected Builder from(CodeSearchSupport codeSearchSupport) {
            super.from(codeSearchSupport);
            return this;
        }
    }

    public enum Value {
        /**
         * In Compose
         * 
         * <p>The search for code on ValueSet returns ValueSet resources where the code is included in the extensional definition 
         * of the ValueSet.
         */
        IN_COMPOSE("in-compose"),

        /**
         * In Expansion
         * 
         * <p>The search for code on ValueSet returns ValueSet resources where the code is contained in the ValueSet expansion.
         */
        IN_EXPANSION("in-expansion"),

        /**
         * In Compose Or Expansion
         * 
         * <p>The search for code on ValueSet returns ValueSet resources where the code is included in the extensional definition 
         * or contained in the ValueSet expansion.
         */
        IN_COMPOSE_OR_EXPANSION("in-compose-or-expansion");

        private final java.lang.String value;

        Value(java.lang.String value) {
            this.value = value;
        }

        /**
         * @return
         *     The java.lang.String value of the code represented by this enum
         */
        public java.lang.String value() {
            return value;
        }

        /**
         * Factory method for creating CodeSearchSupport.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding CodeSearchSupport.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "in-compose":
                return IN_COMPOSE;
            case "in-expansion":
                return IN_EXPANSION;
            case "in-compose-or-expansion":
                return IN_COMPOSE_OR_EXPANSION;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
