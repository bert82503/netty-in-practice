package io.netty.example.telnet;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Simplistic telnet client.
 * telnet客户端。
 *
 * @since 2019-06-09
 */
public final class TelnetClient {
    private static final String HOST = System.getProperty("host", "127.0.0.1");
    private static final int PORT = Integer.parseInt(System.getProperty("port", "8023"));
    private static final String BYE = "bye";

    public static void main(String[] args) throws InterruptedException, IOException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new TelnetClientInitializer());

            // Start the connection attempt.
            // 开始连接尝试
            Channel ch = bootstrap.connect(HOST, PORT).sync().channel();

            // Read commands from the stdin.
            // 从标准输入读取命令
            ChannelFuture lastWriteFuture = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            for (; ; ) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }

                // Sends the received line to the server.
                // 将收到的数据发送到服务器
                lastWriteFuture = ch.writeAndFlush(line + System.lineSeparator());

                // If user typed the 'bye' command, wait until the server closes the connection.
                // 等到服务器关闭连接
                if (BYE.equalsIgnoreCase(line)) {
                    ch.closeFuture().sync();
                    break;
                }
            }

            // Wait until all messages are flushed before closing the channel.
            // 等到所有消息都被刷新后，再关闭连接套接字
            if (lastWriteFuture != null) {
                lastWriteFuture.sync();
            }
        } finally {
            group.shutdownGracefully();
        }
    }
}
