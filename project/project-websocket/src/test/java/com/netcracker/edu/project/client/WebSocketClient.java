package com.netcracker.edu.project.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.log4j.Logger;

import java.util.concurrent.BlockingQueue;

import static com.netcracker.edu.project.server.Server.PORT;

public class WebSocketClient {
    private static final String HOST = "localhost";

    private final Logger logger = Logger.getLogger(WebSocketClient.class);

    private WebSocketHandler websocketHandler;
    private final Bootstrap bootstrap;

    public WebSocketClient() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();

        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast("httpClientCodec", new HttpClientCodec())
                                .addLast("httpHandler", new HttpObjectAggregator(5242880))
                                .addLast(websocketHandler = new WebSocketHandler());        // TODO: check mentor
                    }
                });
    }

    public void sendRequest(String request) {
        try {
            Channel channel = bootstrap.connect(HOST, PORT).sync().channel();

            if (!handshakeComplete()) {
                websocketHandler.getHandshakeFuture().sync();
            }
            ByteBuf ByteBufRequest = convertToByteBuf(request);

            channel.writeAndFlush(new TextWebSocketFrame(ByteBufRequest));

            channel.closeFuture();
        } catch (Exception e) {
            logger.error("Channel isn't created when request send");
        }
    }

    public BlockingQueue<String> getResponses() {
        return websocketHandler.getResponses();
    }

    private ByteBuf convertToByteBuf(String request) {
        byte[] bytes = request.getBytes();

        return Unpooled.copiedBuffer(bytes);
    }

    private boolean handshakeComplete() {
        return websocketHandler.getWebsocketHandshaker().isHandshakeComplete();
    }
}
