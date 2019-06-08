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
 *
 * @since 2019-06-08
 */
class TelnetServerInitializer extends ChannelInitializer<SocketChannel> {
    private static final Logger logger = LoggerFactory.getLogger(TelnetServerInitializer.class);

    private static final StringDecoder DECODER = new StringDecoder(StandardCharsets.UTF_8);
    private static final StringEncoder ENCODER = new StringEncoder(StandardCharsets.UTF_8);

    private static final TelnetServerHandler SERVER_HANDLER = new TelnetServerHandler();

    TelnetServerInitializer() {
        logger.info("Create TelnetServerInitializer");
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        logger.info("initChannel");
        ChannelPipeline pipeline = ch.pipeline();

        // Add the text line codec combination first,
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        // the encoder and decoder are static as these are sharable
        pipeline.addLast(DECODER, ENCODER);

        // and then business logic.
        pipeline.addLast(SERVER_HANDLER);
    }
}
