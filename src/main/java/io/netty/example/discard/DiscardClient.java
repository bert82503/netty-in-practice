package io.netty.example.discard;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Keeps sending random data to the specified address.
 * 持续地发送随机数据到指定的地址。
 *
 * @since 2019-06-04
 */
public final class DiscardClient {
    private static final String HOST = System.getProperty("host", "127.0.0.1");
    private static final int PORT = Integer.parseInt(System.getProperty("port", "8009"));

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 引导客户端
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new DiscardClientHandler());
                        }
                    });

            // Make the connection attempt.
            // 进行连接尝试
            ChannelFuture future = bootstrap.connect(HOST, PORT).sync();

            // Wait until the connection is closed.
            // 等待直到连接关闭
            future.channel().closeFuture().sync();
        } finally {
            // 优雅地关闭连接事件处理链
            group.shutdownGracefully();
        }
    }
}
