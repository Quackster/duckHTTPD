package org.alexdev.duckhttpd.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.alexdev.duckhttpd.exceptions.NoServerResponseException;
import org.alexdev.duckhttpd.response.ResponseBuilder;
import org.alexdev.duckhttpd.routes.PageRules;
import org.alexdev.duckhttpd.routes.Route;
import org.alexdev.duckhttpd.routes.RouteManager;
import org.alexdev.duckhttpd.server.connection.WebConnection;
import org.alexdev.duckhttpd.util.config.Settings;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

public class WebChannelHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        //if (msg instanceof FullHttpRequest) {
        final FullHttpRequest request = (FullHttpRequest) msg;

        WebConnection client = new WebConnection(ctx.channel(), request);

        for (var ip : Settings.getInstance().getBlockIpv4()) {
            if (client.getIpAddress().startsWith(ip)) {
                ctx.channel().close();
                return;
            }
        }

        client.validateSession();

        ctx.channel().attr(WebConnection.WEB_CONNECTION).set(client);
        String newUri = request.uri();

        if (newUri.contains("//")) {
            newUri = newUri.replace("//", "/");
            newUri = newUri.replace("//", "/");
            //client.redirect(newUri);
            //ctx.channel().writeAndFlush(client.response());
            //return;
        }

        client.setRequestHandled(false);

        if (request.headers().contains(HttpHeaderNames.REFERER)) {
            var referrer = request.headers().get(HttpHeaderNames.REFERER);
            var matches = PageRules.getInstance().matchesRule(referrer);

            if (matches != null) {
                client.movedpermanently(PageRules.getInstance().getNewUrl(matches, referrer));
                ctx.channel().writeAndFlush(client.response());
                return;
            }
        }

        final Route rawRoute = RouteManager.getRoute(client, "");

        try {
            newUri = URLDecoder.decode(newUri, StandardCharsets.UTF_8);
        } catch (Exception ex) {

        }

        final Route route = RouteManager.getRoute(client, newUri);

        if (rawRoute != null) {
            if (route != null) {
                client.setRequestHandled(true);
            }

            try {
                rawRoute.handleRoute(client);
            } catch (Exception ex) {
                client.send(Settings.getInstance().getDefaultResponses().getErrorResponse(client, ex));
            }
        }

        if (route != null) {
            if (client.response() == null) {
                if (request.uri().contains("//")) {
                    client.redirect(newUri);
                    ctx.channel().writeAndFlush(client.response());
                    client.tryDisposeResponse();
                    return;
                }

                try {
                    route.handleRoute(client);
                } catch (Exception ex) {
                    client.send(Settings.getInstance().getDefaultResponses().getErrorResponse(client, ex));
                }

                if (client.isFileSent()) {
                    client.setFileSent(false);
                    client.tryDisposeResponse();
                    return;
                }
            }
        }

        if (client.response() == null) {
            client.tryDisposeResponse();

            if (!ResponseBuilder.create(client, request)) {
                client.send(Settings.getInstance().getDefaultResponses().getResponse(HttpResponseStatus.NOT_FOUND, client));
            } else {
                return;
            }
        }

        var response = client.response();

        if (HttpUtil.isKeepAlive(request)) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        client.cookies().encodeCookies(response);
        ctx.channel().writeAndFlush(response);
        client.tryDisposeResponse();
        /*} else {
            super.channelRead(ctx, msg);
        }*/
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

            ctx.channel().writeAndFlush(Settings.getInstance().getDefaultResponses().getErrorResponse(client, cause));
        }
    }
}