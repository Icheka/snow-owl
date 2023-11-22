/*
 * (C) Copyright IBM Corp. 2018,2019
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.linuxforhealth.fhir.model.r5.exception;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.linuxforhealth.fhir.exception.FHIRException;
import org.linuxforhealth.fhir.model.r5.resource.OperationOutcome;
import org.linuxforhealth.fhir.model.r5.util.FHIRUtil;

public class FHIROperationException extends FHIRException {
    private static final long serialVersionUID = 1L;

    private List<OperationOutcome.Issue> issues = new ArrayList<>();

    /**
     * @see Exception#Exception(String)
     */
    public FHIROperationException(String message) {
        super(message);
    }

    /**
     * @see Exception#Exception(String, Throwable)
     */
    public FHIROperationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public OperationOutcome buildOperationOutcome() {
        return FHIRUtil.buildOperationOutcome(getIssues()).toBuilder().id(getUniqueId()).build();
    }

    public List<OperationOutcome.Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<OperationOutcome.Issue> issues) {
        this.issues = issues;
    }
    
    public FHIROperationException withIssue(OperationOutcome.Issue... issues) {
        if (issues != null) {
            for (OperationOutcome.Issue issue : issues) {
                getIssues().add(issue);
            }    
        }
        return this;
    }
    
    public FHIROperationException withIssue(Collection<OperationOutcome.Issue> issues) {
        if (issues != null) {
            getIssues().addAll(issues);
        }
        return this;
    }
}
