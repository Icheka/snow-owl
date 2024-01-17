/*
 * Copyright 2017-2022 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.events;

import java.util.concurrent.atomic.AtomicBoolean;

import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;
import com.b2international.snowowl.eventbus.events.SystemNotification;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.subjects.PublishSubject;

/**
 * @since 5.7
 */
public final class Notifications extends Observable<SystemNotification> implements IDisposableService, IHandler<IMessage> {

	private final IEventBus bus;
	private final AtomicBoolean disposed = new AtomicBoolean(false);
	private final PublishSubject<SystemNotification> processor;

	public Notifications(IEventBus bus) {
		super();
		this.bus = bus;
		this.processor = PublishSubject.create();
		this.bus.registerHandler(SystemNotification.ADDRESS, this);
	}
	
	@Override
	public void handle(IMessage message) {
		try {
			final SystemNotification notification = message.body(SystemNotification.class);
			processor.onNext(notification);
		} catch (Throwable e) {
			processor.onError(e);
		}
	}

	@Override
	protected void subscribeActual(Observer<? super SystemNotification> subscriber) {
		this.processor.subscribe(subscriber);
	}
	
	@Override
	public boolean isDisposed() {
		return disposed.get();
	}
	
	@Override
	public void dispose() {
		if (disposed.compareAndSet(false, true)) {
			processor.onComplete();
			bus.unregisterHandler(SystemNotification.ADDRESS, this);
		}
	}

}
