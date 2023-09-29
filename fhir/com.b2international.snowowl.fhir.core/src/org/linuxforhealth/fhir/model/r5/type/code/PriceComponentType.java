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

@System("http://hl7.org/fhir/price-component-type")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class PriceComponentType extends Code {
    /**
     * base price
     * 
     * <p>the amount is the base price used for calculating the total price before applying surcharges, discount or taxes.
     */
    public static final PriceComponentType BASE = PriceComponentType.builder().value(Value.BASE).build();

    /**
     * surcharge
     * 
     * <p>the amount is a surcharge applied on the base price.
     */
    public static final PriceComponentType SURCHARGE = PriceComponentType.builder().value(Value.SURCHARGE).build();

    /**
     * deduction
     * 
     * <p>the amount is a deduction applied on the base price.
     */
    public static final PriceComponentType DEDUCTION = PriceComponentType.builder().value(Value.DEDUCTION).build();

    /**
     * discount
     * 
     * <p>the amount is a discount applied on the base price.
     */
    public static final PriceComponentType DISCOUNT = PriceComponentType.builder().value(Value.DISCOUNT).build();

    /**
     * tax
     * 
     * <p>the amount is the tax component of the total price.
     */
    public static final PriceComponentType TAX = PriceComponentType.builder().value(Value.TAX).build();

    /**
     * informational
     * 
     * <p>the amount is of informational character, it has not been applied in the calculation of the total price.
     */
    public static final PriceComponentType INFORMATIONAL = PriceComponentType.builder().value(Value.INFORMATIONAL).build();

    private volatile int hashCode;

    private PriceComponentType(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this PriceComponentType as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating PriceComponentType objects from a passed enum value.
     */
    public static PriceComponentType of(Value value) {
        switch (value) {
        case BASE:
            return BASE;
        case SURCHARGE:
            return SURCHARGE;
        case DEDUCTION:
            return DEDUCTION;
        case DISCOUNT:
            return DISCOUNT;
        case TAX:
            return TAX;
        case INFORMATIONAL:
            return INFORMATIONAL;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating PriceComponentType objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static PriceComponentType of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating PriceComponentType objects from a passed string value.
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
     * Inherited factory method for creating PriceComponentType objects from a passed string value.
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
        PriceComponentType other = (PriceComponentType) obj;
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
         *     An enum constant for PriceComponentType
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public PriceComponentType build() {
            PriceComponentType priceComponentType = new PriceComponentType(this);
            if (validating) {
                validate(priceComponentType);
            }
            return priceComponentType;
        }

        protected void validate(PriceComponentType priceComponentType) {
            super.validate(priceComponentType);
        }

        protected Builder from(PriceComponentType priceComponentType) {
            super.from(priceComponentType);
            return this;
        }
    }

    public enum Value {
        /**
         * base price
         * 
         * <p>the amount is the base price used for calculating the total price before applying surcharges, discount or taxes.
         */
        BASE("base"),

        /**
         * surcharge
         * 
         * <p>the amount is a surcharge applied on the base price.
         */
        SURCHARGE("surcharge"),

        /**
         * deduction
         * 
         * <p>the amount is a deduction applied on the base price.
         */
        DEDUCTION("deduction"),

        /**
         * discount
         * 
         * <p>the amount is a discount applied on the base price.
         */
        DISCOUNT("discount"),

        /**
         * tax
         * 
         * <p>the amount is the tax component of the total price.
         */
        TAX("tax"),

        /**
         * informational
         * 
         * <p>the amount is of informational character, it has not been applied in the calculation of the total price.
         */
        INFORMATIONAL("informational");

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
         * Factory method for creating PriceComponentType.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding PriceComponentType.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "base":
                return BASE;
            case "surcharge":
                return SURCHARGE;
            case "deduction":
                return DEDUCTION;
            case "discount":
                return DISCOUNT;
            case "tax":
                return TAX;
            case "informational":
                return INFORMATIONAL;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
