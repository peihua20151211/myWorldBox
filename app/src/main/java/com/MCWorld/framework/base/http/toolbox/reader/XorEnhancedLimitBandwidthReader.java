package com.MCWorld.framework.base.http.toolbox.reader;

import android.support.v4.media.session.PlaybackStateCompat;
import com.MCWorld.framework.base.http.io.impl.request.DownloadRequest;
import com.MCWorld.framework.base.http.io.impl.request.SegmentRequest;
import com.MCWorld.framework.base.http.toolbox.error.LocalFileError;
import com.MCWorld.framework.base.http.toolbox.error.VolleyError;
import com.MCWorld.framework.base.utils.ByteArrayPool;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import org.bytedeco.javacpp.avcodec;

public class XorEnhancedLimitBandwidthReader extends DownloadReader {
    private static final int DEFALUT_BUFFER_SIZE = 8192;
    public static final long DEFAULT_READ_INTERVAL_IN_MS = 100;
    private static final long LIMIT_TIME_INTERVAL = 100;
    private static final String TAG = "XorEnhancedLimitBandwidthReader";
    private int KEY_BYTE_0 = 0;
    private int KEY_BYTE_1 = 0;
    private int KEY_BYTE_2 = avcodec.AV_CODEC_ID_HQX;
    private int KEY_BYTE_3 = 252;
    private byte[] buffer;
    private int bufferSize = 8192;
    private InputStream mInputStream;
    private int mLimitRate = Integer.MAX_VALUE;
    private IAdapterToStreamAndRaf mOutputStream;
    private WeakReference<DownloadRequest> mRequest;
    private long mStartLength;

    public XorEnhancedLimitBandwidthReader(ByteArrayPool byteArrayPool) {
        super(byteArrayPool);
    }

    public void setRequest(DownloadRequest request) {
        this.mRequest = new WeakReference(request);
        if (request instanceof SegmentRequest) {
            setLimitRate(((SegmentRequest) request).getRate());
        }
    }

    private void setLimitRate(int rate) {
        if (rate > 0) {
            this.mLimitRate = rate * 1000;
        }
    }

    public void setStartLength(long length) {
        this.mStartLength = length;
    }

    public <E extends Throwable, T extends Throwable> void copy(InputStream inputStream, IAdapterToStreamAndRaf outputStream, IReaderCallback<E, T> callback) throws IOException, Throwable, Throwable, VolleyError {
        this.buffer = this.mByteArrayPool.getBuf(8192);
        this.mInputStream = inputStream;
        this.mOutputStream = outputStream;
        RateLimiter limiter = RateLimiter.create(this.mLimitRate);
        while (true) {
            int count = inputStream.read(this.buffer);
            if (count == -1) {
                break;
            }
            int bufferIndex = 0;
            while (bufferIndex < count) {
                int i;
                int bufferIndex2;
                if (this.mStartLength < PlaybackStateCompat.ACTION_PLAY_FROM_URI) {
                    int left = count - bufferIndex;
                    byte[] decoded;
                    if (left < 4) {
                        decoded = new byte[left];
                        i = 0;
                        while (i < decoded.length) {
                            bufferIndex2 = bufferIndex + 1;
                            decoded[i] = (byte) (this.buffer[bufferIndex] ^ getXorValue(i));
                            i++;
                            bufferIndex = bufferIndex2;
                        }
                        this.mStartLength += (long) decoded.length;
                        try {
                            outputStream.write(decoded, 0, decoded.length);
                        } catch (IOException e) {
                            throw new LocalFileError(e);
                        }
                    }
                    int remainder = (int) (this.mStartLength % 4);
                    decoded = new byte[(4 - remainder)];
                    i = remainder;
                    int j = 0;
                    bufferIndex2 = bufferIndex;
                    while (i < 4) {
                        bufferIndex = bufferIndex2 + 1;
                        decoded[j] = (byte) (this.buffer[bufferIndex2] ^ getXorValue(i));
                        i++;
                        j++;
                        bufferIndex2 = bufferIndex;
                    }
                    this.mStartLength += (long) decoded.length;
                    try {
                        outputStream.write(decoded, 0, decoded.length);
                        bufferIndex = bufferIndex2;
                    } catch (IOException e2) {
                        throw new LocalFileError(e2);
                    }
                } else if (bufferIndex != 0) {
                    int length = count - bufferIndex;
                    byte[] data = new byte[length];
                    i = 0;
                    bufferIndex2 = bufferIndex;
                    while (i < length) {
                        bufferIndex = bufferIndex2 + 1;
                        data[i] = this.buffer[bufferIndex2];
                        i++;
                        bufferIndex2 = bufferIndex;
                    }
                    this.mStartLength += (long) count;
                    try {
                        outputStream.write(data, 0, data.length);
                        bufferIndex = bufferIndex2;
                    } catch (IOException e22) {
                        throw new LocalFileError(e22);
                    }
                } else {
                    this.mStartLength += (long) count;
                    bufferIndex += count;
                    try {
                        outputStream.write(this.buffer, 0, count);
                    } catch (IOException e222) {
                        throw new LocalFileError(e222);
                    }
                }
            }
            if (callback != null) {
                callback.readLoop(count);
            }
            if (LimitFlag.RATE_LIMIT) {
                limiter.acquire(count);
            }
        }
        if (callback != null) {
            callback.end();
        }
    }

    public void close() throws IOException {
        this.mOutputStream.flush();
        this.mOutputStream.close();
        this.mOutputStream = null;
        this.mByteArrayPool.returnBuf(this.buffer);
        this.mInputStream.close();
        this.mInputStream = null;
    }

    private byte getXorValue(int index) {
        switch (index) {
            case 0:
                return (byte) this.KEY_BYTE_0;
            case 1:
                return (byte) this.KEY_BYTE_1;
            case 2:
                return (byte) this.KEY_BYTE_2;
            default:
                return (byte) this.KEY_BYTE_3;
        }
    }
}
