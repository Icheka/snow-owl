/*
 * Copyright 2011-2022 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.eventbus;

import java.util.Map;

/**
 * Represents a message to be send/receive over/from the {@link IEventBus}.
 *
 * @since 3.1
 */
public interface IMessage {

	/**
	 * Default tag for messages.
	 */
	String TAG_EVENT = "event";
	
	/**
	 * Tag for response messages.
	 */
	String TAG_REPLY = "reply";
	
	/**
	 * @return the current headers {@link Map} or an empty {@link Map}.
	 * @since 7.2
	 */
	Map<String, String> headers();

	/**
	 * Returns the body of this {@link IMessage}, can be used for simple types
	 * with explicit cast.
	 *
	 * @return
	 * @throws ClassNotFoundException
	 */
	Object body();

	/**
	 * Returns the body of this message. The method always return the resolved
	 * object with the given type.
	 *
	 * @return
	 */
	<T> T body(Class<T> type);

	/**
	 * Returns the replyAddress for this message.
	 *
	 * @return
	 */
	String replyAddress();
	
	/**
	 * Returns the address this message is belongs to.
	 *
	 * @return
	 */
	String address();

	/**
	 * Returns if this message is a send only message.
	 *
	 * @return
	 */
	boolean isSend();

	/**
	 * Replies with the given response.
	 *
	 * @param response
	 */
	void reply(Object response);
	
	/**
	 * Replies with the given response and sets the given headers as response headers
	 *
	 * @param response
	 * @param headers
	 */
	void reply(Object response, Map<String, String> headers);
	
	/**
	 * Returns a failure response to the reply address.
	 *
	 * @param failure
	 */
	void fail(Object failure);
	
	/**
	 * @return the tag associated with this message.
	 */
	String tag();
	
	/**
	 * Returns <code>true</code> if the original message was successfully
	 * delivered to the address, <code>false</code> if some error happened, in
	 * this case the {@link #body(Class)} method should return the
	 * {@link Exception} for this error.
	 *
	 * @return
	 * @see IMessage#body(Class)
	 */
	boolean isSucceeded();
}
