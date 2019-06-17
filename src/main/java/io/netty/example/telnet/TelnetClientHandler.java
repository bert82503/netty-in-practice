package io.netty.example.telnet;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles a client-side channel.
 * 处理客户端连接套接字。
 *
 * @since 2019-06-09
 */
@Sharable
class TelnetClientHandler extends SimpleChannelInboundHandler<String> {
    private static final Logger logger = LoggerFactory.getLogger(TelnetClientHandler.class);

    TelnetClientHandler() {
        logger.info("Create TelnetClientHandler");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        System.out.println(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        logger.warn("exception caught", cause);
        ctx.close();
    }
}
