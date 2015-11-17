/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.id.cis.memory;

import org.junit.Before;

import com.b2international.snowowl.snomed.datastore.id.AbstractIdentifierServiceTest;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.gen.ItemIdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.id.memory.InMemorySnomedIdentifierServiceImpl;
import com.b2international.snowowl.snomed.datastore.id.reservations.ISnomedIdentiferReservationService;
import com.b2international.snowowl.snomed.datastore.internal.id.reservations.SnomedIdentifierReservationServiceImpl;

/**
 * @since 4.5
 */
public class InMemorySnomedIdentifierServiceImplTest extends AbstractIdentifierServiceTest {

	private ISnomedIdentifierService service;

	@Override
	protected ISnomedIdentifierService getIdentifierService() {
		return service;
	}

	@Before
	public void init() {
		final ISnomedIdentiferReservationService reservationService = new SnomedIdentifierReservationServiceImpl();
		service = new InMemorySnomedIdentifierServiceImpl(ItemIdGenerationStrategy.RANDOM, null, reservationService);
	}

}
