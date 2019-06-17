package io.netty.example.localecho;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Local echo.
 *
 * @since 2019-06-02
 */
public final class LocalEcho {
    private static final Logger logger = LoggerFactory.getLogger(LocalEcho.class);

    private static final String PORT = System.getProperty("port", "test_port");

    public static void main(String[] args) throws InterruptedException, IOException {
        // Address to bind on / connect to.
        final LocalAddress address = new LocalAddress(PORT);

        EventLoopGroup serverGroup = new LocalEventLoopGroup();
        // NIO event loops are also OK
        EventLoopGroup clientGroup = new NioEventLoopGroup();
        try {
            // Note that we can use any event loop to ensure certain local channels
            // are handled by the same event loop thread which drives a certain socket channel
            // to reduce the communication latency between socket channels and local channels.
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(serverGroup)
                    .channel(LocalServerChannel.class)
                    .handler(new ChannelInitializer<LocalServerChannel>() {
                        @Override
                        protected void initChannel(LocalServerChannel ch) {
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                        }
                    })
                    .childHandler(new ChannelInitializer<LocalChannel>() {
                        @Override
                        protected void initChannel(LocalChannel ch) {
                            ch.pipeline().addLast(
                                    new LoggingHandler(LogLevel.INFO),
                                    new LocalEchoServerHandler());
                        }
                    });

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(clientGroup)
                    .channel(LocalChannel.class)
                    .handler(new ChannelInitializer<LocalChannel>() {
                        @Override
                        protected void initChannel(LocalChannel ch) {
                            ch.pipeline().addLast(
                                    new LoggingHandler(LogLevel.INFO),
                                    new LocalEchoClientHandler());
                        }
                    });

            // Start the server.
            // 启动引导服务器
            serverBootstrap.bind(address).sync();

            // Start the client.
            // 启动引导客户端
            Channel channel = bootstrap.connect(address).sync().channel();

            // Read commands from the stdin.
            // 从标准输入中读取命令
            logger.info("Enter text (quit to end)");
            ChannelFuture lastWriteFuture = null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            for (; ; ) {
                String line = reader.readLine();
                if (line == null || "quit".equalsIgnoreCase(line)) {
                    break;
                }

                // Sends the received line to the server.
                // 将收到的数据发送到服务器
                lastWriteFuture = channel.writeAndFlush(line);
            }

            // Wait until all messages are flushed before closing the channel.
            // 等到所有消息都被刷新后，再关闭连接套接字
            if (lastWriteFuture != null) {
                lastWriteFuture.awaitUninterruptibly();
            }
        } finally {
            serverGroup.shutdownGracefully();
            clientGroup.shutdownGracefully();
        }
    }
}
