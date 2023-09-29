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
import org.linuxforhealth.fhir.model.annotation.Choice;
import org.linuxforhealth.fhir.model.r5.annotation.Maturity;
import org.linuxforhealth.fhir.model.annotation.ReferenceTarget;
import org.linuxforhealth.fhir.model.annotation.Required;
import org.linuxforhealth.fhir.model.annotation.Summary;
import org.linuxforhealth.fhir.model.r5.type.Annotation;
import org.linuxforhealth.fhir.model.r5.type.BackboneElement;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Canonical;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.CodeableConcept;
import org.linuxforhealth.fhir.model.r5.type.CodeableReference;
import org.linuxforhealth.fhir.model.r5.type.DateTime;
import org.linuxforhealth.fhir.model.r5.type.Element;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.Identifier;
import org.linuxforhealth.fhir.model.r5.type.Integer;
import org.linuxforhealth.fhir.model.r5.type.Meta;
import org.linuxforhealth.fhir.model.r5.type.Narrative;
import org.linuxforhealth.fhir.model.r5.type.Period;
import org.linuxforhealth.fhir.model.r5.type.Quantity;
import org.linuxforhealth.fhir.model.r5.type.Range;
import org.linuxforhealth.fhir.model.r5.type.Reference;
import org.linuxforhealth.fhir.model.r5.type.Timing;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.type.code.BindingStrength;
import org.linuxforhealth.fhir.model.r5.type.code.DeviceRequestStatus;
import org.linuxforhealth.fhir.model.r5.type.code.RequestIntent;
import org.linuxforhealth.fhir.model.r5.type.code.RequestPriority;
import org.linuxforhealth.fhir.model.r5.type.code.StandardsStatus;
import org.linuxforhealth.fhir.model.r5.util.ValidationSupport;
import org.linuxforhealth.fhir.model.r5.visitor.Visitor;

/**
 * Represents a request for a patient to employ a medical device. The device may be an implantable device, or an external 
 * assistive device, such as a walker.
 * 
 * <p>Maturity level: FMM1 (Trial Use)
 */
