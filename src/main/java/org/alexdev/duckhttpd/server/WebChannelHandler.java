package org.alexdev.duckhttpd.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.alexdev.duckhttpd.response.ResponseBuilder;
import org.alexdev.duckhttpd.routes.Route;
import org.alexdev.duckhttpd.routes.RouteManager;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.util.config.Settings;

import java.io.IOException;

public class WebChannelHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            final FullHttpRequest request = (FullHttpRequest) msg;
            WebConnection client = null;//new WebConnection(ctx.channel(), request);

            if (!ctx.channel().hasAttr(WebConnection.WEB_CONNECTION)) {
                client = new WebConnection(ctx.channel(), request);
            }

            if (ctx.channel().hasAttr(WebConnection.WEB_CONNECTION)) {
                client = ctx.channel().attr(WebConnection.WEB_CONNECTION).get();
            }

            if (client != null) {
                client.validateSession();
            } else {
                return;
            }

            final Route rawRoute = RouteManager.getRoute(client, "");
            final Route route = RouteManager.getRoute(client, request.uri());

            if (rawRoute != null) {
                rawRoute.handleRoute(client);
            }

            FullHttpResponse response = null;

            if (route != null) {
                route.handleRoute(client);

                if (client.hasFileResponseOverride()) {
                    client.setFileResponseOverride(false);
                    return;
                }

                response = client.response();

                if (response == null) {
                    response = Settings.getInstance().getResponses().getErrorResponse(client, "Unknown Response", "This server handler did not send a response back.");
                }

            } else {

                if (Settings.getInstance().getSiteDirectory().length() > 0) {

                    if (!ResponseBuilder.create(client, request)) {
                        response = Settings.getInstance().getResponses().getNotFoundResponse(client);
                    }
                }
            }

            if (response != null){

                if (HttpUtil.isKeepAlive(request)) {
                    response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                }

                client.cookies().encodeCookies(response);
                ctx.channel().writeAndFlush(response);
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
        if (!(cause instanceof IOException)) {
            WebConnection client = null;

            if (ctx.channel().hasAttr(WebConnection.WEB_CONNECTION)) {
                client = ctx.channel().attr(WebConnection.WEB_CONNECTION).get();
            }

            ctx.channel().writeAndFlush(Settings.getInstance().getResponses().getInternalServerErrorResponse(client, cause));
        }
    }
}