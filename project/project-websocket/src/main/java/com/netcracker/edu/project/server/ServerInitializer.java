package com.netcracker.edu.project.server;

import com.netcracker.edu.auth.handlers.HttpHandler;
import com.netcracker.edu.logic.ProjectService;
import com.netcracker.edu.project.handler.ProjectHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    private final ProjectService projectService;

    public ServerInitializer(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                .addLast("httpServerCodec", new HttpServerCodec())
                .addLast("HttpObjectAggregator", new HttpObjectAggregator(1048576))
                .addLast("httpHandler", new HttpHandler())
                .addLast("Project handler", new ProjectHandler(projectService))
                .addLast("logger", new LoggingHandler(LogLevel.INFO));
    }
}
