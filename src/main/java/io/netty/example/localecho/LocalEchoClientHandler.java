package io.netty.example.localecho;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler of local echo client.
 *
 * @since 2019-06-02
 */
class LocalEchoClientHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger logger = LoggerFactory.getLogger(LocalEchoClientHandler.class);

    LocalEchoClientHandler() {
        logger.info("Create LocalEchoClientHandler");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        logger.info("channelRead0");
        // Print as received
        logger.info("msg:{}", msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        logger.warn("exception caught", cause);
        ctx.close();
    }
}
