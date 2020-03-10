package io.netty.example.uptime;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Keep reconnecting to the server while printing out the current uptime and
 * connection attempt getStatus.
 *
 * @since 2019-06-17
 */
@Sharable
class UptimeClientHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger logger = LoggerFactory.getLogger(UptimeClientHandler.class);

    private long startTime = -1L;

    UptimeClientHandler() {
        logger.info("Create UptimeClientHandler");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (startTime < 0L) {
            startTime = System.currentTimeMillis();
        }
        logger.info("Connected to: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.info("Disconnected from: {}", ctx.channel().remoteAddress());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        // Discard received data
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (!(evt instanceof IdleStateEvent)) {
            return;
        }
        IdleStateEvent event = (IdleStateEvent) evt;
        if (event.state() == IdleState.READER_IDLE) {
            // The connection was OK but there was no traffic for last period.
            // 连接没问题，但上一段时间没有流量
            logger.info("Disconnecting due to no inbound traffic");
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        logger.warn("exception caught", cause);
        ctx.close();
    }
}
