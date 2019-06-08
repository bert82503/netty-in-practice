package io.netty.example.discard;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles a server-side channel.
 * 处理服务器端的套接字连接。
 *
 * @since 2019-06-04
 */
class DiscardServerHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger logger = LoggerFactory.getLogger(DiscardServerHandler.class);

    DiscardServerHandler() {
        logger.info("Create DiscardServerHandler");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        // discard
        // 丢弃任何收到的请求数据
        logger.info("channelRead0");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        // 当引发异常时，关闭连接
        logger.warn("exception caught", cause);
        ctx.close();
    }
}
