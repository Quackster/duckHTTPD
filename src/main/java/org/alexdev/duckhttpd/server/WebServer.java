package org.alexdev.duckhttpd.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class WebServer {

    private int port = 80;

    private final EventLoopGroup masterGroup;
    private final EventLoopGroup slaveGroup;

    private ChannelFuture channel;
    private ServerBootstrap bootstrap;

    public WebServer(int port) {
        this.port = port;
        this.masterGroup = new NioEventLoopGroup();
        this.slaveGroup = new NioEventLoopGroup();
    }

    public void start() throws InterruptedException {
        final ServerBootstrap bootstrap = new ServerBootstrap()
            .group(masterGroup, slaveGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(new WebChannelInitializer())
            .option(ChannelOption.SO_BACKLOG, 128)
            .childOption(ChannelOption.SO_KEEPALIVE, true);

        channel = bootstrap.bind(this.port).sync();
    }
}