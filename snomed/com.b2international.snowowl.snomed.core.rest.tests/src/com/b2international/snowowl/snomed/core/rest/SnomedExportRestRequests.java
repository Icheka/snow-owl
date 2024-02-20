/*
 * Copyright 2011-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.core.rest;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.test.commons.rest.RestExtensions;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;

import io.restassured.response.Response;

/**
 * @since 5.4
 */
public abstract class SnomedExportRestRequests {

	private SnomedExportRestRequests() {
	}
	
	public static Response export(String path, Map<String, Object> exportConfiguration) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.queryParams(RestExtensions.encodeQueryParameters(exportConfiguration))
			.get("/{path}/export", path);
	}
	
	public static File doExport(final IBranchPath branchPath, final Map<String, Object> exportConfiguration) throws Exception {
		return doExport(branchPath.getPath(), exportConfiguration);
	}
	
	public static File doExport(final String branchPath, final Map<String, Object> exportConfiguration) throws Exception {
		File tmpDir = null;
		File exportArchive = null;

		try {

			tmpDir = Files.createTempDir();
			exportArchive = new File(tmpDir, "export.zip");
			new ByteSource() {
				@Override
				public InputStream openStream() throws IOException {
					return export(branchPath, exportConfiguration)
							.then().assertThat().statusCode(200)
							.extract().asInputStream();
				}
			}.copyTo(Files.asByteSink(exportArchive));

		} catch (final Exception e) {
			throw e;
		} finally {
			if (tmpDir != null) {
				tmpDir.deleteOnExit();
			}

			if (exportArchive != null) {
				exportArchive.deleteOnExit();
			}
		}

		assertNotNull(exportArchive);
		return exportArchive;
	}

}
