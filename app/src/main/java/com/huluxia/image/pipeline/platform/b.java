package com.huluxia.image.pipeline.platform;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.os.Build.VERSION;
import com.huluxia.framework.base.utils.Preconditions;
import com.huluxia.framework.base.utils.Throwables;
import com.huluxia.image.base.imagepipeline.common.TooManyBitmapsException;
import com.huluxia.image.base.imagepipeline.image.d;
import com.huluxia.image.base.imagepipeline.memory.PooledByteBuffer;
import com.huluxia.image.pipeline.memory.a;
import com.huluxia.image.pipeline.memory.c;
import com.huluxia.image.pipeline.nativecode.Bitmaps;

/* compiled from: DalvikPurgeableDecoder */
abstract class b implements e {
    protected static final byte[] Ep = new byte[]{(byte) -1, (byte) -39};
    private final a IT;

    abstract Bitmap a(com.huluxia.image.core.common.references.a<PooledByteBuffer> aVar, int i, Options options);

    abstract Bitmap a(com.huluxia.image.core.common.references.a<PooledByteBuffer> aVar, Options options);

    b(c tracker) {
        this.IT = com.huluxia.image.pipeline.memory.b.a(tracker);
    }

    public com.huluxia.image.core.common.references.a<Bitmap> a(d encodedImage, Config bitmapConfig) {
        Options options = a(encodedImage.hU(), bitmapConfig);
        com.huluxia.image.core.common.references.a bytesRef = encodedImage.hS();
        Preconditions.checkNotNull(bytesRef);
        try {
            com.huluxia.image.core.common.references.a<Bitmap> m = m(a(bytesRef, options));
            return m;
        } finally {
            com.huluxia.image.core.common.references.a.c(bytesRef);
        }
    }

    public com.huluxia.image.core.common.references.a<Bitmap> a(d encodedImage, Config bitmapConfig, int length) {
        Options options = a(encodedImage.hU(), bitmapConfig);
        com.huluxia.image.core.common.references.a bytesRef = encodedImage.hS();
        Preconditions.checkNotNull(bytesRef);
        try {
            com.huluxia.image.core.common.references.a<Bitmap> m = m(a(bytesRef, length, options));
            return m;
        } finally {
            com.huluxia.image.core.common.references.a.c(bytesRef);
        }
    }

    private static Options a(int sampleSize, Config bitmapConfig) {
        Options options = new Options();
        options.inDither = true;
        options.inPreferredConfig = bitmapConfig;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inSampleSize = sampleSize;
        if (VERSION.SDK_INT >= 11) {
            options.inMutable = true;
        }
        return options;
    }

    protected static boolean a(com.huluxia.image.core.common.references.a<PooledByteBuffer> bytesRef, int length) {
        PooledByteBuffer buffer = (PooledByteBuffer) bytesRef.get();
        return length >= 2 && buffer.bs(length - 2) == (byte) -1 && buffer.bs(length - 1) == (byte) -39;
    }

    public com.huluxia.image.core.common.references.a<Bitmap> m(Bitmap bitmap) {
        try {
            Bitmaps.k(bitmap);
            if (this.IT.f(bitmap)) {
                return com.huluxia.image.core.common.references.a.a(bitmap, this.IT.nQ());
            }
            bitmap.recycle();
            throw new TooManyBitmapsException();
        } catch (Exception e) {
            bitmap.recycle();
            throw Throwables.propagate(e);
        }
    }
}
