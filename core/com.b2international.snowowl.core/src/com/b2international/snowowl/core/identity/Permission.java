/*
 * Copyright 2021-2023 B2i Healthcare, https://b2ihealthcare.com
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
import java.util.function.Predicate;

import com.b2international.commons.CompareUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Represents an application specific permission.
 * Permissions have two parts: operation and resource
 * Resources can be expressed using file-style wildcards such as '*' and '?'
 * 
 * @since 8.0
 */
public interface Permission extends Serializable {

	public static final String SEPARATOR = ":";
	public static final String RESOURCE_SEPARATOR = "/";
	
	public static final String ALL = "*"; //$NON-NLS-N$
	public static final String OPERATION_BROWSE = "browse";  //$NON-NLS-N$
	public static final String OPERATION_EDIT = "edit";  //$NON-NLS-N$
	public static final String OPERATION_IMPORT = "import";  //$NON-NLS-N$
	public static final String OPERATION_EXPORT = "export";  //$NON-NLS-N$
	public static final String OPERATION_VERSION = "version";  //$NON-NLS-N$
	public static final String OPERATION_PROMOTE = "promote";  //$NON-NLS-N$
	public static final String OPERATION_CLASSIFY = "classify";  //$NON-NLS-N$
	
	/**
	 * The ultimate superuser permission which allows any operation to be performed on any resource.
	 */
	public static final Permission ADMIN = requireAll(Permission.ALL, Permission.ALL);
	
	public static Permission requireAll(String operation, String...resources) {
		return requireAll(operation, List.of(resources));
	}
	
	public static Permission requireAll(String operation, Iterable<String> resources) {
		return new RequireAllPermission(operation, resources);
	}
	
	public static Permission requireAny(String operation, String...resources) {
		return requireAny(operation, List.of(resources));
	}
	
	public static Permission requireAny(String operation, Iterable<String> resources) {
		return new RequireAnyPermission(operation, resources);
	}
	
	public static String asResource(String...resources) {
		return String.join(RESOURCE_SEPARATOR, resources);
	}
	
	public static String asResource(Iterable<String> resources) {
		return String.join(RESOURCE_SEPARATOR, resources);
	}

	/**
	 * @return the operation part from the permission string value.
	 */
	@JsonIgnore
	String getOperation();

	/**
	 * @return the resource part from the permission string value.
	 */
	@JsonIgnore
	String getResource();

	/**
	 * @return the actual permission string value that represents this {@link Permission} object.
	 */
	String getPermission();
	
	/**
	 * @return a {@link List} representation of all permission resources this permission gives access to.
	 */
	@JsonIgnore
	default List<String> getResources() {
		return List.of(getResource());
	}

	/**
	 * @param permissionToAuthenticate
	 * @return <code>true</code> if this permission implies the given permission, meaning it satisfies at as a requirement and basically represent the same access rules
	 */
	boolean implies(Permission permissionToAuthenticate);

	/**
	 * @return <code>true</code> if the permission implies the superuser (aka administrator) permission, which is any operation allowed on any resource, <code>false</code> if not.
	 */
	@JsonIgnore
	default boolean isAdmin() {
		return Permission.ALL.equals(getOperation()) && Permission.ALL.equals(getResource());
	}
	
	/**
	 * Convert the {@link String} representation of a permission into a {@link Permission} object.
	 * The input string must be in the form of "&lt;operation&gt;:&lt;resource&gt;"
	 * 
	 * @param permission as String
	 * @return a {@link Permission} with the appropriate operation and resources set
	*/
	@JsonCreator
	static Permission valueOf(@JsonProperty("permission") final String permission) {
		checkArgument(!CompareUtils.isEmpty(permission), "Permission argument is required");
		final String[] parts = permission.split(SEPARATOR);
		checkArgument(parts.length == 2, "A permission should consist of two String values separated by a ':' character. Got: %s", permission);
		final String operation = parts[0];
		final String resourceReference = parts[1];
		if (RequireAnyPermission.isRequireAnyResource(resourceReference)) {
			String[] resourceIds = resourceReference.substring("anyOf(".length(), resourceReference.length() - 1).split(",");
			return requireAny(operation, resourceIds);
		} else if (RequireAllPermission.isRequireAllResource(resourceReference)) {
			String[] resourceIds = resourceReference.substring("allOf(".length(), resourceReference.length() - 1).split(",");
			return requireAll(operation, resourceIds);
		} else {
			return requireAll(operation, resourceReference);
		}
	}

	/**
	 * Perform custom access check on each resource present in this {@link Permission}.
	 * @param checkResource - the function to run for each resource in this permission
	 * @return <code>true</code> or <code>false</code> depending on whether the user has access to the resource or not
	 */
	boolean check(Predicate<String> checkResource);
	
}
