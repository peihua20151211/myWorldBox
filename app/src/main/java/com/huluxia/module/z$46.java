package com.huluxia.module;

import com.huluxia.framework.base.http.io.Response.ErrorListener;
import com.huluxia.framework.base.http.toolbox.error.VolleyError;
import com.huluxia.framework.base.notification.EventNotifyCenter;

/* compiled from: StudioModule */
class z$46 implements ErrorListener {
    final /* synthetic */ Object amk;
    final /* synthetic */ int axy;

    z$46(int i, Object obj) {
        this.axy = i;
        this.amk = obj;
    }

    public void onErrorResponse(VolleyError error) {
        EventNotifyCenter.notifyEvent(h.class, 784, new Object[]{Boolean.valueOf(false), null, Integer.valueOf(this.axy), this.amk});
    }
}
