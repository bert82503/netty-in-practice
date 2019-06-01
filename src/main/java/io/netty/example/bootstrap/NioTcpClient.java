package io.netty.example.bootstrap;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * 使用NIO TCP传输的客户端。
 *
 * @author edwardlee03.lihg
 */
public class NioTcpClient {
    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 引导客户端
            // 创建一个Bootstrap类的实例，以创建和连接新的客户端Channel
            Bootstrap bootstrap = new Bootstrap();
            // 设置EventLoopGroup，提供用于处理Channel事件的EventLoop
            bootstrap.group(group)
                    // 指定要使用的Channel实现
                    .channel(NioSocketChannel.class)
                    // 设置用于Channel事件和数据的ChannelInboundHandler
                    .handler(new SimpleChannelInboundHandler<ByteBuf>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
                            System.out.println("Received data: " + msg.toString(StandardCharsets.UTF_8));
                        }
                    });

            // Make the connection attempt.
            // 连接到远程主机
            ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress("www.baidu.com", 80));
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    if (future.isSuccess()) {
                        System.out.println("Connection established");
                        future.channel().close();
                    } else {
                        System.err.println("Connection attempt failed");
                        future.cause().printStackTrace();
                    }
                }
            });
        } finally {
            // 优雅地关闭连接事件处理链
//            group.shutdownGracefully();
        }
    }
}
