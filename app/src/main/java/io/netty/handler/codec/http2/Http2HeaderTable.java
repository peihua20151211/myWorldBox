package io.netty.handler.codec.http2;

public interface Http2HeaderTable {
    int maxHeaderListSize();

    void maxHeaderListSize(int i) throws Http2Exception;

    int maxHeaderTableSize();

    void maxHeaderTableSize(int i) throws Http2Exception;
}
