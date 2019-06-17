package io.netty.example.objectecho;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles both client-side and server-side handler depending on which
 * constructor was called.
 *
 * @since 2019-06-03
 */
class ObjectEchoServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ObjectEchoServerHandler.class);

    ObjectEchoServerHandler() {
        logger.info("Create ObjectEchoServerHandler");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        logger.info("channelRead");
        // Echo back the received object to the client.
        // 将收到的对象回送给客户端
        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        logger.info("channelReadComplete");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        logger.warn("exception caught", cause);
        ctx.close();
    }
}
