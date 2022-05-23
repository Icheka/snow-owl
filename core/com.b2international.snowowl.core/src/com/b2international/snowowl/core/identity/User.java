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

import static com.google.common.base.Preconditions.checkArgument;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import com.b2international.commons.collections.Collections3;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;

/**
 * Represents a logged in user in the system. A logged in user has access to his own username and assigned roles (and permissions).
 * @since 5.11.0
 */
public final class User implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final User SYSTEM = new User("System", List.of(Permission.ADMIN));
	
	private final String username;
	private final List<Permission> permissions;
	
	public User(String username, List<Permission> permissions) {
		checkArgument(!Strings.isNullOrEmpty(username), "Username may not be null or empty");
		this.username = username;
		this.permissions = Collections3.toImmutableList(permissions);
	}
	
	public String getUsername() {
		return username;
	}
	
	@JsonIgnore
	public List<Permission> getPermissions() {
		return permissions;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(username);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		User other = (User) obj;
		return Objects.equals(username, other.username);
	}

	/**
	 * @return <code>true</code> if this user has a permission that implies all other permissions, <code>false</code> otherwise.
	 */
	public boolean isAdministrator() {
		return getPermissions().stream()
				.filter(Permission::isAdmin)
				.findFirst()
				.isPresent();
	}
	
	/**
	 * Returns <code>true</code> if the user has the necessary permission to allow performing an operation on the given resource.
	 *  
	 * @param permissionRequirement
	 * @return
	 */
	public boolean hasPermission(Permission permissionRequirement) {
		return getPermissions().stream().anyMatch(permission -> permission.implies(permissionRequirement));
	}

	/**
	 * @param userId
	 * @return <code>true</code> if this User is the System user.
	 * @see #SYSTEM
	 */
	public static boolean isSystem(String userId) {
		return SYSTEM.getUsername().equals(userId);
	}

}