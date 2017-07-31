package io.netty.channel;

public class ChannelPipelineException extends ChannelException {
    private static final long serialVersionUID = 3379174210419885980L;

    public ChannelPipelineException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChannelPipelineException(String message) {
        super(message);
    }

    public ChannelPipelineException(Throwable cause) {
        super(cause);
    }
}
