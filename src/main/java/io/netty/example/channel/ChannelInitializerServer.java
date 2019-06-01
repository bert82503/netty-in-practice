package io.netty.example.channel;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;

/**
 * 引导和使用{@link ChannelInitializer}。
 *
 * @since 2019-06-02
 */
public class ChannelInitializerServer {
    /**
     * 在引导过程中添加多个ChannelHandler。
     */
    public static void main(String[] args) throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                // 注册一个ChannelInitializerImpl类的实例来设置ChannelPipeline
                .childHandler(new ChannelInitializerImpl());
        ChannelFuture channelFuture = bootstrap.bind(8080);
        channelFuture.sync();
    }

    /**
     * 用于设置{@link ChannelPipeline}的自定义{@link ChannelInitializer}实现。
     */
    final static class ChannelInitializerImpl extends ChannelInitializer<Channel> {
        @Override
        protected void initChannel(Channel ch) throws Exception {
            // 将所需的ChannelHandler添加到ChannelPipeline
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new HttpClientCodec(), new HttpObjectAggregator(Integer.MAX_VALUE));
        }
    }
}
