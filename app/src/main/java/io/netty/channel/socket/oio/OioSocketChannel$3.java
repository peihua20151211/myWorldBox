package io.netty.channel.socket.oio;

import io.netty.channel.ChannelPromise;

class OioSocketChannel$3 implements Runnable {
    final /* synthetic */ OioSocketChannel this$0;
    final /* synthetic */ ChannelPromise val$promise;

    OioSocketChannel$3(OioSocketChannel oioSocketChannel, ChannelPromise channelPromise) {
        this.this$0 = oioSocketChannel;
        this.val$promise = channelPromise;
    }

    public void run() {
        OioSocketChannel.access$200(this.this$0, this.val$promise);
    }
}
