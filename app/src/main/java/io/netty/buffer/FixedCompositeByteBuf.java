package io.netty.buffer;

import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.RecyclableArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.util.Collections;

final class FixedCompositeByteBuf extends AbstractReferenceCountedByteBuf {
    private static final ByteBuf[] EMPTY = new ByteBuf[]{Unpooled.EMPTY_BUFFER};
    private final ByteBufAllocator allocator;
    private final Object[] buffers;
    private final int capacity;
    private final boolean direct;
    private final int nioBufferCount;
    private final ByteOrder order;

    private static final class Component {
        private final ByteBuf buf;
        private final int endOffset;
        private final int index;
        private final int offset;

        Component(int index, int offset, ByteBuf buf) {
            this.index = index;
            this.offset = offset;
            this.endOffset = buf.readableBytes() + offset;
            this.buf = buf;
        }
    }

    FixedCompositeByteBuf(ByteBufAllocator allocator, ByteBuf... buffers) {
        super(Integer.MAX_VALUE);
        if (buffers.length == 0) {
            this.buffers = EMPTY;
            this.order = ByteOrder.BIG_ENDIAN;
            this.nioBufferCount = 1;
            this.capacity = 0;
            this.direct = false;
        } else {
            ByteBuf b = buffers[0];
            this.buffers = new Object[buffers.length];
            this.buffers[0] = b;
            boolean direct = true;
            int nioBufferCount = b.nioBufferCount();
            int capacity = b.readableBytes();
            this.order = b.order();
            for (int i = 1; i < buffers.length; i++) {
                b = buffers[i];
                if (buffers[i].order() != this.order) {
                    throw new IllegalArgumentException("All ByteBufs need to have same ByteOrder");
                }
                nioBufferCount += b.nioBufferCount();
                capacity += b.readableBytes();
                if (!b.isDirect()) {
                    direct = false;
                }
                this.buffers[i] = b;
            }
            this.nioBufferCount = nioBufferCount;
            this.capacity = capacity;
            this.direct = direct;
        }
        setIndex(0, capacity());
        this.allocator = allocator;
    }

    public boolean isWritable() {
        return false;
    }

    public boolean isWritable(int size) {
        return false;
    }

