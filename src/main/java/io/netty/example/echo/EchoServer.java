package io.netty.example.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Echoes back any received data from a client.
 * 回声从客户端收到的任何数据。
 *
 * @since 2019-06-02
 */
public final class EchoServer {
    private static final int PORT = Integer.parseInt(System.getProperty("port", "8007"));

    public static void main(String[] args) throws InterruptedException {
        // Configure the server.
        // 配置引导服务器
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new EchoServerHandler());
                        }
                    });

            // Start the server.
            // 启动引导服务器
            ChannelFuture future = serverBootstrap.bind(PORT).sync();

            // Wait until the server socket is closed.
            // 等待直到服务器套接字关闭
            future.channel().closeFuture().sync();
        } finally {
            // Shutdown all event loops to terminate all threads.
            // 优雅地关闭所有连接事件处理链，终止所有线程
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
