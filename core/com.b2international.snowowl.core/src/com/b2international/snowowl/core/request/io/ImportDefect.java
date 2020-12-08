/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request.io;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 7.12
 */
public final class ImportDefect implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum ImportDefectType {
		ERROR,
		WARNING,
		INFO
	}
	
	private final String file;
	private final String location;
	private final String message;
	private final ImportDefectType type;
	
	@JsonCreator
	public ImportDefect(
			@JsonProperty("file") final String file, 
			@JsonProperty("location") final String location, 
			@JsonProperty("message") final String message, 
			@JsonProperty("type") final ImportDefectType type) {
		this.file = file;
		this.location = location;
		this.message = message;
		this.type = type;
	}
	
	public String getFile() {
		return file;
	}
	
	public String getLocation() {
		return location;
	}
	
	public String getMessage() {
		return message;
	}
	
	public ImportDefectType getType() {
		return type;
	}
	
	public boolean isError() {
		return type == ImportDefectType.ERROR;
	}
	
	public boolean isWarning() {
		return type == ImportDefectType.WARNING;
	}
	
	public boolean isInfo() {
		return type == ImportDefectType.INFO;
	}

	public static ImportDefect error(String file, String location, String message) {
		return new ImportDefect(file, location, message, ImportDefectType.ERROR);
	}
	
	public static ImportDefect warn(String file, String location, String message) {
		return new ImportDefect(file, location, message, ImportDefectType.WARNING);
	}
	
	public static ImportDefect info(String file, String location, String message) {
		return new ImportDefect(file, location, message, ImportDefectType.INFO);
	}
	
}