    public ByteBuf discardReadBytes() {
        throw new ReadOnlyBufferException();
    }

    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        throw new ReadOnlyBufferException();
    }

    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        throw new ReadOnlyBufferException();
    }

    public ByteBuf setBytes(int index, ByteBuffer src) {
        throw new ReadOnlyBufferException();
    }

    public ByteBuf setByte(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    protected void _setByte(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    public ByteBuf setShort(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    protected void _setShort(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    protected void _setShortLE(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    public ByteBuf setMedium(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    protected void _setMedium(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    protected void _setMediumLE(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    public ByteBuf setInt(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    protected void _setInt(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    protected void _setIntLE(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    public ByteBuf setLong(int index, long value) {
        throw new ReadOnlyBufferException();
    }

    protected void _setLong(int index, long value) {
        throw new ReadOnlyBufferException();
    }

    protected void _setLongLE(int index, long value) {
        throw new ReadOnlyBufferException();
    }

    public int setBytes(int index, InputStream in, int length) {
        throw new ReadOnlyBufferException();
    }

    public int setBytes(int index, ScatteringByteChannel in, int length) {
        throw new ReadOnlyBufferException();
    }

    public int setBytes(int index, FileChannel in, long position, int length) {
        throw new ReadOnlyBufferException();
    }

    public int capacity() {
        return this.capacity;
    }

    public int maxCapacity() {
        return this.capacity;
    }

    public ByteBuf capacity(int newCapacity) {
        throw new ReadOnlyBufferException();
    }

    public ByteBufAllocator alloc() {
        return this.allocator;
    }

    public ByteOrder order() {
        return this.order;
    }

    public ByteBuf unwrap() {
        return null;
    }

    public boolean isDirect() {
        return this.direct;
    }

    private Component findComponent(int index) {
        int readable = 0;
        int i = 0;
        while (i < this.buffers.length) {
            ByteBuf b;
            boolean isBuffer;
            Component comp = null;
            ByteBuf obj = this.buffers[i];
            if (obj instanceof ByteBuf) {
                b = obj;
                isBuffer = true;
            } else {
                comp = (Component) obj;
                b = comp.buf;
                isBuffer = false;
            }
            readable += b.readableBytes();
            if (index >= readable) {
                i++;
            } else if (!isBuffer) {
                return comp;
            } else {
                comp = new Component(i, readable - b.readableBytes(), b);
                this.buffers[i] = comp;
                return comp;
            }
        }
        throw new IllegalStateException();
    }

    private ByteBuf buffer(int i) {
        Object obj = this.buffers[i];
        if (obj instanceof ByteBuf) {
            return (ByteBuf) obj;
        }
        return ((Component) obj).buf;
    }

    public byte getByte(int index) {
        return _getByte(index);
    }

    protected byte _getByte(int index) {
        Component c = findComponent(index);
        return c.buf.getByte(index - c.offset);
    }

    protected short _getShort(int index) {
        Component c = findComponent(index);
        if (index + 2 <= c.endOffset) {
            return c.buf.getShort(index - c.offset);
        }
        if (order() == ByteOrder.BIG_ENDIAN) {
            return (short) (((_getByte(index) & 255) << 8) | (_getByte(index + 1) & 255));
        }
        return (short) ((_getByte(index) & 255) | ((_getByte(index + 1) & 255) << 8));
    }

    protected short _getShortLE(int index) {
        Component c = findComponent(index);
        if (index + 2 <= c.endOffset) {
            return c.buf.getShortLE(index - c.offset);
        }
        if (order() == ByteOrder.BIG_ENDIAN) {
            return (short) ((_getByte(index) & 255) | ((_getByte(index + 1) & 255) << 8));
        }
        return (short) (((_getByte(index) & 255) << 8) | (_getByte(index + 1) & 255));
    }

    protected int _getUnsignedMedium(int index) {
        Component c = findComponent(index);
        if (index + 3 <= c.endOffset) {
            return c.buf.getUnsignedMedium(index - c.offset);
        }
        if (order() == ByteOrder.BIG_ENDIAN) {
            return ((_getShort(index) & 65535) << 8) | (_getByte(index + 2) & 255);
        }
        return (_getShort(index) & 65535) | ((_getByte(index + 2) & 255) << 16);
    }

    protected int _getUnsignedMediumLE(int index) {
        Component c = findComponent(index);
        if (index + 3 <= c.endOffset) {
            return c.buf.getUnsignedMediumLE(index - c.offset);
        }
        if (order() == ByteOrder.BIG_ENDIAN) {
            return (_getShortLE(index) & 65535) | ((_getByte(index + 2) & 255) << 16);
        }
        return ((_getShortLE(index) & 65535) << 8) | (_getByte(index + 2) & 255);
    }

    protected int _getInt(int index) {
        Component c = findComponent(index);
        if (index + 4 <= c.endOffset) {
            return c.buf.getInt(index - c.offset);
        }
        if (order() == ByteOrder.BIG_ENDIAN) {
            return ((_getShort(index) & 65535) << 16) | (_getShort(index + 2) & 65535);
        }
        return (_getShort(index) & 65535) | ((_getShort(index + 2) & 65535) << 16);
    }

    protected int _getIntLE(int index) {
        Component c = findComponent(index);
        if (index + 4 <= c.endOffset) {
            return c.buf.getIntLE(index - c.offset);
        }
        if (order() == ByteOrder.BIG_ENDIAN) {
            return (_getShortLE(index) & 65535) | ((_getShortLE(index + 2) & 65535) << 16);
        }
        return ((_getShortLE(index) & 65535) << 16) | (_getShortLE(index + 2) & 65535);
    }

    protected long _getLong(int index) {
        Component c = findComponent(index);
        if (index + 8 <= c.endOffset) {
            return c.buf.getLong(index - c.offset);
        }
        if (order() == ByteOrder.BIG_ENDIAN) {
            return ((((long) _getInt(index)) & 4294967295L) << 32) | (((long) _getInt(index + 4)) & 4294967295L);
        }
        return (((long) _getInt(index)) & 4294967295L) | ((((long) _getInt(index + 4)) & 4294967295L) << 32);
    }

    protected long _getLongLE(int index) {
        Component c = findComponent(index);
        if (index + 8 <= c.endOffset) {
            return c.buf.getLongLE(index - c.offset);
        }
        if (order() == ByteOrder.BIG_ENDIAN) {
            return (((long) _getIntLE(index)) & 4294967295L) | ((((long) _getIntLE(index + 4)) & 4294967295L) << 32);
        }
        return ((((long) _getIntLE(index)) & 4294967295L) << 32) | (((long) _getIntLE(index + 4)) & 4294967295L);
    }

    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        checkDstIndex(index, length, dstIndex, dst.length);
        if (length != 0) {
            Component c = findComponent(index);
            int i = c.index;
            int adjustment = c.offset;
            ByteBuf s = c.buf;
            while (true) {
                int localLength = Math.min(length, s.readableBytes() - (index - adjustment));
                s.getBytes(index - adjustment, dst, dstIndex, localLength);
                index += localLength;
                dstIndex += localLength;
                length -= localLength;
                adjustment += s.readableBytes();
                if (length <= 0) {
                    break;
                }
                i++;
                s = buffer(i);
            }
        }
        return this;
    }

    public ByteBuf getBytes(int index, ByteBuffer dst) {
        int limit = dst.limit();
        int length = dst.remaining();
        checkIndex(index, length);
        if (length != 0) {
            try {
                Component c = findComponent(index);
                int i = c.index;
                int adjustment = c.offset;
                ByteBuf s = c.buf;
                while (true) {
                    int localLength = Math.min(length, s.readableBytes() - (index - adjustment));
                    dst.limit(dst.position() + localLength);
                    s.getBytes(index - adjustment, dst);
                    index += localLength;
                    length -= localLength;
                    adjustment += s.readableBytes();
                    if (length <= 0) {
                        break;
                    }
                    i++;
                    s = buffer(i);
                }
                dst.limit(limit);
            } catch (Throwable th) {
                dst.limit(limit);
            }
        }
        return this;
    }

    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        checkDstIndex(index, length, dstIndex, dst.capacity());
        if (length != 0) {
            Component c = findComponent(index);
            int i = c.index;
            int adjustment = c.offset;
            ByteBuf s = c.buf;
            while (true) {
                int localLength = Math.min(length, s.readableBytes() - (index - adjustment));
                s.getBytes(index - adjustment, dst, dstIndex, localLength);
                index += localLength;
                dstIndex += localLength;
                length -= localLength;
                adjustment += s.readableBytes();
                if (length <= 0) {
                    break;
                }
                i++;
                s = buffer(i);
            }
        }
        return this;
    }

    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        if (nioBufferCount() == 1) {
            return out.write(internalNioBuffer(index, length));
        }
        long writtenBytes = out.write(nioBuffers(index, length));
        if (writtenBytes > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int) writtenBytes;
    }

    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        if (nioBufferCount() == 1) {
            return out.write(internalNioBuffer(index, length), position);
        }
        long writtenBytes = 0;
        for (ByteBuffer buf : nioBuffers(index, length)) {
            writtenBytes += (long) out.write(buf, position + writtenBytes);
        }
        if (writtenBytes > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int) writtenBytes;
    }

    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        checkIndex(index, length);
        if (length != 0) {
            Component c = findComponent(index);
            int i = c.index;
            int adjustment = c.offset;
            ByteBuf s = c.buf;
            while (true) {
                int localLength = Math.min(length, s.readableBytes() - (index - adjustment));
                s.getBytes(index - adjustment, out, localLength);
                index += localLength;
                length -= localLength;
                adjustment += s.readableBytes();
                if (length <= 0) {
                    break;
                }
                i++;
                s = buffer(i);
            }
        }
        return this;
    }

    public ByteBuf copy(int index, int length) {
        checkIndex(index, length);
        boolean release = true;
        ByteBuf buf = alloc().buffer(length);
        try {
            buf.writeBytes(this, index, length);
            release = false;
            return buf;
        } finally {
            if (release) {
                buf.release();
            }
        }
    }

    public int nioBufferCount() {
        return this.nioBufferCount;
    }

    public ByteBuffer nioBuffer(int index, int length) {
        checkIndex(index, length);
        if (this.buffers.length == 1) {
            ByteBuf buf = buffer(0);
            if (buf.nioBufferCount() == 1) {
                return buf.nioBuffer(index, length);
            }
        }
        ByteBuffer merged = ByteBuffer.allocate(length).order(order());
        ByteBuffer[] buffers = nioBuffers(index, length);
        for (ByteBuffer put : buffers) {
            merged.put(put);
        }
        merged.flip();
        return merged;
    }

    public ByteBuffer internalNioBuffer(int index, int length) {
        if (this.buffers.length == 1) {
            return buffer(0).internalNioBuffer(index, length);
        }
        throw new UnsupportedOperationException();
    }

    public ByteBuffer[] nioBuffers(int index, int length) {
        checkIndex(index, length);
        if (length == 0) {
            return EmptyArrays.EMPTY_BYTE_BUFFERS;
        }
        RecyclableArrayList array = RecyclableArrayList.newInstance(this.buffers.length);
        try {
            Component c = findComponent(index);
            int i = c.index;
            int adjustment = c.offset;
            ByteBuf s = c.buf;
            while (true) {
                int localLength = Math.min(length, s.readableBytes() - (index - adjustment));
                switch (s.nioBufferCount()) {
                    case 0:
                        throw new UnsupportedOperationException();
                    case 1:
                        array.add(s.nioBuffer(index - adjustment, localLength));
                        break;
                    default:
                        Collections.addAll(array, s.nioBuffers(index - adjustment, localLength));
                        break;
                }
                index += localLength;
                length -= localLength;
                adjustment += s.readableBytes();
                if (length <= 0) {
                    ByteBuffer[] byteBufferArr = (ByteBuffer[]) array.toArray(new ByteBuffer[array.size()]);
                    return byteBufferArr;
                }
                i++;
                s = buffer(i);
            }
        } finally {
            array.recycle();
        }
    }

    public boolean hasArray() {
        return false;
    }

    public byte[] array() {
        throw new UnsupportedOperationException();
    }

    public int arrayOffset() {
        throw new UnsupportedOperationException();
    }

    public boolean hasMemoryAddress() {
        return false;
    }

    public long memoryAddress() {
        throw new UnsupportedOperationException();
    }

    protected void deallocate() {
        for (int i = 0; i < this.buffers.length; i++) {
            buffer(i).release();
        }
    }

    public String toString() {
        String result = super.toString();
        return result.substring(0, result.length() - 1) + ", components=" + this.buffers.length + ')';
    }
}
