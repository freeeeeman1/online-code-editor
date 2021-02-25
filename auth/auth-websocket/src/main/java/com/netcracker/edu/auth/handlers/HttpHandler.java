package com.netcracker.edu.auth.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import org.apache.log4j.Logger;

public class HttpHandler extends ChannelInboundHandlerAdapter {

    final Logger logger = Logger.getLogger(HttpHandler.class);
    private final static String UPGRADE = "upgrade";
    private final static String WEBSOCKET = "webSocket";

    @Override
    public void channelRead(ChannelHandlerContext context, Object message) {
        if (message instanceof FullHttpRequest) {
            FullHttpRequest httpRequest = (FullHttpRequest) message;

            if (isRenewable(httpRequest)) {
                handleHandshake(context, httpRequest);
                context.pipeline().remove(this);
            }

        } else {
            logger.error("Unexpected message format");
            context.fireChannelReadComplete();
        }
    }

    private Boolean isRenewable(HttpRequest httpRequest) {
        HttpHeaders headers = httpRequest.headers();
        String connection = headers.get(HttpHeaderNames.CONNECTION).toLowerCase();

        return connection.contains(UPGRADE) &&
                WEBSOCKET.equalsIgnoreCase(headers.get(HttpHeaderNames.UPGRADE));
    }

    protected void handleHandshake(ChannelHandlerContext context, HttpRequest request) {
        WebSocketServerHandshakerFactory wsFactory =
                new WebSocketServerHandshakerFactory(getWebSocketURL(request), null, true);
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(request);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(context.channel());
        } else {
            handshaker.handshake(context.channel(), request);
        }
    }

    private String getWebSocketURL(HttpRequest request) {
        return "ws://" + request.headers().get("Host") + request.uri();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error(cause);
        ctx.close();
    }
}
