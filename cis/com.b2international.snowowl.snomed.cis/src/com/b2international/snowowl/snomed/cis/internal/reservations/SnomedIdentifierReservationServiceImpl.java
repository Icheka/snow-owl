/*
 * Copyright 2011-2018 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.cis.internal.reservations;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.cis.SnomedIdentifier;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.cis.reservations.ISnomedIdentifierReservationService;
import com.b2international.snowowl.snomed.cis.reservations.Reservation;
import com.b2international.snowowl.snomed.cis.reservations.Reservations;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * @since 4.0 
 */
public class SnomedIdentifierReservationServiceImpl implements ISnomedIdentifierReservationService {

	private static final Logger LOG = LoggerFactory.getLogger(ISnomedIdentifierReservationService.class);
	private final Map<String, Reservation> reservations = Collections.synchronizedMap(Maps.<String, Reservation>newHashMap());
	
	public SnomedIdentifierReservationServiceImpl() {
		reservations.put("metadata-range", Reservations.range(9000_0000_0000_000L, SnomedIdentifiers.MAX_INT_ITEMID, null, Collections.singleton(ComponentCategory.CONCEPT)));
	}
	
	@Override
	public void create(String reservationName, Reservation reservation) {
		checkName(reservationName);
		checkArgument(reservation != null, "Reservation must be defined");
		if (reservations.containsKey(reservationName)) {
			throw new SnowowlRuntimeException("Reservation already defined: " + reservationName);
		}
		reservations.put(reservationName, reservation);
		LOG.info("Added SNOMED CT Identifier Reservation: {} -> {}", reservationName, reservation);
	}

	@Override
	public Collection<Reservation> getReservations() {
		return ImmutableList.copyOf(reservations.values());
	}

	@Override
	public void delete(String reservationName) {
		final Reservation removed = reservations.remove(reservationName);
		if (removed != null) {
			LOG.info("Removed SNOMED CT Identifier Reservation: {} -> {}", reservationName, removed);
		}
	}

	@Override
	public Reservation getReservation(String reservationName) {
		checkName(reservationName);
		return reservations.get(reservationName);
	}

	private void checkName(String reservationName) {
		checkArgument(!Strings.isNullOrEmpty(reservationName), "Name must be defined");
	}

	@Override
	public Set<String> isReserved(Set<String> componentIdsToCheck) {
		final ImmutableSet.Builder<String> reservedIds = ImmutableSet.builder();
		final Set<SnomedIdentifier> identifiersToCheck = componentIdsToCheck.stream().map(SnomedIdentifiers::create).collect(Collectors.toSet());
		for (Reservation reservation : getReservations()) {
			reservation.intersection(identifiersToCheck)
				.stream()
				.map(SnomedIdentifier::toString)
				.forEach(reservedIds::add);
		}
		return reservedIds.build();
	}

}
