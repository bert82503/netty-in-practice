package io.netty.example.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler implementation for the echo client.  It initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 * echo客户端处理器实现，通过向服务器发送第一条消息来启动echo客户端和服务器之间的反复交换/乒乓流量。
 *
 * @since 2019-06-02
 */
class EchoClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(EchoClientHandler.class);

//    private static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));

    private final ByteBuf firstMessage;

    /**
     * Creates a client-side handler.
     */
    EchoClientHandler() {
        logger.info("Create EchoClientHandler");

//        firstMessage = Unpooled.buffer(SIZE);
//        for (int i = 0; i < firstMessage.capacity(); i++) {
//            firstMessage.writeByte(i);
//        }

        firstMessage = Unpooled.buffer(16);
        firstMessage.writeBytes("ping-pong".getBytes(StandardCharsets.UTF_8));
    }

    // 打开服务器连接

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("channelActive");
        // Sends one message when a connection is open
        ctx.writeAndFlush(firstMessage);
    }

    // 接收到服务器的响应数据

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        logger.info("channelRead, msg:{}", msg);
        // echoes back any received data to the server
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
