package com.netcracker.edu.project.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import org.apache.log4j.Logger;

import java.net.URI;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static com.netcracker.edu.project.server.Server.PORT;

public class WebSocketHandler extends ChannelInboundHandlerAdapter {
    private final Logger logger = Logger.getLogger(WebSocketHandler.class);

    private static final String HOST = "localhost";

    private final WebSocketClientHandshaker websocketHandshaker;
    private ChannelPromise handshakeFuture;
    private final BlockingQueue<String> responses;

    public WebSocketHandler() {
        this.websocketHandshaker = createHandshaker();
        responses = new ArrayBlockingQueue<>(10);
    }

    public ChannelPromise getHandshakeFuture() {
        return handshakeFuture;
    }

    public WebSocketClientHandshaker getWebsocketHandshaker() {
        return websocketHandshaker;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        this.handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        websocketHandshaker.handshake(ctx.channel());
        logger.info("websocket connected");
    }

    public BlockingQueue<String> getResponses() {
        return responses;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) {
        if (!websocketHandshaker.isHandshakeComplete()) {
            Channel channel = ctx.channel();
            FullHttpResponse response = (FullHttpResponse) message;

            try {
                websocketHandshaker.finishHandshake(channel, response);

                handshakeFuture.setSuccess();
                logger.info("handshake done");
            } catch (WebSocketHandshakeException e) {
                handshakeFuture.setFailure(e);
            }
        }

        if (message instanceof TextWebSocketFrame) {
            responses.add(((TextWebSocketFrame) message).text());
        }

        if (message instanceof CloseWebSocketFrame) {
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error(ctx, cause);
    }

    private WebSocketClientHandshaker createHandshaker() {
        return WebSocketClientHandshakerFactory.newHandshaker(
                URI.create("ws://" + HOST + PORT),
                WebSocketVersion.V13,
                null,
                true,
                new DefaultHttpHeaders());
    }
}
