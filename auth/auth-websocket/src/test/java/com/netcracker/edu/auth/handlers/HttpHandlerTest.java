package com.netcracker.edu.auth.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpHandlerTest {

    private HttpHandler httpHandler;
    private HttpRequest httpRequest;
    private EmbeddedChannel embeddedChannel;

    @Before
    public void initTest() {
        this.httpHandler = new HttpHandler();
        this.httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "8080");
        this.httpRequest.headers()
                .add("Origin", "http://example.com")
                .add("Connection", "Upgrade")
                .add("Host", "localhost:")
                .add("Upgrade", "websocket")
                .add("Sec-WebSocket-Key", "dGhlIHNhbXBsZSBub25jZQ==")
                .add("Sec-WebSocket-Version", "13");
    }

    @Test
    public void checkSuccessfulHandshake() {

        this.embeddedChannel = new EmbeddedChannel(new HttpServerCodec(), httpHandler);
        ChannelHandlerContext channelHandlerContext = mock(ChannelHandlerContext.class);

        when(channelHandlerContext.pipeline()).thenReturn(embeddedChannel.pipeline());
        when(channelHandlerContext.channel()).thenReturn(embeddedChannel);
        String expected = "HTTP/1.1 101 Switching Protocols\r\n" +
                "upgrade: websocket\r\n" +
                "connection: upgrade\r\n" +
                "sec-websocket-accept: s3pPLMBiTxaQ9kYGzzhZRbK+xOo=\r\n\r\n";

        embeddedChannel.writeInbound(httpRequest);

        ByteBuf buf = embeddedChannel.readOutbound();
        byte[] bytes = new byte[buf.readableBytes()];
        buf.duplicate().readBytes(bytes);
        String response = new String(bytes);

        assertEquals(expected, response);
    }

    @Test(expected = WebSocketHandshakeException.class)
    public void checkBadHandshake() {
        this.embeddedChannel = new EmbeddedChannel(new HttpServerCodec(), httpHandler);
        HttpRequest httpWrongRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "8080");
        ChannelHandlerContext channelHandlerContext = mock(ChannelHandlerContext.class);

        when(channelHandlerContext.pipeline()).thenReturn(embeddedChannel.pipeline());
        when(channelHandlerContext.channel()).thenReturn(embeddedChannel);

        httpHandler.handleHandshake(channelHandlerContext, httpWrongRequest);
    }
}
