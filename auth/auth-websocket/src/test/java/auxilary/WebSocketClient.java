package auxilary;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;

import java.net.URI;
import java.util.ArrayDeque;

public class WebSocketClient {

    static final int PORT = 8080;    
    static final String LOCALHOST = "localhost";

    private final EventLoopGroup group;
    private final Bootstrap bootstrap;

    public WebSocketClient(ArrayDeque<String> responses, String request) {
        this.group = new NioEventLoopGroup();

        final WebSocketHandler websocketHandler = new WebSocketHandler(WebSocketClientHandshakerFactory
                .newHandshaker(
                        URI.create("ws://localhost:8080"),
                        WebSocketVersion.V13,
                        null,
                        true,
                        new DefaultHttpHeaders()), responses, request);

        this.bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast("HttpClientCodec", new HttpClientCodec());
                        p.addLast("HttpObjectAggregator", new HttpObjectAggregator(5242880));
                        p.addLast(websocketHandler);
                    }
                });
    }

    public void sendRequest() throws InterruptedException {
        try {
            Channel channel = bootstrap.connect(LOCALHOST, PORT).sync().channel();
            channel.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
