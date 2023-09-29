/*
 * (C) Copyright IBM Corp. 2019, 2022
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.linuxforhealth.fhir.model.r4b.type.code;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Generated;

import org.linuxforhealth.fhir.model.annotation.System;
import org.linuxforhealth.fhir.model.r4b.type.Code;
import org.linuxforhealth.fhir.model.r4b.type.Extension;
import org.linuxforhealth.fhir.model.r4b.type.String;

@System("http://hl7.org/fhir/map-target-list-mode")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class StructureMapTargetListMode extends Code {
    /**
     * First
     * 
     * <p>when the target list is being assembled, the items for this rule go first. If more than one rule defines a first 
     * item (for a given instance of mapping) then this is an error.
     */
    public static final StructureMapTargetListMode FIRST = StructureMapTargetListMode.builder().value(Value.FIRST).build();

    /**
     * Share
     * 
     * <p>the target instance is shared with the target instances generated by another rule (up to the first common n items, 
     * then create new ones).
     */
    public static final StructureMapTargetListMode SHARE = StructureMapTargetListMode.builder().value(Value.SHARE).build();

    /**
     * Last
     * 
     * <p>when the target list is being assembled, the items for this rule go last. If more than one rule defines a last item 
     * (for a given instance of mapping) then this is an error.
     */
    public static final StructureMapTargetListMode LAST = StructureMapTargetListMode.builder().value(Value.LAST).build();

    /**
     * Collate
     * 
     * <p>re-use the first item in the list, and keep adding content to it.
     */
    public static final StructureMapTargetListMode COLLATE = StructureMapTargetListMode.builder().value(Value.COLLATE).build();

    private volatile int hashCode;

    private StructureMapTargetListMode(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this StructureMapTargetListMode as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating StructureMapTargetListMode objects from a passed enum value.
     */
    public static StructureMapTargetListMode of(Value value) {
        switch (value) {
        case FIRST:
            return FIRST;
        case SHARE:
            return SHARE;
        case LAST:
            return LAST;
        case COLLATE:
            return COLLATE;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating StructureMapTargetListMode objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static StructureMapTargetListMode of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating StructureMapTargetListMode objects from a passed string value.
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
     * Inherited factory method for creating StructureMapTargetListMode objects from a passed string value.
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
        StructureMapTargetListMode other = (StructureMapTargetListMode) obj;
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
         *     An enum constant for StructureMapTargetListMode
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public StructureMapTargetListMode build() {
            StructureMapTargetListMode structureMapTargetListMode = new StructureMapTargetListMode(this);
            if (validating) {
                validate(structureMapTargetListMode);
            }
            return structureMapTargetListMode;
        }

        protected void validate(StructureMapTargetListMode structureMapTargetListMode) {
            super.validate(structureMapTargetListMode);
        }

        protected Builder from(StructureMapTargetListMode structureMapTargetListMode) {
            super.from(structureMapTargetListMode);
            return this;
        }
    }

    public enum Value {
        /**
         * First
         * 
         * <p>when the target list is being assembled, the items for this rule go first. If more than one rule defines a first 
         * item (for a given instance of mapping) then this is an error.
         */
        FIRST("first"),

        /**
         * Share
         * 
         * <p>the target instance is shared with the target instances generated by another rule (up to the first common n items, 
         * then create new ones).
         */
        SHARE("share"),

        /**
         * Last
         * 
         * <p>when the target list is being assembled, the items for this rule go last. If more than one rule defines a last item 
         * (for a given instance of mapping) then this is an error.
         */
        LAST("last"),

        /**
         * Collate
         * 
         * <p>re-use the first item in the list, and keep adding content to it.
         */
        COLLATE("collate");

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
         * Factory method for creating StructureMapTargetListMode.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding StructureMapTargetListMode.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "first":
                return FIRST;
            case "share":
                return SHARE;
            case "last":
                return LAST;
            case "collate":
                return COLLATE;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
