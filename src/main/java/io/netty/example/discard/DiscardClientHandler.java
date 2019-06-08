package io.netty.example.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles a client-side channel.
 * 处理客户端的套接字连接。
 *
 * @since 2019-06-04
 */
class DiscardClientHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger logger = LoggerFactory.getLogger(DiscardClientHandler.class);

    private static final int SIZE = Integer.parseInt(System.getProperty("size", "3"));

    private ByteBuf content;
    private ChannelHandlerContext ctx;

    DiscardClientHandler() {
        logger.info("Create DiscardClientHandler");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("channelActive");
        this.ctx = ctx;

        // Initialize the message.
        content = ctx.alloc().directBuffer(SIZE).writeZero(SIZE);

        // Send the initial messages.
        generateTraffic();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.info("channelInactive");
        content.release();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        // Server is supposed to send nothing, but if it sends something, discard it.
        logger.info("channelRead0");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        // 当引发异常时，关闭连接
        logger.warn("exception caught", cause);
        ctx.close();
    }

    private void generateTraffic() {
        logger.info("generateTraffic");
        // Flush the outbound buffer to the socket.
        // Once flushed, generate the same amount of traffic again.
        ctx.writeAndFlush(content.duplicate().retain())
                .addListener(trafficGenerator);
    }

    private final ChannelFutureListener trafficGenerator = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) {
            logger.info("operationComplete");
            if (future.isSuccess()) {
                generateTraffic();
            } else {
                logger.warn("exception caught", future.cause());
                future.channel().close();
            }
        }
    };
}
