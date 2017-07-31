package com.huluxia.image.pipeline.cache;

import android.net.Uri;
import com.huluxia.image.base.cache.common.b;
import com.huluxia.image.pipeline.request.ImageRequest;
import javax.annotation.Nullable;

/* compiled from: CacheKeyFactory */
public interface d {
    b a(ImageRequest imageRequest, Uri uri, @Nullable Object obj);

    b a(ImageRequest imageRequest, Object obj);

    b b(ImageRequest imageRequest, Object obj);

    b c(ImageRequest imageRequest, @Nullable Object obj);
}
