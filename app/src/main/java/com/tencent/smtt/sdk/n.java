package com.tencent.smtt.sdk;

import android.net.Uri;
import android.webkit.ValueCallback;

class n implements ValueCallback<Uri[]> {
    final /* synthetic */ ValueCallback a;
    final /* synthetic */ k b;

    n(k kVar, ValueCallback valueCallback) {
        this.b = kVar;
        this.a = valueCallback;
    }

    public void a(Uri[] uriArr) {
        this.a.onReceiveValue(uriArr);
    }

    public /* synthetic */ void onReceiveValue(Object obj) {
        a((Uri[]) obj);
    }
}