@Maturity(
    level = 1,
    status = StandardsStatus.Value.TRIAL_USE
)
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class DeviceRequest extends DomainResource {
    @Summary
    private final List<Identifier> identifier;
    @Summary
    private final List<Canonical> instantiatesCanonical;
    @Summary
    private final List<Uri> instantiatesUri;
    @Summary
    private final List<Reference> basedOn;
    @Summary
    @ReferenceTarget({ "DeviceRequest" })
    private final List<Reference> replaces;
    @Summary
    private final Identifier groupIdentifier;
    @Summary
    @Binding(
        bindingName = "DeviceRequestStatus",
        strength = BindingStrength.Value.REQUIRED,
        description = "Codes representing the status of the request.",
        valueSet = "http://hl7.org/fhir/ValueSet/request-status|5.0.0"
    )
    private final DeviceRequestStatus status;
    @Summary
    @Binding(
        bindingName = "RequestIntent",
        strength = BindingStrength.Value.REQUIRED,
        description = "The kind of diagnostic request.",
        valueSet = "http://hl7.org/fhir/ValueSet/request-intent|5.0.0"
    )
    @Required
    private final RequestIntent intent;
    @Summary
    @Binding(
        bindingName = "RequestPriority",
        strength = BindingStrength.Value.REQUIRED,
        description = "Identifies the level of importance to be assigned to actioning the request.",
        valueSet = "http://hl7.org/fhir/ValueSet/request-priority|5.0.0"
    )
    private final RequestPriority priority;
    @Summary
    private final Boolean doNotPerform;
    @Summary
    @Binding(
        bindingName = "DeviceRequestCode",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Codes for devices that can be requested.",
        valueSet = "http://hl7.org/fhir/ValueSet/device-type"
    )
    @Required
    private final CodeableReference code;
    private final Integer quantity;
    private final List<Parameter> parameter;
    @Summary
    @ReferenceTarget({ "Patient", "Group", "Location", "Device" })
    @Required
    private final Reference subject;
    @Summary
    @ReferenceTarget({ "Encounter" })
    private final Reference encounter;
    @Summary
    @Choice({ DateTime.class, Period.class, Timing.class })
    private final Element occurrence;
    @Summary
    private final DateTime authoredOn;
    @Summary
    @ReferenceTarget({ "Device", "Practitioner", "PractitionerRole", "Organization" })
    private final Reference requester;
    @Summary
    private final CodeableReference performer;
    @Summary
    @Binding(
        bindingName = "DeviceRequestReason",
        strength = BindingStrength.Value.EXAMPLE,
        description = "Diagnosis or problem codes justifying the reason for requesting the device.",
        valueSet = "http://hl7.org/fhir/ValueSet/condition-code"
    )
    private final List<CodeableReference> reason;
    private final Boolean asNeeded;
    private final CodeableConcept asNeededFor;
    @ReferenceTarget({ "Coverage", "ClaimResponse" })
    private final List<Reference> insurance;
    private final List<Reference> supportingInfo;
    private final List<Annotation> note;
    @ReferenceTarget({ "Provenance" })
    private final List<Reference> relevantHistory;

    private DeviceRequest(Builder builder) {
        super(builder);
        identifier = Collections.unmodifiableList(builder.identifier);
        instantiatesCanonical = Collections.unmodifiableList(builder.instantiatesCanonical);
        instantiatesUri = Collections.unmodifiableList(builder.instantiatesUri);
        basedOn = Collections.unmodifiableList(builder.basedOn);
        replaces = Collections.unmodifiableList(builder.replaces);
        groupIdentifier = builder.groupIdentifier;
        status = builder.status;
        intent = builder.intent;
        priority = builder.priority;
        doNotPerform = builder.doNotPerform;
        code = builder.code;
        quantity = builder.quantity;
        parameter = Collections.unmodifiableList(builder.parameter);
        subject = builder.subject;
        encounter = builder.encounter;
        occurrence = builder.occurrence;
        authoredOn = builder.authoredOn;
        requester = builder.requester;
        performer = builder.performer;
        reason = Collections.unmodifiableList(builder.reason);
        asNeeded = builder.asNeeded;
        asNeededFor = builder.asNeededFor;
        insurance = Collections.unmodifiableList(builder.insurance);
        supportingInfo = Collections.unmodifiableList(builder.supportingInfo);
        note = Collections.unmodifiableList(builder.note);
        relevantHistory = Collections.unmodifiableList(builder.relevantHistory);
    }

    /**
     * Identifiers assigned to this order by the orderer or by the receiver.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Identifier} that may be empty.
     */
    public List<Identifier> getIdentifier() {
        return identifier;
    }

    /**
     * The URL pointing to a FHIR-defined protocol, guideline, orderset or other definition that is adhered to in whole or in 
     * part by this DeviceRequest.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Canonical} that may be empty.
     */
    public List<Canonical> getInstantiatesCanonical() {
        return instantiatesCanonical;
    }

    /**
     * The URL pointing to an externally maintained protocol, guideline, orderset or other definition that is adhered to in 
     * whole or in part by this DeviceRequest.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Uri} that may be empty.
     */
    public List<Uri> getInstantiatesUri() {
        return instantiatesUri;
    }

    /**
     * Plan/proposal/order fulfilled by this request.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getBasedOn() {
        return basedOn;
    }

    /**
     * The request takes the place of the referenced completed or terminated request(s).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getReplaces() {
        return replaces;
    }

    /**
     * A shared identifier common to multiple independent Request instances that were activated/authorized more or less 
     * simultaneously by a single author. The presence of the same identifier on each request ties those requests together 
     * and may have business ramifications in terms of reporting of results, billing, etc. E.g. a requisition number shared 
     * by a set of lab tests ordered together, or a prescription number shared by all meds ordered at one time.
     * 
     * @return
     *     An immutable object of type {@link Identifier} that may be null.
     */
    public Identifier getGroupIdentifier() {
        return groupIdentifier;
    }

    /**
     * The status of the request.
     * 
     * @return
     *     An immutable object of type {@link DeviceRequestStatus} that may be null.
     */
    public DeviceRequestStatus getStatus() {
        return status;
    }

    /**
     * Whether the request is a proposal, plan, an original order or a reflex order.
     * 
     * @return
     *     An immutable object of type {@link RequestIntent} that is non-null.
     */
    public RequestIntent getIntent() {
        return intent;
    }

    /**
     * Indicates how quickly the request should be addressed with respect to other requests.
     * 
     * @return
     *     An immutable object of type {@link RequestPriority} that may be null.
     */
    public RequestPriority getPriority() {
        return priority;
    }

    /**
     * If true, indicates that the provider is asking for the patient to either stop using or to not start using the 
     * specified device or category of devices. For example, the patient has undergone surgery and the provider is indicating 
     * that the patient should not wear contact lenses.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getDoNotPerform() {
        return doNotPerform;
    }

    /**
     * The details of the device to be used.
     * 
     * @return
     *     An immutable object of type {@link CodeableReference} that is non-null.
     */
    public CodeableReference getCode() {
        return code;
    }

    /**
     * The number of devices to be provided.
     * 
     * @return
     *     An immutable object of type {@link Integer} that may be null.
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Specific parameters for the ordered item. For example, the prism value for lenses.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Parameter} that may be empty.
     */
    public List<Parameter> getParameter() {
        return parameter;
    }

    /**
     * The patient who will use the device.
     * 
     * @return
     *     An immutable object of type {@link Reference} that is non-null.
     */
    public Reference getSubject() {
        return subject;
    }

    /**
     * An encounter that provides additional context in which this request is made.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getEncounter() {
        return encounter;
    }

    /**
     * The timing schedule for the use of the device. The Schedule data type allows many different expressions, for example. 
     * "Every 8 hours"; "Three times a day"; "1/2 an hour before breakfast for 10 days from 23-Dec 2011:"; "15 Oct 2013, 17 
     * Oct 2013 and 1 Nov 2013".
     * 
     * @return
     *     An immutable object of type {@link DateTime}, {@link Period} or {@link Timing} that may be null.
     */
    public Element getOccurrence() {
        return occurrence;
    }

    /**
     * When the request transitioned to being actionable.
     * 
     * @return
     *     An immutable object of type {@link DateTime} that may be null.
     */
    public DateTime getAuthoredOn() {
        return authoredOn;
    }

    /**
     * The individual or entity who initiated the request and has responsibility for its activation.
     * 
     * @return
     *     An immutable object of type {@link Reference} that may be null.
     */
    public Reference getRequester() {
        return requester;
    }

    /**
     * The desired individual or entity to provide the device to the subject of the request (e.g., patient, location).
     * 
     * @return
     *     An immutable object of type {@link CodeableReference} that may be null.
     */
    public CodeableReference getPerformer() {
        return performer;
    }

    /**
     * Reason or justification for the use of this device.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link CodeableReference} that may be empty.
     */
    public List<CodeableReference> getReason() {
        return reason;
    }

    /**
     * This status is to indicate whether the request is a PRN or may be given as needed.
     * 
     * @return
     *     An immutable object of type {@link Boolean} that may be null.
     */
    public Boolean getAsNeeded() {
        return asNeeded;
    }

    /**
     * The reason for using the device.
     * 
     * @return
     *     An immutable object of type {@link CodeableConcept} that may be null.
     */
    public CodeableConcept getAsNeededFor() {
        return asNeededFor;
    }

    /**
     * Insurance plans, coverage extensions, pre-authorizations and/or pre-determinations that may be required for delivering 
     * the requested service.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getInsurance() {
        return insurance;
    }

    /**
     * Additional clinical information about the patient that may influence the request fulfilment. For example, this may 
     * include where on the subject's body the device will be used (i.e. the target site).
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getSupportingInfo() {
        return supportingInfo;
    }

    /**
     * Details about this request that were not represented at all or sufficiently in one of the attributes provided in a 
     * class. These may include for example a comment, an instruction, or a note associated with the statement.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Annotation} that may be empty.
     */
    public List<Annotation> getNote() {
        return note;
    }

    /**
     * Key events in the history of the request.
     * 
     * @return
     *     An unmodifiable list containing immutable objects of type {@link Reference} that may be empty.
     */
    public List<Reference> getRelevantHistory() {
        return relevantHistory;
    }

    @Override
    public boolean hasChildren() {
        return super.hasChildren() || 
            !identifier.isEmpty() || 
            !instantiatesCanonical.isEmpty() || 
            !instantiatesUri.isEmpty() || 
            !basedOn.isEmpty() || 
            !replaces.isEmpty() || 
            (groupIdentifier != null) || 
            (status != null) || 
            (intent != null) || 
            (priority != null) || 
            (doNotPerform != null) || 
            (code != null) || 
            (quantity != null) || 
            !parameter.isEmpty() || 
            (subject != null) || 
            (encounter != null) || 
            (occurrence != null) || 
            (authoredOn != null) || 
            (requester != null) || 
            (performer != null) || 
            !reason.isEmpty() || 
            (asNeeded != null) || 
            (asNeededFor != null) || 
            !insurance.isEmpty() || 
            !supportingInfo.isEmpty() || 
            !note.isEmpty() || 
            !relevantHistory.isEmpty();
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
                accept(instantiatesCanonical, "instantiatesCanonical", visitor, Canonical.class);
                accept(instantiatesUri, "instantiatesUri", visitor, Uri.class);
                accept(basedOn, "basedOn", visitor, Reference.class);
                accept(replaces, "replaces", visitor, Reference.class);
                accept(groupIdentifier, "groupIdentifier", visitor);
                accept(status, "status", visitor);
                accept(intent, "intent", visitor);
                accept(priority, "priority", visitor);
                accept(doNotPerform, "doNotPerform", visitor);
                accept(code, "code", visitor);
                accept(quantity, "quantity", visitor);
                accept(parameter, "parameter", visitor, Parameter.class);
                accept(subject, "subject", visitor);
                accept(encounter, "encounter", visitor);
                accept(occurrence, "occurrence", visitor);
                accept(authoredOn, "authoredOn", visitor);
                accept(requester, "requester", visitor);
                accept(performer, "performer", visitor);
                accept(reason, "reason", visitor, CodeableReference.class);
                accept(asNeeded, "asNeeded", visitor);
                accept(asNeededFor, "asNeededFor", visitor);
                accept(insurance, "insurance", visitor, Reference.class);
                accept(supportingInfo, "supportingInfo", visitor, Reference.class);
                accept(note, "note", visitor, Annotation.class);
                accept(relevantHistory, "relevantHistory", visitor, Reference.class);
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
        DeviceRequest other = (DeviceRequest) obj;
        return Objects.equals(id, other.id) && 
            Objects.equals(meta, other.meta) && 
            Objects.equals(implicitRules, other.implicitRules) && 
            Objects.equals(language, other.language) && 
            Objects.equals(text, other.text) && 
            Objects.equals(contained, other.contained) && 
            Objects.equals(extension, other.extension) && 
            Objects.equals(modifierExtension, other.modifierExtension) && 
            Objects.equals(identifier, other.identifier) && 
            Objects.equals(instantiatesCanonical, other.instantiatesCanonical) && 
            Objects.equals(instantiatesUri, other.instantiatesUri) && 
            Objects.equals(basedOn, other.basedOn) && 
            Objects.equals(replaces, other.replaces) && 
            Objects.equals(groupIdentifier, other.groupIdentifier) && 
            Objects.equals(status, other.status) && 
            Objects.equals(intent, other.intent) && 
            Objects.equals(priority, other.priority) && 
            Objects.equals(doNotPerform, other.doNotPerform) && 
            Objects.equals(code, other.code) && 
            Objects.equals(quantity, other.quantity) && 
            Objects.equals(parameter, other.parameter) && 
            Objects.equals(subject, other.subject) && 
            Objects.equals(encounter, other.encounter) && 
            Objects.equals(occurrence, other.occurrence) && 
            Objects.equals(authoredOn, other.authoredOn) && 
            Objects.equals(requester, other.requester) && 
            Objects.equals(performer, other.performer) && 
            Objects.equals(reason, other.reason) && 
            Objects.equals(asNeeded, other.asNeeded) && 
            Objects.equals(asNeededFor, other.asNeededFor) && 
            Objects.equals(insurance, other.insurance) && 
            Objects.equals(supportingInfo, other.supportingInfo) && 
            Objects.equals(note, other.note) && 
            Objects.equals(relevantHistory, other.relevantHistory);
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
                instantiatesCanonical, 
                instantiatesUri, 
                basedOn, 
                replaces, 
                groupIdentifier, 
                status, 
                intent, 
                priority, 
                doNotPerform, 
                code, 
                quantity, 
                parameter, 
                subject, 
                encounter, 
                occurrence, 
                authoredOn, 
                requester, 
                performer, 
                reason, 
                asNeeded, 
                asNeededFor, 
                insurance, 
                supportingInfo, 
                note, 
                relevantHistory);
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
        private List<Canonical> instantiatesCanonical = new ArrayList<>();
        private List<Uri> instantiatesUri = new ArrayList<>();
        private List<Reference> basedOn = new ArrayList<>();
        private List<Reference> replaces = new ArrayList<>();
        private Identifier groupIdentifier;
        private DeviceRequestStatus status;
        private RequestIntent intent;
        private RequestPriority priority;
        private Boolean doNotPerform;
        private CodeableReference code;
        private Integer quantity;
        private List<Parameter> parameter = new ArrayList<>();
        private Reference subject;
        private Reference encounter;
        private Element occurrence;
        private DateTime authoredOn;
        private Reference requester;
        private CodeableReference performer;
        private List<CodeableReference> reason = new ArrayList<>();
        private Boolean asNeeded;
        private CodeableConcept asNeededFor;
        private List<Reference> insurance = new ArrayList<>();
        private List<Reference> supportingInfo = new ArrayList<>();
        private List<Annotation> note = new ArrayList<>();
        private List<Reference> relevantHistory = new ArrayList<>();

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
         * Identifiers assigned to this order by the orderer or by the receiver.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     External Request identifier
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
         * Identifiers assigned to this order by the orderer or by the receiver.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param identifier
         *     External Request identifier
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
         * The URL pointing to a FHIR-defined protocol, guideline, orderset or other definition that is adhered to in whole or in 
         * part by this DeviceRequest.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param instantiatesCanonical
         *     Instantiates FHIR protocol or definition
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder instantiatesCanonical(Canonical... instantiatesCanonical) {
            for (Canonical value : instantiatesCanonical) {
                this.instantiatesCanonical.add(value);
            }
            return this;
        }

        /**
         * The URL pointing to a FHIR-defined protocol, guideline, orderset or other definition that is adhered to in whole or in 
         * part by this DeviceRequest.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param instantiatesCanonical
         *     Instantiates FHIR protocol or definition
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder instantiatesCanonical(Collection<Canonical> instantiatesCanonical) {
            this.instantiatesCanonical = new ArrayList<>(instantiatesCanonical);
            return this;
        }

        /**
         * The URL pointing to an externally maintained protocol, guideline, orderset or other definition that is adhered to in 
         * whole or in part by this DeviceRequest.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param instantiatesUri
         *     Instantiates external protocol or definition
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder instantiatesUri(Uri... instantiatesUri) {
            for (Uri value : instantiatesUri) {
                this.instantiatesUri.add(value);
            }
            return this;
        }

        /**
         * The URL pointing to an externally maintained protocol, guideline, orderset or other definition that is adhered to in 
         * whole or in part by this DeviceRequest.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param instantiatesUri
         *     Instantiates external protocol or definition
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder instantiatesUri(Collection<Uri> instantiatesUri) {
            this.instantiatesUri = new ArrayList<>(instantiatesUri);
            return this;
        }

        /**
         * Plan/proposal/order fulfilled by this request.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param basedOn
         *     What request fulfills
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
         * Plan/proposal/order fulfilled by this request.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param basedOn
         *     What request fulfills
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
         * The request takes the place of the referenced completed or terminated request(s).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link DeviceRequest}</li>
         * </ul>
         * 
         * @param replaces
         *     What request replaces
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder replaces(Reference... replaces) {
            for (Reference value : replaces) {
                this.replaces.add(value);
            }
            return this;
        }

        /**
         * The request takes the place of the referenced completed or terminated request(s).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link DeviceRequest}</li>
         * </ul>
         * 
         * @param replaces
         *     What request replaces
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder replaces(Collection<Reference> replaces) {
            this.replaces = new ArrayList<>(replaces);
            return this;
        }

        /**
         * A shared identifier common to multiple independent Request instances that were activated/authorized more or less 
         * simultaneously by a single author. The presence of the same identifier on each request ties those requests together 
         * and may have business ramifications in terms of reporting of results, billing, etc. E.g. a requisition number shared 
         * by a set of lab tests ordered together, or a prescription number shared by all meds ordered at one time.
         * 
         * @param groupIdentifier
         *     Identifier of composite request
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder groupIdentifier(Identifier groupIdentifier) {
            this.groupIdentifier = groupIdentifier;
            return this;
        }

        /**
         * The status of the request.
         * 
         * @param status
         *     draft | active | on-hold | revoked | completed | entered-in-error | unknown
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder status(DeviceRequestStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Whether the request is a proposal, plan, an original order or a reflex order.
         * 
         * <p>This element is required.
         * 
         * @param intent
         *     proposal | plan | directive | order | original-order | reflex-order | filler-order | instance-order | option
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder intent(RequestIntent intent) {
            this.intent = intent;
            return this;
        }

        /**
         * Indicates how quickly the request should be addressed with respect to other requests.
         * 
         * @param priority
         *     routine | urgent | asap | stat
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder priority(RequestPriority priority) {
            this.priority = priority;
            return this;
        }

        /**
         * Convenience method for setting {@code doNotPerform}.
         * 
         * @param doNotPerform
         *     True if the request is to stop or not to start using the device
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #doNotPerform(org.linuxforhealth.fhir.model.type.Boolean)
         */
        public Builder doNotPerform(java.lang.Boolean doNotPerform) {
            this.doNotPerform = (doNotPerform == null) ? null : Boolean.of(doNotPerform);
            return this;
        }

        /**
         * If true, indicates that the provider is asking for the patient to either stop using or to not start using the 
         * specified device or category of devices. For example, the patient has undergone surgery and the provider is indicating 
         * that the patient should not wear contact lenses.
         * 
         * @param doNotPerform
         *     True if the request is to stop or not to start using the device
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder doNotPerform(Boolean doNotPerform) {
            this.doNotPerform = doNotPerform;
            return this;
        }

        /**
         * The details of the device to be used.
         * 
         * <p>This element is required.
         * 
         * @param code
         *     Device requested
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder code(CodeableReference code) {
            this.code = code;
            return this;
        }

        /**
         * Convenience method for setting {@code quantity}.
         * 
         * @param quantity
         *     Quantity of devices to supply
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #quantity(org.linuxforhealth.fhir.model.type.Integer)
         */
        public Builder quantity(java.lang.Integer quantity) {
            this.quantity = (quantity == null) ? null : Integer.of(quantity);
            return this;
        }

        /**
         * The number of devices to be provided.
         * 
         * @param quantity
         *     Quantity of devices to supply
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        /**
         * Specific parameters for the ordered item. For example, the prism value for lenses.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param parameter
         *     Device details
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder parameter(Parameter... parameter) {
            for (Parameter value : parameter) {
                this.parameter.add(value);
            }
            return this;
        }

        /**
         * Specific parameters for the ordered item. For example, the prism value for lenses.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param parameter
         *     Device details
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder parameter(Collection<Parameter> parameter) {
            this.parameter = new ArrayList<>(parameter);
            return this;
        }

        /**
         * The patient who will use the device.
         * 
         * <p>This element is required.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Patient}</li>
         * <li>{@link Group}</li>
         * <li>{@link Location}</li>
         * <li>{@link Device}</li>
         * </ul>
         * 
         * @param subject
         *     Focus of request
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder subject(Reference subject) {
            this.subject = subject;
            return this;
        }

        /**
         * An encounter that provides additional context in which this request is made.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Encounter}</li>
         * </ul>
         * 
         * @param encounter
         *     Encounter motivating request
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder encounter(Reference encounter) {
            this.encounter = encounter;
            return this;
        }

        /**
         * The timing schedule for the use of the device. The Schedule data type allows many different expressions, for example. 
         * "Every 8 hours"; "Three times a day"; "1/2 an hour before breakfast for 10 days from 23-Dec 2011:"; "15 Oct 2013, 17 
         * Oct 2013 and 1 Nov 2013".
         * 
         * <p>This is a choice element with the following allowed types:
         * <ul>
         * <li>{@link DateTime}</li>
         * <li>{@link Period}</li>
         * <li>{@link Timing}</li>
         * </ul>
         * 
         * @param occurrence
         *     Desired time or schedule for use
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder occurrence(Element occurrence) {
            this.occurrence = occurrence;
            return this;
        }

        /**
         * When the request transitioned to being actionable.
         * 
         * @param authoredOn
         *     When recorded
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder authoredOn(DateTime authoredOn) {
            this.authoredOn = authoredOn;
            return this;
        }

        /**
         * The individual or entity who initiated the request and has responsibility for its activation.
         * 
         * <p>Allowed resource types for this reference:
         * <ul>
         * <li>{@link Device}</li>
         * <li>{@link Practitioner}</li>
         * <li>{@link PractitionerRole}</li>
         * <li>{@link Organization}</li>
         * </ul>
         * 
         * @param requester
         *     Who/what submitted the device request
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder requester(Reference requester) {
            this.requester = requester;
            return this;
        }

        /**
         * The desired individual or entity to provide the device to the subject of the request (e.g., patient, location).
         * 
         * @param performer
         *     Requested Filler
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder performer(CodeableReference performer) {
            this.performer = performer;
            return this;
        }

        /**
         * Reason or justification for the use of this device.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     Coded/Linked Reason for request
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
         * Reason or justification for the use of this device.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param reason
         *     Coded/Linked Reason for request
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
         * Convenience method for setting {@code asNeeded}.
         * 
         * @param asNeeded
         *     PRN status of request
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @see #asNeeded(org.linuxforhealth.fhir.model.type.Boolean)
         */
        public Builder asNeeded(java.lang.Boolean asNeeded) {
            this.asNeeded = (asNeeded == null) ? null : Boolean.of(asNeeded);
            return this;
        }

        /**
         * This status is to indicate whether the request is a PRN or may be given as needed.
         * 
         * @param asNeeded
         *     PRN status of request
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder asNeeded(Boolean asNeeded) {
            this.asNeeded = asNeeded;
            return this;
        }

        /**
         * The reason for using the device.
         * 
         * @param asNeededFor
         *     Device usage reason
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder asNeededFor(CodeableConcept asNeededFor) {
            this.asNeededFor = asNeededFor;
            return this;
        }

        /**
         * Insurance plans, coverage extensions, pre-authorizations and/or pre-determinations that may be required for delivering 
         * the requested service.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Coverage}</li>
         * <li>{@link ClaimResponse}</li>
         * </ul>
         * 
         * @param insurance
         *     Associated insurance coverage
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder insurance(Reference... insurance) {
            for (Reference value : insurance) {
                this.insurance.add(value);
            }
            return this;
        }

        /**
         * Insurance plans, coverage extensions, pre-authorizations and/or pre-determinations that may be required for delivering 
         * the requested service.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Coverage}</li>
         * <li>{@link ClaimResponse}</li>
         * </ul>
         * 
         * @param insurance
         *     Associated insurance coverage
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder insurance(Collection<Reference> insurance) {
            this.insurance = new ArrayList<>(insurance);
            return this;
        }

        /**
         * Additional clinical information about the patient that may influence the request fulfilment. For example, this may 
         * include where on the subject's body the device will be used (i.e. the target site).
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param supportingInfo
         *     Additional clinical information
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder supportingInfo(Reference... supportingInfo) {
            for (Reference value : supportingInfo) {
                this.supportingInfo.add(value);
            }
            return this;
        }

        /**
         * Additional clinical information about the patient that may influence the request fulfilment. For example, this may 
         * include where on the subject's body the device will be used (i.e. the target site).
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param supportingInfo
         *     Additional clinical information
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder supportingInfo(Collection<Reference> supportingInfo) {
            this.supportingInfo = new ArrayList<>(supportingInfo);
            return this;
        }

        /**
         * Details about this request that were not represented at all or sufficiently in one of the attributes provided in a 
         * class. These may include for example a comment, an instruction, or a note associated with the statement.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Notes or comments
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
         * Details about this request that were not represented at all or sufficiently in one of the attributes provided in a 
         * class. These may include for example a comment, an instruction, or a note associated with the statement.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * @param note
         *     Notes or comments
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
         * Key events in the history of the request.
         * 
         * <p>Adds new element(s) to the existing list.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Provenance}</li>
         * </ul>
         * 
         * @param relevantHistory
         *     Request provenance
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder relevantHistory(Reference... relevantHistory) {
            for (Reference value : relevantHistory) {
                this.relevantHistory.add(value);
            }
            return this;
        }

        /**
         * Key events in the history of the request.
         * 
         * <p>Replaces the existing list with a new one containing elements from the Collection.
         * If any of the elements are null, calling {@link #build()} will fail.
         * 
         * <p>Allowed resource types for the references:
         * <ul>
         * <li>{@link Provenance}</li>
         * </ul>
         * 
         * @param relevantHistory
         *     Request provenance
         * 
         * @return
         *     A reference to this Builder instance
         * 
         * @throws NullPointerException
         *     If the passed collection is null
         */
        public Builder relevantHistory(Collection<Reference> relevantHistory) {
            this.relevantHistory = new ArrayList<>(relevantHistory);
            return this;
        }

        /**
         * Build the {@link DeviceRequest}
         * 
         * <p>Required elements:
         * <ul>
         * <li>intent</li>
         * <li>code</li>
         * <li>subject</li>
         * </ul>
         * 
         * @return
         *     An immutable object of type {@link DeviceRequest}
         * @throws IllegalStateException
         *     if the current state cannot be built into a valid DeviceRequest per the base specification
         */
        @Override
        public DeviceRequest build() {
            DeviceRequest deviceRequest = new DeviceRequest(this);
            if (validating) {
                validate(deviceRequest);
            }
            return deviceRequest;
        }

        protected void validate(DeviceRequest deviceRequest) {
            super.validate(deviceRequest);
            ValidationSupport.checkList(deviceRequest.identifier, "identifier", Identifier.class);
            ValidationSupport.checkList(deviceRequest.instantiatesCanonical, "instantiatesCanonical", Canonical.class);
            ValidationSupport.checkList(deviceRequest.instantiatesUri, "instantiatesUri", Uri.class);
            ValidationSupport.checkList(deviceRequest.basedOn, "basedOn", Reference.class);
            ValidationSupport.checkList(deviceRequest.replaces, "replaces", Reference.class);
            ValidationSupport.requireNonNull(deviceRequest.intent, "intent");
            ValidationSupport.requireNonNull(deviceRequest.code, "code");
            ValidationSupport.checkList(deviceRequest.parameter, "parameter", Parameter.class);
            ValidationSupport.requireNonNull(deviceRequest.subject, "subject");
            ValidationSupport.choiceElement(deviceRequest.occurrence, "occurrence", DateTime.class, Period.class, Timing.class);
            ValidationSupport.checkList(deviceRequest.reason, "reason", CodeableReference.class);
            ValidationSupport.checkList(deviceRequest.insurance, "insurance", Reference.class);
            ValidationSupport.checkList(deviceRequest.supportingInfo, "supportingInfo", Reference.class);
            ValidationSupport.checkList(deviceRequest.note, "note", Annotation.class);
            ValidationSupport.checkList(deviceRequest.relevantHistory, "relevantHistory", Reference.class);
            ValidationSupport.checkReferenceType(deviceRequest.replaces, "replaces", "DeviceRequest");
            ValidationSupport.checkReferenceType(deviceRequest.subject, "subject", "Patient", "Group", "Location", "Device");
            ValidationSupport.checkReferenceType(deviceRequest.encounter, "encounter", "Encounter");
            ValidationSupport.checkReferenceType(deviceRequest.requester, "requester", "Device", "Practitioner", "PractitionerRole", "Organization");
            ValidationSupport.checkReferenceType(deviceRequest.insurance, "insurance", "Coverage", "ClaimResponse");
            ValidationSupport.checkReferenceType(deviceRequest.relevantHistory, "relevantHistory", "Provenance");
        }

        protected Builder from(DeviceRequest deviceRequest) {
            super.from(deviceRequest);
            identifier.addAll(deviceRequest.identifier);
            instantiatesCanonical.addAll(deviceRequest.instantiatesCanonical);
            instantiatesUri.addAll(deviceRequest.instantiatesUri);
            basedOn.addAll(deviceRequest.basedOn);
            replaces.addAll(deviceRequest.replaces);
            groupIdentifier = deviceRequest.groupIdentifier;
            status = deviceRequest.status;
            intent = deviceRequest.intent;
            priority = deviceRequest.priority;
            doNotPerform = deviceRequest.doNotPerform;
            code = deviceRequest.code;
            quantity = deviceRequest.quantity;
            parameter.addAll(deviceRequest.parameter);
            subject = deviceRequest.subject;
            encounter = deviceRequest.encounter;
            occurrence = deviceRequest.occurrence;
            authoredOn = deviceRequest.authoredOn;
            requester = deviceRequest.requester;
            performer = deviceRequest.performer;
            reason.addAll(deviceRequest.reason);
            asNeeded = deviceRequest.asNeeded;
            asNeededFor = deviceRequest.asNeededFor;
            insurance.addAll(deviceRequest.insurance);
            supportingInfo.addAll(deviceRequest.supportingInfo);
            note.addAll(deviceRequest.note);
            relevantHistory.addAll(deviceRequest.relevantHistory);
            return this;
        }
    }

    /**
     * Specific parameters for the ordered item. For example, the prism value for lenses.
     */
    public static class Parameter extends BackboneElement {
        @Binding(
            bindingName = "ParameterCode",
            strength = BindingStrength.Value.EXAMPLE,
            description = "A code that identifies the device detail."
        )
        private final CodeableConcept code;
        @Choice({ CodeableConcept.class, Quantity.class, Range.class, Boolean.class })
        private final Element value;

        private Parameter(Builder builder) {
            super(builder);
            code = builder.code;
            value = builder.value;
        }

        /**
         * A code or string that identifies the device detail being asserted.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept} that may be null.
         */
        public CodeableConcept getCode() {
            return code;
        }

        /**
         * The value of the device detail.
         * 
         * @return
         *     An immutable object of type {@link CodeableConcept}, {@link Quantity}, {@link Range} or {@link Boolean} that may be 
         *     null.
         */
        public Element getValue() {
            return value;
        }

        @Override
        public boolean hasChildren() {
            return super.hasChildren() || 
                (code != null) || 
                (value != null);
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
                    accept(code, "code", visitor);
                    accept(value, "value", visitor);
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
            Parameter other = (Parameter) obj;
            return Objects.equals(id, other.id) && 
                Objects.equals(extension, other.extension) && 
                Objects.equals(modifierExtension, other.modifierExtension) && 
                Objects.equals(code, other.code) && 
                Objects.equals(value, other.value);
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = Objects.hash(id, 
                    extension, 
                    modifierExtension, 
                    code, 
                    value);
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
            private CodeableConcept code;
            private Element value;

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
             * A code or string that identifies the device detail being asserted.
             * 
             * @param code
             *     Device detail
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder code(CodeableConcept code) {
                this.code = code;
                return this;
            }

            /**
             * Convenience method for setting {@code value} with choice type Boolean.
             * 
             * @param value
             *     Value of detail
             * 
             * @return
             *     A reference to this Builder instance
             * 
             * @see #value(Element)
             */
            public Builder value(java.lang.Boolean value) {
                this.value = (value == null) ? null : Boolean.of(value);
                return this;
            }

            /**
             * The value of the device detail.
             * 
             * <p>This is a choice element with the following allowed types:
             * <ul>
             * <li>{@link CodeableConcept}</li>
             * <li>{@link Quantity}</li>
             * <li>{@link Range}</li>
             * <li>{@link Boolean}</li>
             * </ul>
             * 
             * @param value
             *     Value of detail
             * 
             * @return
             *     A reference to this Builder instance
             */
            public Builder value(Element value) {
                this.value = value;
                return this;
            }

            /**
             * Build the {@link Parameter}
             * 
             * @return
             *     An immutable object of type {@link Parameter}
             * @throws IllegalStateException
             *     if the current state cannot be built into a valid Parameter per the base specification
             */
            @Override
            public Parameter build() {
                Parameter parameter = new Parameter(this);
                if (validating) {
                    validate(parameter);
                }
                return parameter;
            }

            protected void validate(Parameter parameter) {
                super.validate(parameter);
                ValidationSupport.choiceElement(parameter.value, "value", CodeableConcept.class, Quantity.class, Range.class, Boolean.class);
                ValidationSupport.requireValueOrChildren(parameter);
            }

            protected Builder from(Parameter parameter) {
                super.from(parameter);
                code = parameter.code;
                value = parameter.value;
                return this;
            }
        }
    }
}
