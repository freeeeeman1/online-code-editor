package com.netcracker.edu.auth.server;

import com.netcracker.edu.auth.handlers.HttpHandler;
import com.netcracker.edu.auth.handlers.AuthHandler;
import com.netcracker.edu.auth.handlers.WebSocketHandler;
import com.netcracker.edu.auth.session.SessionManager;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import com.netcracker.edu.auth.service.AuthServiceImpl;

public class ServerInit extends ChannelInitializer<SocketChannel> {

    AuthServiceImpl authService;
    SessionManager sessionManager;

    public ServerInit(AuthServiceImpl authService, SessionManager sessionManager) {
        this.authService = authService;
        this.sessionManager = sessionManager;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline channelPipeline = socketChannel.pipeline();
        channelPipeline.addLast("HttpServerCodec", new HttpServerCodec());
        channelPipeline.addLast("HttpObjectAggregator", new HttpObjectAggregator(1048576));
        channelPipeline.addLast("HttpHandler", new HttpHandler());
        channelPipeline.addLast("AuthHandler", new AuthHandler(sessionManager, authService));
        channelPipeline.addLast("WebSocketHandler", new WebSocketHandler());
    }
}
