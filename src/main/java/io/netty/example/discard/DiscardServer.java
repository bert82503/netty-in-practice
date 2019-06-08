package io.netty.example.discard;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Discards any incoming data.
 * 丢弃任何传入的数据。
 *
 * @since 2019-06-04
 */
public final class DiscardServer {
    private static final int PORT = Integer.parseInt(System.getProperty("port", "8009"));

    public static void main(String[] args) throws InterruptedException {
        // 网络套接字连接的I/O操作事件处理链
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 引导服务器
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    // 设置"服务器套接字连接"类
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // 网络套接字连接的入站事件和出站操作的处理链的容器
                            ch.pipeline().addLast(new DiscardServerHandler());
                        }
                    });

            // Bind and start to accept incoming connections.
            // 绑定并开始接受传入的连接
            ChannelFuture future = serverBootstrap.bind(PORT).sync();

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            // 等待直到服务器套接字关闭
            // 优雅地关闭服务器
            future.channel().closeFuture().sync();
        } finally {
            // 优雅地关闭连接事件处理链
            // 释放所有的资源，关闭所有的当前正在使用中的Channel
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
