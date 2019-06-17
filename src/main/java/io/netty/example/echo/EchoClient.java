package io.netty.example.echo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Sends one message when a connection is open and echoes back any received
 * data to the server.  Simply put, the echo client initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 * 在连接打开时发送一条消息，并将任何接收到的数据回送给服务器。
 * 简单地说，echo客户端通过向服务器发送第一条消息来启动echo客户端和服务器之间的反复交换/乒乓流量。
 *
 * @since 2019-06-02
 */
public final class EchoClient {
    private static final String HOST = System.getProperty("host", "127.0.0.1");
    private static final int PORT = Integer.parseInt(System.getProperty("port", "8007"));
    
    public static void main(String[] args) throws InterruptedException {
        // Configure the client.
        // 配置引导客户端
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new EchoClientHandler());
                        }
                    });

            // Start the client.
            // 启动引导客户端
            ChannelFuture future = bootstrap.connect(HOST, PORT).sync();

            // Wait until the connection is closed.
            // 等待直到连接关闭
            future.channel().closeFuture().sync();
        } finally {
            // Shutdown the event loop to terminate all threads.
            // 优雅地关闭连接事件处理链，终止所有线程
            group.shutdownGracefully();
        }
    }
}
