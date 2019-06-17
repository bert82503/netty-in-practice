package io.netty.example.telnet;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a newly configured {@link ChannelPipeline} for a new channel.
 * 为新的连接套接字创建新配置的{@link ChannelPipeline}。
 *
 * @since 2019-06-09
 */
class TelnetClientInitializer extends ChannelInitializer<SocketChannel> {
    private static final Logger logger = LoggerFactory.getLogger(TelnetClientInitializer.class);

    private static final StringDecoder DECODER = new StringDecoder(StandardCharsets.UTF_8);
    private static final StringEncoder ENCODER = new StringEncoder(StandardCharsets.UTF_8);

    private static final TelnetClientHandler CLIENT_HANDLER = new TelnetClientHandler();

    TelnetClientInitializer() {
        logger.info("Create TelnetClientInitializer");
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        logger.info("initChannel");

        ChannelPipeline pipeline = ch.pipeline();

        // Add the text line codec combination first,
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast(DECODER, ENCODER);

        // and then business logic.
        pipeline.addLast(CLIENT_HANDLER);
    }
}
