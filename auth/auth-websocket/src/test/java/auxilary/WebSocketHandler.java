package auxilary;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;

import java.util.ArrayDeque;

class WebSocketHandler extends ChannelInboundHandlerAdapter {

    private final WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;
    private final ArrayDeque<String> responses ;
    private final String request;

    public WebSocketHandler(WebSocketClientHandshaker newHandshaker, ArrayDeque<String> responses, String request) {
        this.handshaker = newHandshaker;
        this.responses = responses;
        this.request = request;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        this.handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object message) {
        if (!handshaker.isHandshakeComplete()) {
            Channel ch = context.channel();

            try {
                handshaker.finishHandshake(ch, (FullHttpResponse) message);
                handshakeFuture.setSuccess();
            } catch (WebSocketHandshakeException e) {
                handshakeFuture.setFailure(e);
            }
        }

        context.writeAndFlush(new TextWebSocketFrame(request));

        if (message instanceof TextWebSocketFrame) {
            responses.add(((TextWebSocketFrame) message).text());
            context.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
