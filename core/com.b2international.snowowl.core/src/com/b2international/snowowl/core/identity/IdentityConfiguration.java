/*
 * Copyright 2017-2022 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.core.identity;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Identity module configuration. Use to configure the underlying identity services (authentication, authorization, tokens, etc.).
 * 
 * @since 5.11
 */
public class IdentityConfiguration {

	private static final String OLD_SNOW_OWL_ISSUER = "Snow Owl (pre-8.8.0)";

	private boolean adminParty = false;
	
	// JWT configuration
	private String issuer = OLD_SNOW_OWL_ISSUER;
	private String jwksUrl;
	
	private String jws;
	private String secret;
	private String signingKey;
	private String verificationKey;
	private String emailClaimProperty = "sub";
	private String permissionsClaimProperty = "permissions";
	
	private List<IdentityProviderConfig> providerConfigurations = Collections.emptyList();
	
	public boolean isAdminParty() {
		return adminParty;
	}

	public void setAdminParty(boolean adminParty) {
		this.adminParty = adminParty;
	}
	
	@JsonProperty("providers")
	public List<IdentityProviderConfig> getProviderConfigurations() {
		return providerConfigurations;
	}
	
	public void setProviderConfigurations(List<IdentityProviderConfig> providerConfigurations) {
		this.providerConfigurations = providerConfigurations;
	}
	
	public String getSecret() {
		return secret;
	}
	
	public void setSecret(String secret) {
		this.secret = secret;
	}
	
	public String getIssuer() {
		return issuer;
	}
	
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	
	public String getJws() {
		return jws;
	}
	
	public String getJwksUrl() {
		return jwksUrl;
	}
	
	public String getSigningKey() {
		return signingKey;
	}
	
	public String getVerificationKey() {
		return verificationKey;
	}
	
	public void setJws(String jws) {
		this.jws = jws;
	}
	
	public void setJwksUrl(String jwksUrl) {
		this.jwksUrl = jwksUrl;
	}
	
	public void setSigningKey(String signingKey) {
		this.signingKey = signingKey;
	}
	
	public void setVerificationKey(String verificationKey) {
		this.verificationKey = verificationKey;
	}
	
	public String getEmailClaimProperty() {
		return emailClaimProperty;
	}
	
	public String getPermissionsClaimProperty() {
		return permissionsClaimProperty;
	}
	
	public void setEmailClaimProperty(String emailClaimProperty) {
		this.emailClaimProperty = emailClaimProperty;
	}
	
	public void setPermissionsClaimProperty(String permissionsClaimProperty) {
		this.permissionsClaimProperty = permissionsClaimProperty;
	}

}