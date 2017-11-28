package org.alexdev.duckhttpd.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.alexdev.duckhttpd.session.SessionId;
import org.alexdev.duckhttpd.session.SessionIdManager;

public class WebServer {

    private int port;

    private final EventLoopGroup masterGroup;
    private final EventLoopGroup slaveGroup;

    private ServerBootstrap bootstrap;

    public WebServer(int port) {
        this.port = port;
        this.masterGroup = new NioEventLoopGroup();
        this.slaveGroup = new NioEventLoopGroup();
    }

    public void start() throws InterruptedException {
        this.bootstrap = new ServerBootstrap()
            .group(this.masterGroup, this.slaveGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(new WebChannelInitializer())
            .option(ChannelOption.SO_BACKLOG, 128)
            .childOption(ChannelOption.SO_KEEPALIVE, true);

        SessionIdManager.getInstance(); // start session manager
        ChannelFuture channel = this.bootstrap.bind(this.port).sync();

    }
}