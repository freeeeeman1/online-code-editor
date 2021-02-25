package com.netcracker.edu.project.server;

import com.netcracker.edu.logic.ProjectService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.Logger;

public class Server {
    private final Logger logger = Logger.getLogger(Server.class);

    public static final int PORT = 8082;
    private static final String HOST = "localhost";

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workGroup;
    private final ProjectService projectService;

    public Server(ProjectService projectService) {
        this.bossGroup = new NioEventLoopGroup();
        this.workGroup = new NioEventLoopGroup();
        this.projectService = projectService;
    }

    public void start() {
        try {
            ServerBootstrap server = new ServerBootstrap();

            server.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerInitializer(projectService));

            ChannelFuture future = server.bind(HOST, PORT).sync();

            logger.info("Server started");

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error(e);
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
