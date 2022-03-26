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
package com.b2international.snowowl.eventbus.util;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;

import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;

/**
 * @since 3.1
 */
public class CountDownHandler implements IHandler<IMessage> {

	private final String expectedMessage;
	private final CountDownLatch latch;

	public CountDownHandler(String expectedMessage, CountDownLatch latch) {
		this.expectedMessage = expectedMessage;
		this.latch = latch;
	}
	
	@Override
	public void handle(IMessage message) {
		assertEquals(expectedMessage, message.body(String.class));
		latch.countDown();
	}

}
