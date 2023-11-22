/*
 * (C) Copyright IBM Corp. 2021
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.linuxforhealth.fhir.model.r5.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;

/**
 * @see <a href="https://confluence.hl7.org/display/FHIR/FHIR+Maturity+Model">https://confluence.hl7.org/display/FHIR/FHIR+Maturity+Model</a>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Maturity {
    int level();
    StandardsStatus.Value status();
}
