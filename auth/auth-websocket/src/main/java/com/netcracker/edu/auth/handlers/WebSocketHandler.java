package com.netcracker.edu.auth.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.apache.log4j.Logger;

public class WebSocketHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = Logger.getLogger(WebSocketHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext context, Object message) {
        if (message instanceof WebSocketFrame) {
            context.writeAndFlush(new TextWebSocketFrame("Hello World!"));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause);
        ctx.close();
    }
}
