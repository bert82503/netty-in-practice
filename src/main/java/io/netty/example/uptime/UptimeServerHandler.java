package io.netty.example.uptime;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles a server-side channel.
 *
 * @since 2019-06-09
 */
@Sharable
class UptimeServerHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger logger = LoggerFactory.getLogger(UptimeServerHandler.class);

    UptimeServerHandler() {
        logger.info("Create UptimeServerHandler");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        logger.info("channelRead0");
        // discard
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        logger.warn("exception caught", cause);
        ctx.close();
    }
}
