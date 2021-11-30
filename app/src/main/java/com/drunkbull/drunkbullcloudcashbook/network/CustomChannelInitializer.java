package com.drunkbull.drunkbullcloudcashbook.network;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;

public class CustomChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline channelPipeline = channel.pipeline();

        //解码和编码
        //传输协议 Protobuf
        channelPipeline.addLast(new CustomProtobufDecoder());

        //channelPipeline.addLast(new ProtobufEncoder());

        //客户端的逻辑
        channelPipeline.addLast("handler", new MessageHandler());

    }
}

