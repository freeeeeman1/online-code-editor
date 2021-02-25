package com.netcracker.edu.auth.server;

import com.netcracker.edu.auth.service.AuthServiceImpl;
import com.netcracker.edu.auth.session.SessionManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.Logger;

public class Server {

    private static final int PORT = 8080;

    private final Logger logger = Logger.getLogger(Server.class);
    private final AuthServiceImpl authService;
    private final SessionManager sessionManager;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    public Server(AuthServiceImpl authService, SessionManager sessionManager) {
        this.authService = authService;
        this.sessionManager = sessionManager;
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
    }

    public void start() throws InterruptedException {
        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerInit(authService, sessionManager))
                    .option(ChannelOption.SO_BACKLOG, 128);
            ChannelFuture channelFuture = server.bind(PORT).sync();
            logger.info("Server starting...");

            channelFuture.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            logger.info("Server shutdown gracefully");
        }
    }
}
