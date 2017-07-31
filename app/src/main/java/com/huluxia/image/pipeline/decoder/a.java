package com.huluxia.image.pipeline.decoder;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import com.huluxia.framework.base.utils.Closeables;
import com.huluxia.image.base.imageformat.c;
import com.huluxia.image.base.imagepipeline.animated.factory.d;
import com.huluxia.image.base.imagepipeline.image.b;
import com.huluxia.image.base.imagepipeline.image.f;
import com.huluxia.image.base.imagepipeline.image.g;
import com.huluxia.image.pipeline.platform.e;
import java.io.InputStream;
import java.util.Map;
import javax.annotation.Nullable;

/* compiled from: DefaultImageDecoder */
public class a implements b {
    private final d Fu;
    private final b GP;
    @Nullable
    private final Map<com.huluxia.image.base.imageformat.d, b> GQ;
    private final e Gm;
    private final Config ws;

    public a(d animatedImageFactory, e platformDecoder, Config bitmapConfig) {
        this(animatedImageFactory, platformDecoder, bitmapConfig, null);
    }

    public a(d animatedImageFactory, e platformDecoder, Config bitmapConfig, @Nullable Map<com.huluxia.image.base.imageformat.d, b> customDecoders) {
        this.GP = new 1(this);
        this.Fu = animatedImageFactory;
        this.ws = bitmapConfig;
        this.Gm = platformDecoder;
        this.GQ = customDecoders;
    }

    public b a(com.huluxia.image.base.imagepipeline.image.d encodedImage, int length, g qualityInfo, com.huluxia.image.base.imagepipeline.common.a options) {
        com.huluxia.image.base.imageformat.d imageFormat = encodedImage.hT();
        if (imageFormat == null || imageFormat == com.huluxia.image.base.imageformat.d.vz) {
            imageFormat = com.huluxia.image.base.imageformat.e.h(encodedImage.getInputStream());
            encodedImage.d(imageFormat);
        }
        if (this.GQ != null) {
            b decoder = (b) this.GQ.get(imageFormat);
            if (decoder != null) {
                return decoder.a(encodedImage, length, qualityInfo, options);
            }
        }
        return this.GP.a(encodedImage, length, qualityInfo, options);
    }

    public b a(com.huluxia.image.base.imagepipeline.image.d encodedImage, com.huluxia.image.base.imagepipeline.common.a options) {
        InputStream is = encodedImage.getInputStream();
        if (is == null) {
            return null;
        }
        try {
            b b;
            if (options.wl || this.Fu == null || !c.e(is)) {
                b = b(encodedImage, options);
                Closeables.closeQuietly(is);
                return b;
            }
            b = this.Fu.a(encodedImage, options, this.ws);
            return b;
        } finally {
            Closeables.closeQuietly(is);
        }
    }

    public com.huluxia.image.base.imagepipeline.image.c b(com.huluxia.image.base.imagepipeline.image.d encodedImage, com.huluxia.image.base.imagepipeline.common.a options) {
        com.huluxia.image.core.common.references.a<Bitmap> bitmapReference = this.Gm.a(encodedImage, options.wm);
        try {
            com.huluxia.image.base.imagepipeline.image.c cVar = new com.huluxia.image.base.imagepipeline.image.c(bitmapReference, f.wZ, encodedImage.hQ());
            return cVar;
        } finally {
            bitmapReference.close();
        }
    }

    public com.huluxia.image.base.imagepipeline.image.c b(com.huluxia.image.base.imagepipeline.image.d encodedImage, int length, g qualityInfo, com.huluxia.image.base.imagepipeline.common.a options) {
        com.huluxia.image.core.common.references.a<Bitmap> bitmapReference = this.Gm.a(encodedImage, options.wm, length);
        try {
            com.huluxia.image.base.imagepipeline.image.c cVar = new com.huluxia.image.base.imagepipeline.image.c(bitmapReference, qualityInfo, encodedImage.hQ());
            return cVar;
        } finally {
            bitmapReference.close();
        }
    }

    public b c(com.huluxia.image.base.imagepipeline.image.d encodedImage, com.huluxia.image.base.imagepipeline.common.a options) {
        return this.Fu.b(encodedImage, options, this.ws);
    }
}
