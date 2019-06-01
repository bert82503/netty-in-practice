package io.netty.example.bootstrap;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 从Channel引导客户端，实现EventLoop共享。
 *
 * 编写Netty应用程序的一般准则：尽可能地重用EventLoop，以减少线程创建所带来的开销。
 *
 * @since 2019-06-02
 */
public class EventLoopSharedChannelServer {
    private static final Logger logger = LoggerFactory.getLogger(EventLoopSharedChannelServer.class);

    public static void main(String[] args) {
        // 引导服务器
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        // 设置EventLoopGroup，提供用于处理Channel事件的EventLoop
        serverBootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new SimpleChannelInboundHandler<ByteBuf>() {
                    private ChannelFuture connectFuture;

                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        // 创建一个Bootstrap类的实例，以连接到远程主机
                        Bootstrap bootstrap = new Bootstrap();
                        // 指定Channel的实现
                        bootstrap.channel(NioSocketChannel.class)
                                // 为入站I/O设置ChannelInboundHandler
                                .handler(new SimpleChannelInboundHandler<ByteBuf>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
                                        logger.info("Received data: {}", msg.toString(StandardCharsets.UTF_8));
                                    }
                                });
                        // 使用与分配给已被接受的子Channel相同的EventLoop
                        bootstrap.group(ctx.channel().eventLoop());
                        // 连接到远程节点
                        connectFuture = bootstrap.connect(
                                new InetSocketAddress("www.baidu.com", 80));
                    }

                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                        if (connectFuture.isDone() || connectFuture.isSuccess()) {
                            // 当连接完成时，执行一些数据操作（如代理）
                            // do something with the data
                            logger.info("Connection established");
                        }
                    }
                });
        ChannelFuture channelFuture = serverBootstrap.bind(new InetSocketAddress(8080));
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                    logger.info("Server bound");
                } else {
                    logger.info("Bind attempt failed");
                    logger.info("Exception cause", future.cause());
                }
            }
        });
    }
}
