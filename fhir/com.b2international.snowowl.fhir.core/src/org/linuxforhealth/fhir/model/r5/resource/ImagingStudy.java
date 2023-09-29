/*
 * (C) Copyright IBM Corp. 2019, 2022
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.linuxforhealth.fhir.model.r5.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.annotation.Generated;

import org.linuxforhealth.fhir.model.r5.annotation.Binding;
import org.linuxforhealth.fhir.model.r5.annotation.Constraint;
import org.linuxforhealth.fhir.model.r5.annotation.Maturity;
import org.linuxforhealth.fhir.model.annotation.ReferenceTarget;
import org.linuxforhealth.fhir.model.annotation.Required;
import org.linuxforhealth.fhir.model.annotation.Summary;
import org.linuxforhealth.fhir.model.r5.type.Annotation;
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.CodeableReference;
import org.linuxforhealth.fhir.model.r5.type.Coding;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Id;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.UnsignedInt;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.ImagingStudyStatus;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * Representation of the content produced in a DICOM imaging study. A study comprises a set of series, each of which 
 * includes a set of Service-Object Pair Instances (SOP Instances - images or other data) acquired or produced in a 
 * common context. A series is of only one modality (e.g. X-ray, CT, MR, ultrasound), but a study may have multiple 
 * series of different modalities.
 * 
 * <p>Maturity level: FMM4 (Trial Use)
 */
