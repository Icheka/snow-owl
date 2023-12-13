/*
 * Copyright 2021-2022 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.internal.eventbus.netty;

import static com.google.common.collect.Maps.newHashMap;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;
import com.b2international.snowowl.eventbus.netty.EventBusNettyUtil;
import com.b2international.snowowl.eventbus.netty.IEventBusNettyHandler;
import com.b2international.snowowl.internal.eventbus.MessageFactory;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * A message handler bridge implementation for a Netty channel and a local event
 * bus. It exhibits the following behavior:
 * <ul>
 * <li>Messages coming from a remote peer over a channel are submitted to the
 * local event bus via {@link IEventBus#receive(IMessage)};
 * 
 * <li>Messages received from the local event bus are written to the channel in
 * a serialization-friendly format.
 * </ul>
 * 
 * @since 8.1.0
 */
public class EventBusNettyHandler extends SimpleChannelInboundHandler<IMessage> implements IEventBusNettyHandler {

	private static class PingMessage implements Serializable {
		// Empty class body 
	}
	
	private static final PingMessage PING = new PingMessage();

	private static final Logger LOG = LoggerFactory.getLogger(EventBusNettyHandler.class);
	
	private final IEventBus eventBus;

	// Handler is stateful, ie. a separate instance is maintained for each connection
	private volatile ChannelHandlerContext ctx;

	public EventBusNettyHandler(final IEventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Override
	public void channelActive(final ChannelHandlerContext ctx) throws Exception {
		this.ctx = ctx;
		super.channelActive(ctx);
	}
	
	@Override
	public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
		this.ctx = null;
		super.channelInactive(ctx);
	}
	
	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final IMessage message) throws Exception {
		final String remoteReplyAddress = message.replyAddress();
		final IHandler<IMessage> replyHandler;
		
		if (remoteReplyAddress == null) {
			replyHandler = null;
		} else {
			// Send reply back to the remote using a local reply handler
			replyHandler = reply -> handle(MessageFactory.createMessage(remoteReplyAddress, 
				reply.body(), 
				reply.tag(), 
				reply.headers(),
				reply.isSend(),
				reply.isSucceeded()));
		}
		
		final Map<String, String> headers = newHashMap(message.headers());
		headers.put(EventBusNettyUtil.HEADER_CLIENT_ID, getChannelId(ctx));
		
		final IMessage messageWithHeader = MessageFactory.createMessage(message.address(), 
			message.body(), 
			message.tag(), 
			headers, 
			message.isSend(), 
			message.isSucceeded());
		
		eventBus.receive(messageWithHeader, replyHandler);
	}
	
	private String getChannelId(final ChannelHandlerContext ctx) {
		return ctx.channel().id().asShortText();
	}
	
	private void channelWrite(final ChannelHandlerContext ctx, final IMessage message) {
		try {
			
			final ChannelFuture writeFuture = ctx.writeAndFlush(MessageFactory.writeMessage(message));
			writeFuture.addListener(f -> { if (!f.isSuccess()) {
				LOG.error("Exception happened when sending message", f.cause());
				channelWriteFailure(ctx, message, f.cause());
			}});
			
		} catch (final Throwable t) {
			LOG.error("Exception happened trying to send message", t);
			channelWriteFailure(ctx, message, t);
		}
	}
	
	private void channelWriteFailure(final ChannelHandlerContext ctx, final IMessage message, final Throwable t) {
		/*
		 * We will try and notify the receiver that something went wrong by sending a
		 * "did not succeed" message with the caught exception. Otherwise calls to
		 * getSync() will hang indefinitely.
		 */
		try {
			ctx.writeAndFlush(MessageFactory.writeFailure(message, t));
		} catch (final Exception ignored) {
			// Best effort
		}
	}

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
		LOG.error("Exception caught for channel, closing.", cause);
		ctx.close();
	}

	@Override
	public void handle(final IMessage message) {
		final ChannelHandlerContext localCtx = ctx;
		if (localCtx != null) {
			channelWrite(localCtx, message);
		}
	}
	
	@Override
	public void userEventTriggered(final ChannelHandlerContext ctx, final Object event) throws Exception {
		if (!(event instanceof IdleStateEvent)) {
			// Propagate user events of unexpected type
			super.userEventTriggered(ctx, event);
			return;
		}
		
		final IdleStateEvent idleEvent = (IdleStateEvent) event;
		if (IdleState.READER_IDLE.equals(idleEvent.state())) {
			// We haven't heard anything from the other end for a while, close the connection
			ctx.close();
		} else if (IdleState.WRITER_IDLE.equals(idleEvent.state())) {
			/*
			 * Send message which will reset the "read idle" timer on the other end, and the
			 * "write idle" timer on our side
			 */
			ctx.writeAndFlush(PING);
		} else {
			/*
			 * We are not interested in other (ALL_IDLE) states, but in case this changes,
			 * propagate this event as well.
			 */
			super.userEventTriggered(ctx, event);
		}
	}
}
