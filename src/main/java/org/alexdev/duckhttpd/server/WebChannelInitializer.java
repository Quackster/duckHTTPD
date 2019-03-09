package org.alexdev.duckhttpd.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.flow.FlowControlHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

public class WebChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(final SocketChannel ch) throws Exception {

        ch.pipeline().addLast(new HttpServerCodec());
        ch.pipeline().addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8));
        ch.pipeline().addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));
        ch.pipeline().addLast(new HttpObjectAggregator(512 * 1024));
        ch.pipeline().addLast(new ChunkedWriteHandler());
        ch.pipeline().addLast(new WebChannelHandler());

    }
}