@Maturity(
    level = 4,
    status = StandardsStatus.Value.TRIAL_USE
)
@Constraint(
    id = "imagingStudy-0",
    level = "Warning",
    location = "(base)",
    description = "SHALL, if possible, contain a code from value set http://dicom.nema.org/medical/dicom/current/output/chtml/part16/sect_CID_33.html",
    expression = "modality.exists() implies (modality.all(memberOf('http://dicom.nema.org/medical/dicom/current/output/chtml/part16/sect_CID_33.html', 'extensible')))",
    source = "http://hl7.org/fhir/StructureDefinition/ImagingStudy",
    generated = true
)
@Constraint(
    id = "imagingStudy-1",
    level = "Warning",
    location = "(base)",
    description = "SHOULD contain a code from value set http://loinc.org/vs/loinc-rsna-radiology-playbook",
    expression = "procedure.exists() implies (procedure.all(memberOf('http://loinc.org/vs/loinc-rsna-radiology-playbook', 'preferred')))",
    source = "http://hl7.org/fhir/StructureDefinition/ImagingStudy",
    generated = true
)
@Constraint(
    id = "imagingStudy-2",
    level = "Warning",
    location = "series.modality",
    description = "SHALL, if possible, contain a code from value set http://dicom.nema.org/medical/dicom/current/output/chtml/part16/sect_CID_33.html",
    expression = "$this.memberOf('http://dicom.nema.org/medical/dicom/current/output/chtml/part16/sect_CID_33.html', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/ImagingStudy",
    generated = true
)
@Constraint(
    id = "imagingStudy-3",
    level = "Warning",
    location = "series.performer.function",
    description = "SHALL, if possible, contain a code from value set http://hl7.org/fhir/ValueSet/series-performer-function",
    expression = "$this.memberOf('http://hl7.org/fhir/ValueSet/series-performer-function', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/ImagingStudy",
    generated = true
)
@Constraint(
    id = "imagingStudy-4",
    level = "Warning",
    location = "series.instance.sopClass",
    description = "SHALL, if possible, contain a code from value set http://dicom.nema.org/medical/dicom/current/output/chtml/part04/sect_B.5.html#table_B.5-1",
    expression = "$this.memberOf('http://dicom.nema.org/medical/dicom/current/output/chtml/part04/sect_B.5.html#table_B.5-1', 'extensible')",
    source = "http://hl7.org/fhir/StructureDefinition/ImagingStudy",
    generated = true
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class ImagingStudy extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    @Binding(
        bindingName = "ImagingStudyStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "The status of the ImagingStudy.",
        valueSet = "http://hl7.org/fhir/ValueSet/imagingstudy-status|5.0.0"
    )
    @Required
    private final ImagingStudyStatus status;
    @Summary
    @Binding(
        bindingName = "ImagingModality",
        strength = BindingStrength.Value.EXTENSIBLE,
        description = "Type of acquired data in the instance.",
        valueSet = "http://dicom.nema.org/medical/dicom/current/output/chtml/part16/sect_CID_33.html"
    )
    private final List<CodeableConcept> modality;
    @Summary
    @ReferenceTarget({ "Patient", "Device", "Group" })
    @Required
    private final Reference subject;
    @Summary
    @ReferenceTarget({ "Encounter" })
    private final Reference encounter;
    @Summary
    private final DateTime started;
    @Summary
    @ReferenceTarget({ "CarePlan", "ServiceRequest", "Appointment", "AppointmentResponse", "Task" })
    private final List<Reference> basedOn;
    @Summary
    @ReferenceTarget({ "Procedure" })
    private final List<Reference> partOf;
    @Summary
    @ReferenceTarget({ "Practitioner", "PractitionerRole" })
    private final Reference referrer;
    @Summary
    @ReferenceTarget({ "Endpoint" })
    private final List<Reference> endpoint;
    @Summary
    private final UnsignedInt numberOfSeries;
    @Summary
    private final UnsignedInt numberOfInstances;
    @Summary
    @Binding(
        bindingName = "ImagingProcedureCode",
        strength = BindingStrength.Value.PREFERRED,
        description = "Use of RadLex is preferred",
        valueSet = "http://loinc.org/vs/loinc-rsna-radiology-playbook"
    )
    private final List<CodeableReference> procedure;
    @Summary
    @ReferenceTarget({ "Location" })
    private final Reference location;
    @Summary
    @Binding(
        bindingName = "ImagingReason",
        strength = BindingStrength.Value.EXAMPLE,
        description = "The reason for the study.",
        valueSet = "http://hl7.org/fhir/ValueSet/procedure-reason"
    )
    private final List<CodeableReference> reason;
    @Summary
    private final List<Annotation> note;
    @Summary
    private final String description;
    @Summary
    private final List<Series> series;

    private ImagingStudy(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        status = builder.status;
        modality = Collections.unmodifiableList(builder.modality);
        subject = builder.subject;
        encounter = builder.encounter;
        started = builder.started;
        basedOn = Collections.unmodifiableList(builder.basedOn);
        partOf = Collections.unmodifiableList(builder.partOf);
        referrer = builder.referrer;
        endpoint = Collections.unmodifiableList(builder.endpoint);
        numberOfSeries = builder.numberOfSeries;
        numberOfInstances = builder.numberOfInstances;
        procedure = Collections.unmodifiableList(builder.procedure);
        location = builder.location;
        reason = Collections.unmodifiableList(builder.reason);
        note = Collections.unmodifiableList(builder.note);
        description = builder.description;
        series = Collections.unmodifiableList(builder.series);
    }

    /**
     * Identifiers for the ImagingStudy such as DICOM Study Instance UID.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The current state of the ImagingStudy resource. This is not the status of any ServiceRequest or Task resources 
     * associated with the ImagingStudy.
     * 
     * @return
     *     An immutable object of type {@link ImagingStudyStatus} that is non-null.
     */
    public ImagingStudyStatus getStatus() {
        return status;
    }

    /**
     * A list of all the distinct values of series.modality. This may include both acquisition and non-acquisition modalities.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableConcept} that may be empty.
     */
    public List<CodeableConcept> getModality() {
        return modality;
    }

    /**
     * The subject, typically a patient, of the imaging study.
     * 
     * @return
     *     An immutable object of type {@link Reference} that is non-null.
     */
    public Reference getSubject() {
        return subject;
    }

    /**
     * The healthcare event (e.g. a patient and healthcare provider interaction) during which this ImagingStudy is made.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getEncounter() {
        return encounter;
    }

    /**
     * Date and time the study started.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getStarted() {
        return started;
    }

    /**
     * A list of the diagnostic requests that resulted in this imaging study being performed.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getBasedOn() {
        return basedOn;
    }

    /**
     * A larger event of which this particular ImagingStudy is a component or step. For example, an ImagingStudy as part of a 
     * procedure.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getPartOf() {
        return partOf;
    }

    /**
     * The requesting/referring physician.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getReferrer() {
        return referrer;
    }

    /**
     * The network service providing access (e.g., query, view, or retrieval) for the study. See implementation notes for 
     * information about using DICOM endpoints. A study-level endpoint applies to each series in the study, unless overridden 
     * by a series-level endpoint with the same Endpoint.connectionType.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getEndpoint() {
        return endpoint;
    }

    /**
     * Number of Series in the Study. This value given may be larger than the number of series elements this Resource 
     * contains due to resource availability, security, or other factors. This element should be present if any series 
     * elements are present.
     * 
     * @return
     *     An immutable object of type {@link UnsignedInt} that may be null.
     */
    public UnsignedInt getNumberOfSeries() {
        return numberOfSeries;
    }

    /**
     * Number of SOP Instances in Study. This value given may be larger than the number of instance elements this resource 
     * contains due to resource availability, security, or other factors. This element should be present if any instance 
     * elements are present.
     * 
     * @return
     *     An immutable object of type {@link UnsignedInt} that may be null.
     */
    public UnsignedInt getNumberOfInstances() {
        return numberOfInstances;
    }

    /**
     * This field corresponds to the DICOM Procedure Code Sequence (0008,1032). This is different from the FHIR Procedure 
     * resource that may include the ImagingStudy.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getProcedure() {
        return procedure;
    }

    /**
     * The principal physical location where the ImagingStudy was performed.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getLocation() {
        return location;
    }

    /**
     * Description of clinical condition indicating why the ImagingStudy was requested, and/or Indicates another resource 
     * whose existence justifies this Study.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getReason() {
        return reason;
    }

    /**
     * Per the recommended DICOM mapping, this element is derived from the Study Description attribute (0008,1030). 
     * Observations or findings about the imaging study should be recorded in another resource, e.g. Observation, and not in 
     * this element.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Annotation} that may be empty.
     */
    public List<Annotation> getNote() {
        return note;
    }

    /**
     * The Imaging Manager description of the study. Institution-generated description or classification of the Study 
     * (component) performed.
     * 
     * @return
     *     An immutable object of type {@link String} that may be null.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Each study has one or more series of images or other content.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Series} that may be empty.
     */
    public List<Series> getSeries() {
        return series;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            (status != null) || 
            !modality.isEmpty() || 
            (subject != null) || 
            (encounter != null) || 
            (started != null) || 
            !basedOn.isEmpty() || 
            !partOf.isEmpty() || 
            (referrer != null) || 
            !endpoint.isEmpty() || 
            (numberOfSeries != null) || 
            (numberOfInstances != null) || 
            !procedure.isEmpty() || 
            (location != null) || 
            !reason.isEmpty() || 
            !note.isEmpty() || 
            (description != null) || 
            !series.isEmpty();
    }

    @Override
    public void accept(java.lang.String elementName, int elementIndex, Visitor visitor) {
        if (visitor.preVisit(this)) {
            visitor.visitStart(elementName, elementIndex, this);
            if (visitor.visit(elementName, elementIndex, this)) {
                // visit children
                accept(id, "id", visitor);
                accept(meta, "meta", visitor);
                accept(implicitRules, "implicitRules", visitor);
                accept(language, "language", visitor);
                accept(text, "text", visitor);
                accept(contained, "contained", visitor, Resource.class);
                accept(extension, "extension", visitor, Extension.class);
                accept(modifierExtension, "modifierExtension", visitor, Extension.class);
                accept(identifier, "identifier", visitor, Identifier.class);
                accept(status, "status", visitor);
                accept(modality, "modality", visitor, CodeableConcept.class);
                accept(subject, "subject", visitor);
                accept(encounter, "encounter", visitor);
                accept(started, "started", visitor);
                accept(basedOn, "basedOn", visitor, Reference.class);
                accept(partOf, "partOf", visitor, Reference.class);
                accept(referrer, "referrer", visitor);
                accept(endpoint, "endpoint", visitor, Reference.class);
                accept(numberOfSeries, "numberOfSeries", visitor);
                accept(numberOfInstances, "numberOfInstances", visitor);
                accept(procedure, "procedure", visitor, CodeableReference.class);
                accept(location, "location", visitor);
                accept(reason, "reason", visitor, CodeableReference.class);
                accept(note, "note", visitor, Annotation.class);
                accept(description, "description", visitor);
                accept(series, "series", visitor, Series.class);
            }
            visitor.visitEnd(elementName, elementIndex, this);
            visitor.postVisit(this);
        }
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
        ImagingStudy other = (ImagingStudy) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(status, other.status) && 
            Objects.equals(modality, other.modality) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(encounter, other.encounter) && 
            Objects.equals(started, other.started) && 
            Objects.equals(basedOn, other.basedOn) && 
            Objects.equals(partOf, other.partOf) && 
            Objects.equals(referrer, other.referrer) && 
            Objects.equals(endpoint, other.endpoint) && 
            Objects.equals(numberOfSeries, other.numberOfSeries) && 
            Objects.equals(numberOfInstances, other.numberOfInstances) && 
            Objects.equals(procedure, other.procedure) && 
            Objects.equals(location, other.location) && 
            Objects.equals(reason, other.reason) && 
            Objects.equals(note, other.note) && 
            Objects.equals(description, other.description) && 
            Objects.equals(series, other.series);
    }

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = Objects.hash(id, 
                meta, 
                implicitRules, 
                language, 
                text, 
                contained, 
                extension, 
                modifierExtension, 
                identifier, 
                status, 
                modality, 
                subject, 
                encounter, 
                started, 
                basedOn, 
                partOf, 
                referrer, 
                endpoint, 
                numberOfSeries, 
                numberOfInstances, 
                procedure, 
                location, 
                reason, 
                note, 
                description, 
                series);
            hashCode = result;
        }
        return result;
    }

    @Override
    public Builder toBuilder() {
        return new Builder().from(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends DomainResource.Builder {
        private List<Identifier> identifier = new ArrayList<>();
        private ImagingStudyStatus status;
        private List<CodeableConcept> modality = new ArrayList<>();
        private Reference subject;
        private Reference encounter;
        private DateTime started;
        private List<Reference> basedOn = new ArrayList<>();
        private List<Reference> partOf = new ArrayList<>();
        private Reference referrer;
        private List<Reference> endpoint = new ArrayList<>();
        private UnsignedInt numberOfSeries;
        private UnsignedInt numberOfInstances;
        private List<CodeableReference> procedure = new ArrayList<>();
        private Reference location;
        private List<CodeableReference> reason = new ArrayList<>();
        private List<Annotation> note = new ArrayList<>();
        private String description;
        private List<Series> series = new ArrayList<>();

        private Builder() {
            super();
        }

        /**
         * The logical id of the resource, as used in the URL for the resource. Once assigned, this value never changes.
         * 
         * @param id
         *     Logical id of this artifact
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder id(java.lang.String id) {
            return (Builder) super.id(id);
        }

        /**
         * The metadata about the resource. This is content that is maintained by the infrastructure. Changes to the content 
         * might not always be associated with version changes to the resource.
         * 
         * @param meta
         *     Metadata about the resource
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder meta(Meta meta) {
            return (Builder) super.meta(meta);
        }

        /**
         * A reference to a set of rules that were followed when the resource was constructed, and which must be understood when 
         * processing the content. Often, this is a reference to an implementation guide that defines the special rules along 
         * with other profiles etc.
         * 
         * @param implicitRules
         *     A set of rules under which this content was created
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder implicitRules(Uri implicitRules) {
            return (Builder) super.implicitRules(implicitRules);
        }

        /**
         * The base language in which the resource is written.
         * 
         * @param language
         *     Language of the resource content
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder language(Code language) {
            return (Builder) super.language(language);
        }

        /**
         * A human-readable narrative that contains a summary of the resource and can be used to represent the content of the 
         * resource to a human. The narrative need not encode all the structured data, but is required to contain sufficient 
         * detail to make it "clinically safe" for a human to just read the narrative. Resource definitions may define what 
         * content should be represented in the narrative to ensure clinical safety.
         * 
         * @param text
         *     Text summary of the resource, for human interpretation
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder text(Narrative text) {
            return (Builder) super.text(text);
        }

        /**
         * These resources do not have an independent existence apart from the resource that contains them - they cannot be 
         * identified independently, nor can they have their own independent transaction scope. This is allowed to be a 
         * Parameters resource if and only if it is referenced by a resource that provides context/meaning.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param contained
         *     Contained, inline Resources
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder contained(Resource... contained) {
            return (Builder) super.contained(contained);
        }

        /**
         * These resources do not have an independent existence apart from the resource that contains them - they cannot be 
         * identified independently, nor can they have their own independent transaction scope. This is allowed to be a 
         * Parameters resource if and only if it is referenced by a resource that provides context/meaning.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param contained
         *     Contained, inline Resources
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        @Override
        public Builder contained(Collection<Resource> contained) {
            return (Builder) super.contained(contained);
        }

        /**
         * May be used to represent additional information that is not part of the basic definition of the resource. To make the 
         * use of extensions safe and managable, there is a strict set of governance applied to the definition and use of 
         * extensions. Though any implementer can define an extension, there is a set of requirements that SHALL be met as part 
         * of the definition of the extension.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param extension
         *     Additional content defined by implementations
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder extension(Extension... extension) {
            return (Builder) super.extension(extension);
        }

        /**
         * May be used to represent additional information that is not part of the basic definition of the resource. To make the 
         * use of extensions safe and managable, there is a strict set of governance applied to the definition and use of 
         * extensions. Though any implementer can define an extension, there is a set of requirements that SHALL be met as part 
         * of the definition of the extension.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param extension
         *     Additional content defined by implementations
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        @Override
        public Builder extension(Collection<Extension> extension) {
            return (Builder) super.extension(extension);
        }

        /**
         * May be used to represent additional information that is not part of the basic definition of the resource and that 
         * modifies the understanding of the element that contains it and/or the understanding of the containing element's 
         * descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe and 
         * managable, there is a strict set of governance applied to the definition and use of extensions. Though any implementer 
         * is allowed to define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
         * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
         * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
         * modifierExtension itself).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param modifierExtension
         *     Extensions that cannot be ignored
         * 
         * @return
         *     A reference to this Builder instance
         */
        @Override
        public Builder modifierExtension(Extension... modifierExtension) {
            return (Builder) super.modifierExtension(modifierExtension);
        }

        /**
         * May be used to represent additional information that is not part of the basic definition of the resource and that 
         * modifies the understanding of the element that contains it and/or the understanding of the containing element's 
         * descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe and 
         * managable, there is a strict set of governance applied to the definition and use of extensions. Though any implementer 
         * is allowed to define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
         * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
         * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
         * modifierExtension itself).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param modifierExtension
         *     Extensions that cannot be ignored
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        @Override
        public Builder modifierExtension(Collection<Extension> modifierExtension) {
            return (Builder) super.modifierExtension(modifierExtension);
        }

        /**
         * Identifiers for the ImagingStudy such as DICOM Study Instance UID.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Identifiers for the whole study
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder identifier(Identifier... identifier) {
            for (Identifier value : identifier) {
                this.identifier.add(value);
            }
            return this;
        }

        /**
         * Identifiers for the ImagingStudy such as DICOM Study Instance UID.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     Identifiers for the whole study
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder identifier(Collection<Identifier> identifier) {
            this.identifier = new ArrayList<>(identifier);
            return this;
        }

        /**
         * The current state of the ImagingStudy resource. This is not the status of any ServiceRequest or Task resources 
         * associated with the ImagingStudy.
         * 
         * <p>This element is required.
         * 
         * @param status
         *     registered | available | cancelled | entered-in-error | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(ImagingStudyStatus status) {
            this.status = status;
            return this;
        }

        /**
         * A list of all the distinct values of series.modality. This may include both acquisition and non-acquisition modalities.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param modality
         *     All of the distinct values for series' modalities
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder modality(CodeableConcept... modality) {
            for (CodeableConcept value : modality) {
                this.modality.add(value);
            }
            return this;
        }

        /**
         * A list of all the distinct values of series.modality. This may include both acquisition and non-acquisition modalities.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param modality
         *     All of the distinct values for series' modalities
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder modality(Collection<CodeableConcept> modality) {
            this.modality = new ArrayList<>(modality);
            return this;
        }

        /**
         * The subject, typically a patient, of the imaging study.
         * 
         * <p>This element is required.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link Device}</li>
         * <li>{@link Group}</li>
         * </ul>
         * 
         * @param subject
         *     Who or what is the subject of the study
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Reference subject) {
            this.subject = subject;
            return this;
        }

        /**
         * The healthcare event (e.g. a patient and healthcare provider interaction) during which this ImagingStudy is made.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Encounter}</li>
         * </ul>
         * 
         * @param encounter
         *     Encounter with which this imaging study is associated
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder encounter(Reference encounter) {
            this.encounter = encounter;
            return this;
        }

        /**
         * Date and time the study started.
         * 
         * @param started
         *     When the study was started
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder started(DateTime started) {
            this.started = started;
            return this;
        }

        /**
         * A list of the diagnostic requests that resulted in this imaging study being performed.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CarePlan}</li>
         * <li>{@link ServiceRequest}</li>
         * <li>{@link Appointment}</li>
         * <li>{@link AppointmentResponse}</li>
         * <li>{@link Task}</li>
         * </ul>
         * 
         * @param basedOn
         *     Request fulfilled
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder basedOn(Reference... basedOn) {
            for (Reference value : basedOn) {
                this.basedOn.add(value);
            }
            return this;
        }

        /**
         * A list of the diagnostic requests that resulted in this imaging study being performed.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link CarePlan}</li>
         * <li>{@link ServiceRequest}</li>
         * <li>{@link Appointment}</li>
         * <li>{@link AppointmentResponse}</li>
         * <li>{@link Task}</li>
         * </ul>
         * 
         * @param basedOn
         *     Request fulfilled
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder basedOn(Collection<Reference> basedOn) {
            this.basedOn = new ArrayList<>(basedOn);
            return this;
        }

        /**
         * A larger event of which this particular ImagingStudy is a component or step. For example, an ImagingStudy as part of a 
         * procedure.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Procedure}</li>
         * </ul>
         * 
         * @param partOf
         *     Part of referenced event
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder partOf(Reference... partOf) {
            for (Reference value : partOf) {
                this.partOf.add(value);
            }
            return this;
        }

        /**
         * A larger event of which this particular ImagingStudy is a component or step. For example, an ImagingStudy as part of a 
         * procedure.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Procedure}</li>
         * </ul>
         * 
         * @param partOf
         *     Part of referenced event
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder partOf(Collection<Reference> partOf) {
            this.partOf = new ArrayList<>(partOf);
            return this;
        }

        /**
         * The requesting/referring physician.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * </ul>
         * 
         * @param referrer
         *     Referring physician
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder referrer(Reference referrer) {
            this.referrer = referrer;
            return this;
        }

        /**
         * The network service providing access (e.g., query, view, or retrieval) for the study. See implementation notes for 
         * information about using DICOM endpoints. A study-level endpoint applies to each series in the study, unless overridden 
         * by a series-level endpoint with the same Endpoint.connectionType.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Endpoint}</li>
         * </ul>
         * 
         * @param endpoint
         *     Study access endpoint
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder endpoint(Reference... endpoint) {
            for (Reference value : endpoint) {
                this.endpoint.add(value);
            }
            return this;
        }

        /**
         * The network service providing access (e.g., query, view, or retrieval) for the study. See implementation notes for 
         * information about using DICOM endpoints. A study-level endpoint applies to each series in the study, unless overridden 
         * by a series-level endpoint with the same Endpoint.connectionType.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Endpoint}</li>
         * </ul>
         * 
         * @param endpoint
         *     Study access endpoint
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder endpoint(Collection<Reference> endpoint) {
            this.endpoint = new ArrayList<>(endpoint);
            return this;
        }

        /**
         * Number of Series in the Study. This value given may be larger than the number of series elements this Resource 
         * contains due to resource availability, security, or other factors. This element should be present if any series 
         * elements are present.
         * 
         * @param numberOfSeries
         *     Number of Study Related Series
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder numberOfSeries(UnsignedInt numberOfSeries) {
            this.numberOfSeries = numberOfSeries;
            return this;
        }

        /**
         * Number of SOP Instances in Study. This value given may be larger than the number of instance elements this resource 
         * contains due to resource availability, security, or other factors. This element should be present if any instance 
         * elements are present.
         * 
         * @param numberOfInstances
         *     Number of Study Related Instances
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder numberOfInstances(UnsignedInt numberOfInstances) {
            this.numberOfInstances = numberOfInstances;
            return this;
        }

        /**
         * This field corresponds to the DICOM Procedure Code Sequence (0008,1032). This is different from the FHIR Procedure 
         * resource that may include the ImagingStudy.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param procedure
         *     The performed procedure or code
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder procedure(CodeableReference... procedure) {
            for (CodeableReference value : procedure) {
                this.procedure.add(value);
            }
            return this;
        }

        /**
         * This field corresponds to the DICOM Procedure Code Sequence (0008,1032). This is different from the FHIR Procedure 
         * resource that may include the ImagingStudy.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param procedure
         *     The performed procedure or code
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder procedure(Collection<CodeableReference> procedure) {
            this.procedure = new ArrayList<>(procedure);
            return this;
        }

        /**
         * The principal physical location where the ImagingStudy was performed.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Location}</li>
         * </ul>
         * 
         * @param location
         *     Where ImagingStudy occurred
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder location(Reference location) {
            this.location = location;
            return this;
        }

        /**
         * Description of clinical condition indicating why the ImagingStudy was requested, and/or Indicates another resource 
         * whose existence justifies this Study.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     Why the study was requested / performed
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder reason(CodeableReference... reason) {
            for (CodeableReference value : reason) {
                this.reason.add(value);
            }
            return this;
        }

        /**
         * Description of clinical condition indicating why the ImagingStudy was requested, and/or Indicates another resource 
         * whose existence justifies this Study.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     Why the study was requested / performed
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder reason(Collection<CodeableReference> reason) {
            this.reason = new ArrayList<>(reason);
            return this;
        }

        /**
         * Per the recommended DICOM mapping, this element is derived from the Study Description attribute (0008,1030). 
         * Observations or findings about the imaging study should be recorded in another resource, e.g. Observation, and not in 
         * this element.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     User-defined comments
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder note(Annotation... note) {
            for (Annotation value : note) {
                this.note.add(value);
            }
            return this;
        }

        /**
         * Per the recommended DICOM mapping, this element is derived from the Study Description attribute (0008,1030). 
         * Observations or findings about the imaging study should be recorded in another resource, e.g. Observation, and not in 
         * this element.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     User-defined comments
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder note(Collection<Annotation> note) {
            this.note = new ArrayList<>(note);
            return this;
        }

        /**
         * Convenience method for setting {@code description}.
         * 
         * @param description
         *     Institution-generated description
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #description(org.linuxforhealth.fhir.model.type.String)
         */
        public Builder description(java.lang.String description) {
            this.description = (description == null) ? null : String.of(description);
            return this;
        }

        /**
         * The Imaging Manager description of the study. Institution-generated description or classification of the Study 
         * (component) performed.
         * 
         * @param description
         *     Institution-generated description
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Each study has one or more series of images or other content.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param series
         *     Each study has one or more series of instances
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder series(Series... series) {
            for (Series value : series) {
                this.series.add(value);
            }
            return this;
        }

        /**
         * Each study has one or more series of images or other content.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param series
         *     Each study has one or more series of instances
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder series(Collection<Series> series) {
            this.series = new ArrayList<>(series);
            return this;
        }

        /**
         * Build the {@link ImagingStudy}
         * 
         * <p>Required elements:
         * <ul>
         * <li>status</li>
         * <li>subject</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link ImagingStudy}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid ImagingStudy per the base specification
         */
        @Override
        public ImagingStudy build() {
            ImagingStudy imagingStudy = new ImagingStudy(this);
            if (validating) {
                validate(imagingStudy);
            }
            return imagingStudy;
        }

        protected void validate(ImagingStudy imagingStudy) {
            super.validate(imagingStudy);
            ValidationSupport.checkList(imagingStudy.identifier, "identifier", Identifier.class);
            ValidationSupport.requireNonNull(imagingStudy.status, "status");
            ValidationSupport.checkList(imagingStudy.modality, "modality", CodeableConcept.class);
            ValidationSupport.requireNonNull(imagingStudy.subject, "subject");
            ValidationSupport.checkList(imagingStudy.basedOn, "basedOn", Reference.class);
            ValidationSupport.checkList(imagingStudy.partOf, "partOf", Reference.class);
            ValidationSupport.checkList(imagingStudy.endpoint, "endpoint", Reference.class);
            ValidationSupport.checkList(imagingStudy.procedure, "procedure", CodeableReference.class);
            ValidationSupport.checkList(imagingStudy.reason, "reason", CodeableReference.class);
            ValidationSupport.checkList(imagingStudy.note, "note", Annotation.class);
            ValidationSupport.checkList(imagingStudy.series, "series", Series.class);
            ValidationSupport.checkReferenceType(imagingStudy.subject, "subject", "Patient", "Device", "Group");
            ValidationSupport.checkReferenceType(imagingStudy.encounter, "encounter", "Encounter");
            ValidationSupport.checkReferenceType(imagingStudy.basedOn, "basedOn", "CarePlan", "ServiceRequest", "Appointment", "AppointmentResponse", "Task");
            ValidationSupport.checkReferenceType(imagingStudy.partOf, "partOf", "Procedure");
            ValidationSupport.checkReferenceType(imagingStudy.referrer, "referrer", "Practitioner", "PractitionerRole");
            ValidationSupport.checkReferenceType(imagingStudy.endpoint, "endpoint", "Endpoint");
            ValidationSupport.checkReferenceType(imagingStudy.location, "location", "Location");
        }

        protected Builder from(ImagingStudy imagingStudy) {
            super.from(imagingStudy);
            identifier.addAll(imagingStudy.identifier);
            status = imagingStudy.status;
            modality.addAll(imagingStudy.modality);
            subject = imagingStudy.subject;
            encounter = imagingStudy.encounter;
            started = imagingStudy.started;
            basedOn.addAll(imagingStudy.basedOn);
            partOf.addAll(imagingStudy.partOf);
            referrer = imagingStudy.referrer;
            endpoint.addAll(imagingStudy.endpoint);
            numberOfSeries = imagingStudy.numberOfSeries;
            numberOfInstances = imagingStudy.numberOfInstances;
            procedure.addAll(imagingStudy.procedure);
            location = imagingStudy.location;
            reason.addAll(imagingStudy.reason);
            note.addAll(imagingStudy.note);
            description = imagingStudy.description;
            series.addAll(imagingStudy.series);
            return this;
        }
    }

    /**
     * Each study has one or more series of images or other content.
     */
    public static class Series extends BackboneElement {
        @Summary
        @Required
        private final Id uid;
        @Summary
        private final UnsignedInt number;
        @Summary
        @Binding(
            bindingName = "ImagingModality",
            strength = BindingStrength.Value.EXTENSIBLE,
            description = "Type of acquired data in the instance.",
            valueSet = "http://dicom.nema.org/medical/dicom/current/output/chtml/part16/sect_CID_33.html"
        )
        @Required
        private final CodeableConcept modality;
        @Summary
        private final String description;
        @Summary
        private final UnsignedInt numberOfInstances;
        @Summary
        @ReferenceTarget({ "Endpoint" })
        private final List<Reference> endpoint;
        @Summary
        @Binding(
            bindingName = "BodySite",
            strength = BindingStrength.Value.EXAMPLE,
            description = "SNOMED CT Body site concepts",
            valueSet = "http://hl7.org/fhir/ValueSet/body-site"
        )
        private final CodeableReference bodySite;
        @Summary
        @Binding(
            bindingName = "Laterality",
            strength = BindingStrength.Value.EXAMPLE,
            description = "Codes describing body site laterality (left, right, etc.).",
            valueSet = "http://dicom.nema.org/medical/dicom/current/output/chtml/part16/sect_CID_244.html"
        )
        private final CodeableConcept laterality;
        @Summary
        @ReferenceTarget({ "Specimen" })
        private final List<Reference> specimen;
        @Summary
        private final DateTime started;
        @Summary
        private final List<Performer> performer;
        private final List<Instance> instance;

        private Series(Builder builder) {
            super(builder);
            uid = builder.uid;
            number = builder.number;
            modality = builder.modality;
            description = builder.description;
            numberOfInstances = builder.numberOfInstances;
            endpoint = Collections.unmodifiableList(builder.endpoint);
            bodySite = builder.bodySite;
            laterality = builder.laterality;
            specimen = Collections.unmodifiableList(builder.specimen);
            started = builder.started;
            performer = Collections.unmodifiableList(builder.performer);
            instance = Collections.unmodifiableList(builder.instance);
        }

        /**
         * The DICOM Series Instance UID for the series.
         * 
         * @return
         *     An immutable object of type {@link Id} that is non-null.
         */
        public Id getUid() {
            return uid;
        }

        /**
         * The numeric identifier of this series in the study.
         * 
         * @return
         *     An immutable object of type {@link UnsignedInt} that may be null.
         */
        public UnsignedInt getNumber() {
            return number;
        }

        /**
         * The distinct modality for this series. This may include both acquisition and non-acquisition modalities.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that is non-null.
         */
        public CodeableConcept getModality() {
            return modality;
        }

        /**
         * A description of the series.
         * 
         * @return
         *     An immutable object of type {@link String} that may be null.
         */
        public String getDescription() {
            return description;
        }

        /**
         * Number of SOP Instances in the Study. The value given may be larger than the number of instance elements this resource 
         * contains due to resource availability, security, or other factors. This element should be present if any instance 
         * elements are present.
         * 
         * @return
         *     An immutable object of type {@link UnsignedInt} that may be null.
         */
        public UnsignedInt getNumberOfInstances() {
            return numberOfInstances;
        }

        /**
         * The network service providing access (e.g., query, view, or retrieval) for this series. See implementation notes for 
         * information about using DICOM endpoints. A series-level endpoint, if present, has precedence over a study-level 
         * endpoint with the same Endpoint.connectionType.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
         */
        public List<Reference> getEndpoint() {
            return endpoint;
        }

        /**
         * The anatomic structures examined. See DICOM Part 16 Annex L (http://dicom.nema.
         * org/medical/dicom/current/output/chtml/part16/chapter_L.html) for DICOM to SNOMED-CT mappings. The bodySite may 
         * indicate the laterality of body part imaged; if so, it shall be consistent with any content of ImagingStudy.series.
         * laterality.
         * 
         * @return
         *     An immutable object of type {@link CodeableReference} that may be null.
         */
        public CodeableReference getBodySite() {
            return bodySite;
        }

        /**
         * The laterality of the (possibly paired) anatomic structures examined. E.g., the left knee, both lungs, or unpaired 
         * abdomen. If present, shall be consistent with any laterality information indicated in ImagingStudy.series.bodySite.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getLaterality() {
            return laterality;
        }

        /**
         * The specimen imaged, e.g., for whole slide imaging of a biopsy.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
         */
        public List<Reference> getSpecimen() {
            return specimen;
        }

        /**
         * The date and time the series was started.
         * 
         * @return
         *     An immutable object of type {@link DateTime} that may be null.
         */
        public DateTime getStarted() {
            return started;
        }

        /**
         * Indicates who or what performed the series and how they were involved.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Performer} that may be empty.
         */
        public List<Performer> getPerformer() {
            return performer;
        }

        /**
         * A single SOP instance within the series, e.g. an image, or presentation state.
         * 
         * @return
         *     An unmodifiable list containing immutable objects of type {@link Instance} that may be empty.
         */
        public List<Instance> getInstance() {
            return instance;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (uid != null) || 
                (number != null) || 
                (modality != null) || 
                (description != null) || 
                (numberOfInstances != null) || 
                !endpoint.isEmpty() || 
                (bodySite != null) || 
                (laterality != null) || 
                !specimen.isEmpty() || 
                (started != null) || 
                !performer.isEmpty() || 
                !instance.isEmpty();
        }

        @Override
        public void accept(java.lang.String elementName, int elementIndex, Visitor visitor) {
            if (visitor.preVisit(this)) {
                visitor.visitStart(elementName, elementIndex, this);
                if (visitor.visit(elementName, elementIndex, this)) {
                    // visit children
                    accept(id, "id", visitor);
                    accept(extension, "extension", visitor, Extension.class);
                    accept(modifierExtension, "modifierExtension", visitor, Extension.class);
                    accept(uid, "uid", visitor);
                    accept(number, "number", visitor);
                    accept(modality, "modality", visitor);
                    accept(description, "description", visitor);
                    accept(numberOfInstances, "numberOfInstances", visitor);
                    accept(endpoint, "endpoint", visitor, Reference.class);
                    accept(bodySite, "bodySite", visitor);
                    accept(laterality, "laterality", visitor);
                    accept(specimen, "specimen", visitor, Reference.class);
                    accept(started, "started", visitor);
                    accept(performer, "performer", visitor, Performer.class);
                    accept(instance, "instance", visitor, Instance.class);
                }
                visitor.visitEnd(elementName, elementIndex, this);
                visitor.postVisit(this);
            }
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
            Series other = (Series) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(uid, other.uid) && 
                Objects.equals(number, other.number) && 
                Objects.equals(modality, other.modality) && 
                Objects.equals(description, other.description) && 
                Objects.equals(numberOfInstances, other.numberOfInstances) && 
                Objects.equals(endpoint, other.endpoint) && 
                Objects.equals(bodySite, other.bodySite) && 
                Objects.equals(laterality, other.laterality) && 
                Objects.equals(specimen, other.specimen) && 
                Objects.equals(started, other.started) && 
                Objects.equals(performer, other.performer) && 
                Objects.equals(instance, other.instance);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    uid, 
                    number, 
                    modality, 
                    description, 
                    numberOfInstances, 
                    endpoint, 
                    bodySite, 
                    laterality, 
                    specimen, 
                    started, 
                    performer, 
                    instance);
                hashCode = result;
            }
            return result;
        }

        @Override
        public Builder toBuilder() {
            return new Builder().from(this);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder extends BackboneElement.Builder {
            private Id uid;
            private UnsignedInt number;
            private CodeableConcept modality;
            private String description;
            private UnsignedInt numberOfInstances;
            private List<Reference> endpoint = new ArrayList<>();
            private CodeableReference bodySite;
            private CodeableConcept laterality;
            private List<Reference> specimen = new ArrayList<>();
            private DateTime started;
            private List<Performer> performer = new ArrayList<>();
            private List<Instance> instance = new ArrayList<>();

            private Builder() {
                super();
            }

            /**
             * Unique id for the element within a resource (for internal references). This may be any string value that does not 
             * contain spaces.
             * 
             * @param id
             *     Unique id for inter-element referencing
             * 
             * @return
             *     A reference to this Builder instance
             */
            @Override
            public Builder id(java.lang.String id) {
                return (Builder) super.id(id);
            }

            /**
             * May be used to represent additional information that is not part of the basic definition of the element. To make the 
             * use of extensions safe and managable, there is a strict set of governance applied to the definition and use of 
             * extensions. Though any implementer can define an extension, there is a set of requirements that SHALL be met as part 
             * of the definition of the extension.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param extension
             *     Additional content defined by implementations
             * 
             * @return
             *     A reference to this Builder instance
             */
            @Override
            public Builder extension(Extension... extension) {
                return (Builder) super.extension(extension);
            }

            /**
             * May be used to represent additional information that is not part of the basic definition of the element. To make the 
             * use of extensions safe and managable, there is a strict set of governance applied to the definition and use of 
             * extensions. Though any implementer can define an extension, there is a set of requirements that SHALL be met as part 
             * of the definition of the extension.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param extension
             *     Additional content defined by implementations
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            @Override
            public Builder extension(Collection<Extension> extension) {
                return (Builder) super.extension(extension);
            }

            /**
             * May be used to represent additional information that is not part of the basic definition of the element and that 
             * modifies the understanding of the element in which it is contained and/or the understanding of the containing 
             * element's descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe 
             * and managable, there is a strict set of governance applied to the definition and use of extensions. Though any 
             * implementer can define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
             * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
             * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
             * modifierExtension itself).
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param modifierExtension
             *     Extensions that cannot be ignored even if unrecognized
             * 
             * @return
             *     A reference to this Builder instance
             */
            @Override
            public Builder modifierExtension(Extension... modifierExtension) {
                return (Builder) super.modifierExtension(modifierExtension);
            }

            /**
             * May be used to represent additional information that is not part of the basic definition of the element and that 
             * modifies the understanding of the element in which it is contained and/or the understanding of the containing 
             * element's descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe 
             * and managable, there is a strict set of governance applied to the definition and use of extensions. Though any 
             * implementer can define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
             * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
             * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
             * modifierExtension itself).
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param modifierExtension
             *     Extensions that cannot be ignored even if unrecognized
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            @Override
            public Builder modifierExtension(Collection<Extension> modifierExtension) {
                return (Builder) super.modifierExtension(modifierExtension);
            }

            /**
             * The DICOM Series Instance UID for the series.
             * 
             * <p>This element is required.
             * 
             * @param uid
             *     DICOM Series Instance UID for the series
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder uid(Id uid) {
                this.uid = uid;
                return this;
            }

            /**
             * The numeric identifier of this series in the study.
             * 
             * @param number
             *     Numeric identifier of this series
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder number(UnsignedInt number) {
                this.number = number;
                return this;
            }

            /**
             * The distinct modality for this series. This may include both acquisition and non-acquisition modalities.
             * 
             * <p>This element is required.
             * 
             * @param modality
             *     The modality used for this series
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder modality(CodeableConcept modality) {
                this.modality = modality;
                return this;
            }

            /**
             * Convenience method for setting {@code description}.
             * 
             * @param description
             *     A short human readable summary of the series
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #description(org.linuxforhealth.fhir.model.type.String)
             */
            public Builder description(java.lang.String description) {
                this.description = (description == null) ? null : String.of(description);
                return this;
            }

            /**
             * A description of the series.
             * 
             * @param description
             *     A short human readable summary of the series
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder description(String description) {
                this.description = description;
                return this;
            }

            /**
             * Number of SOP Instances in the Study. The value given may be larger than the number of instance elements this resource 
             * contains due to resource availability, security, or other factors. This element should be present if any instance 
             * elements are present.
             * 
             * @param numberOfInstances
             *     Number of Series Related Instances
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder numberOfInstances(UnsignedInt numberOfInstances) {
                this.numberOfInstances = numberOfInstances;
                return this;
            }

            /**
             * The network service providing access (e.g., query, view, or retrieval) for this series. See implementation notes for 
             * information about using DICOM endpoints. A series-level endpoint, if present, has precedence over a study-level 
             * endpoint with the same Endpoint.connectionType.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link Endpoint}</li>
             * </ul>
             * 
             * @param endpoint
             *     Series access endpoint
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder endpoint(Reference... endpoint) {
                for (Reference value : endpoint) {
                    this.endpoint.add(value);
                }
                return this;
            }

            /**
             * The network service providing access (e.g., query, view, or retrieval) for this series. See implementation notes for 
             * information about using DICOM endpoints. A series-level endpoint, if present, has precedence over a study-level 
             * endpoint with the same Endpoint.connectionType.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link Endpoint}</li>
             * </ul>
             * 
             * @param endpoint
             *     Series access endpoint
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder endpoint(Collection<Reference> endpoint) {
                this.endpoint = new ArrayList<>(endpoint);
                return this;
            }

            /**
             * The anatomic structures examined. See DICOM Part 16 Annex L (http://dicom.nema.
             * org/medical/dicom/current/output/chtml/part16/chapter_L.html) for DICOM to SNOMED-CT mappings. The bodySite may 
             * indicate the laterality of body part imaged; if so, it shall be consistent with any content of ImagingStudy.series.
             * laterality.
             * 
             * @param bodySite
             *     Body part examined
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder bodySite(CodeableReference bodySite) {
                this.bodySite = bodySite;
                return this;
            }

            /**
             * The laterality of the (possibly paired) anatomic structures examined. E.g., the left knee, both lungs, or unpaired 
             * abdomen. If present, shall be consistent with any laterality information indicated in ImagingStudy.series.bodySite.
             * 
             * @param laterality
             *     Body part laterality
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder laterality(CodeableConcept laterality) {
                this.laterality = laterality;
                return this;
            }

            /**
             * The specimen imaged, e.g., for whole slide imaging of a biopsy.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link Specimen}</li>
             * </ul>
             * 
             * @param specimen
             *     Specimen imaged
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder specimen(Reference... specimen) {
                for (Reference value : specimen) {
                    this.specimen.add(value);
                }
                return this;
            }

            /**
             * The specimen imaged, e.g., for whole slide imaging of a biopsy.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * <p>Allowed resource types for the references:
             * <ul>
             * <li>{@link Specimen}</li>
             * </ul>
             * 
             * @param specimen
             *     Specimen imaged
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder specimen(Collection<Reference> specimen) {
                this.specimen = new ArrayList<>(specimen);
                return this;
            }

            /**
             * The date and time the series was started.
             * 
             * @param started
             *     When the series started
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder started(DateTime started) {
                this.started = started;
                return this;
            }

            /**
             * Indicates who or what performed the series and how they were involved.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param performer
             *     Who performed the series
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder performer(Performer... performer) {
                for (Performer value : performer) {
                    this.performer.add(value);
                }
                return this;
            }

            /**
             * Indicates who or what performed the series and how they were involved.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param performer
             *     Who performed the series
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder performer(Collection<Performer> performer) {
                this.performer = new ArrayList<>(performer);
                return this;
            }

            /**
             * A single SOP instance within the series, e.g. an image, or presentation state.
             * 
             * <p>Adds new element(s) to the existing list.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param instance
             *     A single SOP instance from the series
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder instance(Instance... instance) {
                for (Instance value : instance) {
                    this.instance.add(value);
                }
                return this;
            }

            /**
             * A single SOP instance within the series, e.g. an image, or presentation state.
             * 
             * <p>Replaces the existing list with a new one containing elements from the Collection.
             * If any of the elements are null, calling {@link #build()} will fail.
             * 
             * @param instance
             *     A single SOP instance from the series
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @throws NullPointerException
             *     If the passed collection is null
             */
            public Builder instance(Collection<Instance> instance) {
                this.instance = new ArrayList<>(instance);
                return this;
            }

            /**
             * Build the {@link Series}
             * 
             * <p>Required elements:
             * <ul>
             * <li>uid</li>
             * <li>modality</li>
             * </ul>
             * 
             * @return
             *     An immutable object of type {@link Series}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Series per the base specification
             */
            @Override
            public Series build() {
                Series series = new Series(this);
                if (validating) {
                    validate(series);
                }
                return series;
            }

            protected void validate(Series series) {
                super.validate(series);
                ValidationSupport.requireNonNull(series.uid, "uid");
                ValidationSupport.requireNonNull(series.modality, "modality");
                ValidationSupport.checkList(series.endpoint, "endpoint", Reference.class);
                ValidationSupport.checkList(series.specimen, "specimen", Reference.class);
                ValidationSupport.checkList(series.performer, "performer", Performer.class);
                ValidationSupport.checkList(series.instance, "instance", Instance.class);
                ValidationSupport.checkReferenceType(series.endpoint, "endpoint", "Endpoint");
                ValidationSupport.checkReferenceType(series.specimen, "specimen", "Specimen");
                ValidationSupport.requireValueOrChildren(series);
            }

            protected Builder from(Series series) {
                super.from(series);
                uid = series.uid;
                number = series.number;
                modality = series.modality;
                description = series.description;
                numberOfInstances = series.numberOfInstances;
                endpoint.addAll(series.endpoint);
                bodySite = series.bodySite;
                laterality = series.laterality;
                specimen.addAll(series.specimen);
                started = series.started;
                performer.addAll(series.performer);
                instance.addAll(series.instance);
                return this;
            }
        }

        /**
         * Indicates who or what performed the series and how they were involved.
         */
        public static class Performer extends BackboneElement {
            @Summary
            @Binding(
                bindingName = "EventPerformerFunction",
                strength = BindingStrength.Value.EXTENSIBLE,
                description = "The type of involvement of the performer.",
                valueSet = "http://hl7.org/fhir/ValueSet/series-performer-function"
            )
            private final CodeableConcept function;
            @Summary
            @ReferenceTarget({ "Practitioner", "PractitionerRole", "Organization", "CareTeam", "Patient", "Device", "RelatedPerson", "HealthcareService" })
            @Required
            private final Reference actor;

            private Performer(Builder builder) {
                super(builder);
                function = builder.function;
                actor = builder.actor;
            }

            /**
             * Distinguishes the type of involvement of the performer in the series.
             * 
             * @return
             *     An immutable object of type {@link CodeableConcept} that may be null.
             */
            public CodeableConcept getFunction() {
                return function;
            }

            /**
             * Indicates who or what performed the series.
             * 
             * @return
             *     An immutable object of type {@link Reference} that is non-null.
             */
            public Reference getActor() {
                return actor;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (function != null) || 
                    (actor != null);
            }

            @Override
            public void accept(java.lang.String elementName, int elementIndex, Visitor visitor) {
                if (visitor.preVisit(this)) {
                    visitor.visitStart(elementName, elementIndex, this);
                    if (visitor.visit(elementName, elementIndex, this)) {
                        // visit children
                        accept(id, "id", visitor);
                        accept(extension, "extension", visitor, Extension.class);
                        accept(modifierExtension, "modifierExtension", visitor, Extension.class);
                        accept(function, "function", visitor);
                        accept(actor, "actor", visitor);
                    }
                    visitor.visitEnd(elementName, elementIndex, this);
                    visitor.postVisit(this);
                }
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
                Performer other = (Performer) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(function, other.function) && 
                    Objects.equals(actor, other.actor);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        function, 
                        actor);
                    hashCode = result;
                }
                return result;
            }

            @Override
            public Builder toBuilder() {
                return new Builder().from(this);
            }

            public static Builder builder() {
                return new Builder();
            }

            public static class Builder extends BackboneElement.Builder {
                private CodeableConcept function;
                private Reference actor;

                private Builder() {
                    super();
                }

                /**
                 * Unique id for the element within a resource (for internal references). This may be any string value that does not 
                 * contain spaces.
                 * 
                 * @param id
                 *     Unique id for inter-element referencing
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                @Override
                public Builder id(java.lang.String id) {
                    return (Builder) super.id(id);
                }

                /**
                 * May be used to represent additional information that is not part of the basic definition of the element. To make the 
                 * use of extensions safe and managable, there is a strict set of governance applied to the definition and use of 
                 * extensions. Though any implementer can define an extension, there is a set of requirements that SHALL be met as part 
                 * of the definition of the extension.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param extension
                 *     Additional content defined by implementations
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                @Override
                public Builder extension(Extension... extension) {
                    return (Builder) super.extension(extension);
                }

                /**
                 * May be used to represent additional information that is not part of the basic definition of the element. To make the 
                 * use of extensions safe and managable, there is a strict set of governance applied to the definition and use of 
                 * extensions. Though any implementer can define an extension, there is a set of requirements that SHALL be met as part 
                 * of the definition of the extension.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param extension
                 *     Additional content defined by implementations
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                @Override
                public Builder extension(Collection<Extension> extension) {
                    return (Builder) super.extension(extension);
                }

                /**
                 * May be used to represent additional information that is not part of the basic definition of the element and that 
                 * modifies the understanding of the element in which it is contained and/or the understanding of the containing 
                 * element's descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe 
                 * and managable, there is a strict set of governance applied to the definition and use of extensions. Though any 
                 * implementer can define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
                 * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
                 * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
                 * modifierExtension itself).
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param modifierExtension
                 *     Extensions that cannot be ignored even if unrecognized
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                @Override
                public Builder modifierExtension(Extension... modifierExtension) {
                    return (Builder) super.modifierExtension(modifierExtension);
                }

                /**
                 * May be used to represent additional information that is not part of the basic definition of the element and that 
                 * modifies the understanding of the element in which it is contained and/or the understanding of the containing 
                 * element's descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe 
                 * and managable, there is a strict set of governance applied to the definition and use of extensions. Though any 
                 * implementer can define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
                 * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
                 * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
                 * modifierExtension itself).
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param modifierExtension
                 *     Extensions that cannot be ignored even if unrecognized
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                @Override
                public Builder modifierExtension(Collection<Extension> modifierExtension) {
                    return (Builder) super.modifierExtension(modifierExtension);
                }

                /**
                 * Distinguishes the type of involvement of the performer in the series.
                 * 
                 * @param function
                 *     Type of performance
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder function(CodeableConcept function) {
                    this.function = function;
                    return this;
                }

                /**
                 * Indicates who or what performed the series.
                 * 
                 * <p>This element is required.
                 * 
                 * <p>Allowed resource types for this reference:
                 * <ul>
                 * <li>{@link Practitioner}</li>
                 * <li>{@link PractitionerRole}</li>
                 * <li>{@link Organization}</li>
                 * <li>{@link CareTeam}</li>
                 * <li>{@link Patient}</li>
                 * <li>{@link Device}</li>
                 * <li>{@link RelatedPerson}</li>
                 * <li>{@link HealthcareService}</li>
                 * </ul>
                 * 
                 * @param actor
                 *     Who performed the series
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder actor(Reference actor) {
                    this.actor = actor;
                    return this;
                }

                /**
                 * Build the {@link Performer}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>actor</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link Performer}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Performer per the base specification
                 */
                @Override
                public Performer build() {
                    Performer performer = new Performer(this);
                    if (validating) {
                        validate(performer);
                    }
                    return performer;
                }

                protected void validate(Performer performer) {
                    super.validate(performer);
                    ValidationSupport.requireNonNull(performer.actor, "actor");
                    ValidationSupport.checkReferenceType(performer.actor, "actor", "Practitioner", "PractitionerRole", "Organization", "CareTeam", "Patient", "Device", "RelatedPerson", "HealthcareService");
                    ValidationSupport.requireValueOrChildren(performer);
                }

                protected Builder from(Performer performer) {
                    super.from(performer);
                    function = performer.function;
                    actor = performer.actor;
                    return this;
                }
            }
        }

        /**
         * A single SOP instance within the series, e.g. an image, or presentation state.
         */
        public static class Instance extends BackboneElement {
            @Required
            private final Id uid;
            @Binding(
                bindingName = "sopClass",
                strength = BindingStrength.Value.EXTENSIBLE,
                description = "The sopClass for the instance.",
                valueSet = "http://dicom.nema.org/medical/dicom/current/output/chtml/part04/sect_B.5.html#table_B.5-1"
            )
            @Required
            private final Coding sopClass;
            private final UnsignedInt number;
            private final String title;

            private Instance(Builder builder) {
                super(builder);
                uid = builder.uid;
                sopClass = builder.sopClass;
                number = builder.number;
                title = builder.title;
            }

            /**
             * The DICOM SOP Instance UID for this image or other DICOM content.
             * 
             * @return
             *     An immutable object of type {@link Id} that is non-null.
             */
            public Id getUid() {
                return uid;
            }

            /**
             * DICOM instance type.
             * 
             * @return
             *     An immutable object of type {@link Coding} that is non-null.
             */
            public Coding getSopClass() {
                return sopClass;
            }

            /**
             * The number of instance in the series.
             * 
             * @return
             *     An immutable object of type {@link UnsignedInt} that may be null.
             */
            public UnsignedInt getNumber() {
                return number;
            }

            /**
             * The description of the instance.
             * 
             * @return
             *     An immutable object of type {@link String} that may be null.
             */
            public String getTitle() {
                return title;
            }

            @Override
            public boolean hasChildren() {
                return super.hasChildren() || 
                    (uid != null) || 
                    (sopClass != null) || 
                    (number != null) || 
                    (title != null);
            }

            @Override
            public void accept(java.lang.String elementName, int elementIndex, Visitor visitor) {
                if (visitor.preVisit(this)) {
                    visitor.visitStart(elementName, elementIndex, this);
                    if (visitor.visit(elementName, elementIndex, this)) {
                        // visit children
                        accept(id, "id", visitor);
                        accept(extension, "extension", visitor, Extension.class);
                        accept(modifierExtension, "modifierExtension", visitor, Extension.class);
                        accept(uid, "uid", visitor);
                        accept(sopClass, "sopClass", visitor);
                        accept(number, "number", visitor);
                        accept(title, "title", visitor);
                    }
                    visitor.visitEnd(elementName, elementIndex, this);
                    visitor.postVisit(this);
                }
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
                Instance other = (Instance) obj;
                return Objects.equals(id, other.id) && 
                    Objects.equals(extension, other.extension) && 
                    Objects.equals(modifierExtension, other.modifierExtension) && 
                    Objects.equals(uid, other.uid) && 
                    Objects.equals(sopClass, other.sopClass) && 
                    Objects.equals(number, other.number) && 
                    Objects.equals(title, other.title);
            }

            @Override
            public int hashCode() {
                int result = hashCode;
                if (result == 0) {
                    result = Objects.hash(id, 
                        extension, 
                        modifierExtension, 
                        uid, 
                        sopClass, 
                        number, 
                        title);
                    hashCode = result;
                }
                return result;
            }

            @Override
            public Builder toBuilder() {
                return new Builder().from(this);
            }

            public static Builder builder() {
                return new Builder();
            }

            public static class Builder extends BackboneElement.Builder {
                private Id uid;
                private Coding sopClass;
                private UnsignedInt number;
                private String title;

                private Builder() {
                    super();
                }

                /**
                 * Unique id for the element within a resource (for internal references). This may be any string value that does not 
                 * contain spaces.
                 * 
                 * @param id
                 *     Unique id for inter-element referencing
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                @Override
                public Builder id(java.lang.String id) {
                    return (Builder) super.id(id);
                }

                /**
                 * May be used to represent additional information that is not part of the basic definition of the element. To make the 
                 * use of extensions safe and managable, there is a strict set of governance applied to the definition and use of 
                 * extensions. Though any implementer can define an extension, there is a set of requirements that SHALL be met as part 
                 * of the definition of the extension.
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param extension
                 *     Additional content defined by implementations
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                @Override
                public Builder extension(Extension... extension) {
                    return (Builder) super.extension(extension);
                }

                /**
                 * May be used to represent additional information that is not part of the basic definition of the element. To make the 
                 * use of extensions safe and managable, there is a strict set of governance applied to the definition and use of 
                 * extensions. Though any implementer can define an extension, there is a set of requirements that SHALL be met as part 
                 * of the definition of the extension.
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param extension
                 *     Additional content defined by implementations
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                @Override
                public Builder extension(Collection<Extension> extension) {
                    return (Builder) super.extension(extension);
                }

                /**
                 * May be used to represent additional information that is not part of the basic definition of the element and that 
                 * modifies the understanding of the element in which it is contained and/or the understanding of the containing 
                 * element's descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe 
                 * and managable, there is a strict set of governance applied to the definition and use of extensions. Though any 
                 * implementer can define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
                 * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
                 * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
                 * modifierExtension itself).
                 * 
                 * <p>Adds new element(s) to the existing list.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param modifierExtension
                 *     Extensions that cannot be ignored even if unrecognized
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                @Override
                public Builder modifierExtension(Extension... modifierExtension) {
                    return (Builder) super.modifierExtension(modifierExtension);
                }

                /**
                 * May be used to represent additional information that is not part of the basic definition of the element and that 
                 * modifies the understanding of the element in which it is contained and/or the understanding of the containing 
                 * element's descendants. Usually modifier elements provide negation or qualification. To make the use of extensions safe 
                 * and managable, there is a strict set of governance applied to the definition and use of extensions. Though any 
                 * implementer can define an extension, there is a set of requirements that SHALL be met as part of the definition of the 
                 * extension. Applications processing a resource are required to check for modifier extensions.

Modifier extensions 
                 * SHALL NOT change the meaning of any elements on Resource or DomainResource (including cannot change the meaning of 
                 * modifierExtension itself).
                 * 
                 * <p>Replaces the existing list with a new one containing elements from the Collection.
                 * If any of the elements are null, calling {@link #build()} will fail.
                 * 
                 * @param modifierExtension
                 *     Extensions that cannot be ignored even if unrecognized
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @throws NullPointerException
                 *     If the passed collection is null
                 */
                @Override
                public Builder modifierExtension(Collection<Extension> modifierExtension) {
                    return (Builder) super.modifierExtension(modifierExtension);
                }

                /**
                 * The DICOM SOP Instance UID for this image or other DICOM content.
                 * 
                 * <p>This element is required.
                 * 
                 * @param uid
                 *     DICOM SOP Instance UID
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder uid(Id uid) {
                    this.uid = uid;
                    return this;
                }

                /**
                 * DICOM instance type.
                 * 
                 * <p>This element is required.
                 * 
                 * @param sopClass
                 *     DICOM class type
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder sopClass(Coding sopClass) {
                    this.sopClass = sopClass;
                    return this;
                }

                /**
                 * The number of instance in the series.
                 * 
                 * @param number
                 *     The number of this instance in the series
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder number(UnsignedInt number) {
                    this.number = number;
                    return this;
                }

                /**
                 * Convenience method for setting {@code title}.
                 * 
                 * @param title
                 *     Description of instance
                 * 
                 * @return
                 *     A reference to this Builder instance
                 * 
                 * @see #title(org.linuxforhealth.fhir.model.type.String)
                 */
                public Builder title(java.lang.String title) {
                    this.title = (title == null) ? null : String.of(title);
                    return this;
                }

                /**
                 * The description of the instance.
                 * 
                 * @param title
                 *     Description of instance
                 * 
                 * @return
                 *     A reference to this Builder instance
                 */
                public Builder title(String title) {
                    this.title = title;
                    return this;
                }

                /**
                 * Build the {@link Instance}
                 * 
                 * <p>Required elements:
                 * <ul>
                 * <li>uid</li>
                 * <li>sopClass</li>
                 * </ul>
                 * 
                 * @return
                 *     An immutable object of type {@link Instance}
                 * @throws IllegalStateException
                 *     if the current state cannot be built into a valid Instance per the base specification
                 */
                @Override
                public Instance build() {
                    Instance instance = new Instance(this);
                    if (validating) {
                        validate(instance);
                    }
                    return instance;
                }

                protected void validate(Instance instance) {
                    super.validate(instance);
                    ValidationSupport.requireNonNull(instance.uid, "uid");
                    ValidationSupport.requireNonNull(instance.sopClass, "sopClass");
                    ValidationSupport.requireValueOrChildren(instance);
                }

                protected Builder from(Instance instance) {
                    super.from(instance);
                    uid = instance.uid;
                    sopClass = instance.sopClass;
                    number = instance.number;
                    title = instance.title;
                    return this;
                }
            }
        }
    }
}
