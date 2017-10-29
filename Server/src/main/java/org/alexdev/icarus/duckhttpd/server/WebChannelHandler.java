package org.alexdev.icarus.duckhttpd.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import org.alexdev.icarus.duckhttpd.routes.manager.Route;
import org.alexdev.icarus.duckhttpd.routes.manager.RouteManager;
import org.alexdev.icarus.duckhttpd.util.config.Settings;
import org.alexdev.icarus.duckhttpd.util.response.ResponseBuilder;
import org.alexdev.icarus.duckhttpd.server.session.WebSession;

public class WebChannelHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof FullHttpRequest) {

            final FullHttpRequest request = (FullHttpRequest) msg;
            final FullHttpResponse fileResponse = ResponseBuilder.handleFileResponse(request);
            final Route route = RouteManager.getRoute(request.uri());

            if (route != null) {
                WebSession session = new WebSession(ctx.channel(), request);
                FullHttpResponse response = route.handleRoute(session);

                if (response == null) {
                    exceptionCaught(ctx, new Exception("Could not handle request: " + request.uri()));
                    return;
                }

                ctx.channel().writeAndFlush(response);
                return;
            }

            if (fileResponse != null) {
                ctx.channel().writeAndFlush(fileResponse);
                return;
            } else {
                ctx.channel().writeAndFlush(Settings.getInstance().getWebResponses().getNotFoundResponse());
            }

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