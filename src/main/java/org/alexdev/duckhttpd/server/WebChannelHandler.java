package org.alexdev.duckhttpd.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import org.alexdev.duckhttpd.routes.Route;
import org.alexdev.duckhttpd.routes.RouteManager;
import org.alexdev.duckhttpd.util.config.Settings;
import org.alexdev.duckhttpd.util.response.ResponseBuilder;
import org.alexdev.duckhttpd.server.session.WebConnection;

public class WebChannelHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof FullHttpRequest) {

            final FullHttpRequest request = (FullHttpRequest) msg;
            final Route route = RouteManager.getRoute(request.uri());

            if (route != null) {
                WebConnection session = new WebConnection(ctx.channel(), request);
                route.handleRoute(session);

                //request.release();

                FullHttpResponse response = session.response();

                if (response != null) {
                    ctx.channel().writeAndFlush(response);
                }
            } else {

                final FullHttpResponse fileResponse = ResponseBuilder.handleFileResponse(request);

                if (fileResponse != null) {
                    ctx.channel().writeAndFlush(fileResponse);
                } else {
                    ctx.channel().writeAndFlush(Settings.getInstance().getWebResponses().getNotFoundResponse());
                }
            }

            request.release();

        } else {
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        /*StringWriter sw = new StringWriter();
        cause.printStackTrace(new PrintWriter(sw));*/

        ctx.channel().writeAndFlush(Settings.getInstance().getWebResponses().getInternalServerErrorResponse(cause));
    }
}