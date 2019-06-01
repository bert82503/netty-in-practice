package io.netty.example.bootstrap;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 引导服务器。
 *
 * @author edwardlee03.lihg
 */
public class NioTcpServer {
    private static final Logger logger = LoggerFactory.getLogger(NioTcpServer.class);

    /**
     * 服务器的引导过程。
     *
     * {@code ChannelPipelineException: io.netty.example.bootstrap.NioTcpServer$1 is not a @Sharable handler, so can't be added or removed multiple times.}
     */
    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();
        // 引导服务器
        ServerBootstrap bootstrap = new ServerBootstrap();
        // 设置EventLoopGroup，提供用于处理Channel事件的EventLoop
        bootstrap.group(group)
                // 指定要使用的Channel实现
                .channel(NioServerSocketChannel.class)
                // 设置用于处理已被接受的子Channel的I/O及数据的ChannelInboundHandler
                .childHandler(new SimpleChannelInboundHandler<ByteBuf>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
                        logger.info("Received data: {}", msg.toString(StandardCharsets.UTF_8));
                    }
                });

        // 通过配置好的ServerBootstrap的实例绑定该Channel
        ChannelFuture channelFuture = bootstrap.bind(new InetSocketAddress(8080));
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                    logger.info("Server bound");
                } else {
                    logger.info("Bound attempt failed");
                    logger.info("Exception cause", future.cause());
                }
            }
        });
    }
}
