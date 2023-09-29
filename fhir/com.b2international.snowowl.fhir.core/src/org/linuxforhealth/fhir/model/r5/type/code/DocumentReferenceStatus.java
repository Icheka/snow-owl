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

@System("http://hl7.org/fhir/document-reference-status")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class DocumentReferenceStatus extends Code {
    /**
     * Current
     * 
     * <p>This is the current reference for this document.
     */
    public static final DocumentReferenceStatus CURRENT = DocumentReferenceStatus.builder().value(Value.CURRENT).build();

    /**
     * Superseded
     * 
     * <p>This reference has been superseded by another reference.
     */
    public static final DocumentReferenceStatus SUPERSEDED = DocumentReferenceStatus.builder().value(Value.SUPERSEDED).build();

    /**
     * Entered in Error
     * 
     * <p>This reference was created in error.
     */
    public static final DocumentReferenceStatus ENTERED_IN_ERROR = DocumentReferenceStatus.builder().value(Value.ENTERED_IN_ERROR).build();

    private volatile int hashCode;

    private DocumentReferenceStatus(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this DocumentReferenceStatus as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating DocumentReferenceStatus objects from a passed enum value.
     */
    public static DocumentReferenceStatus of(Value value) {
        switch (value) {
        case CURRENT:
            return CURRENT;
        case SUPERSEDED:
            return SUPERSEDED;
        case ENTERED_IN_ERROR:
            return ENTERED_IN_ERROR;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating DocumentReferenceStatus objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static DocumentReferenceStatus of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating DocumentReferenceStatus objects from a passed string value.
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
     * Inherited factory method for creating DocumentReferenceStatus objects from a passed string value.
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
        DocumentReferenceStatus other = (DocumentReferenceStatus) obj;
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
         *     An enum constant for DocumentReferenceStatus
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public DocumentReferenceStatus build() {
            DocumentReferenceStatus documentReferenceStatus = new DocumentReferenceStatus(this);
            if (validating) {
                validate(documentReferenceStatus);
            }
            return documentReferenceStatus;
        }

        protected void validate(DocumentReferenceStatus documentReferenceStatus) {
            super.validate(documentReferenceStatus);
        }

        protected Builder from(DocumentReferenceStatus documentReferenceStatus) {
            super.from(documentReferenceStatus);
            return this;
        }
    }

    public enum Value {
        /**
         * Current
         * 
         * <p>This is the current reference for this document.
         */
        CURRENT("current"),

        /**
         * Superseded
         * 
         * <p>This reference has been superseded by another reference.
         */
        SUPERSEDED("superseded"),

        /**
         * Entered in Error
         * 
         * <p>This reference was created in error.
         */
        ENTERED_IN_ERROR("entered-in-error");

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
         * Factory method for creating DocumentReferenceStatus.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding DocumentReferenceStatus.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "current":
                return CURRENT;
            case "superseded":
                return SUPERSEDED;
            case "entered-in-error":
                return ENTERED_IN_ERROR;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
