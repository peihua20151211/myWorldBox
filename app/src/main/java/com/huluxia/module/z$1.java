package com.huluxia.module;

import com.huluxia.framework.base.http.io.Response.ErrorListener;
import com.huluxia.framework.base.http.toolbox.error.VolleyError;
import com.huluxia.framework.base.notification.EventNotifyCenter;

/* compiled from: StudioModule */
class z$1 implements ErrorListener {
    final /* synthetic */ Object amk;
    final /* synthetic */ long axx;

    z$1(long j, Object obj) {
        this.axx = j;
        this.amk = obj;
    }

    public void onErrorResponse(VolleyError error) {
        EventNotifyCenter.notifyEvent(h.class, 776, new Object[]{Boolean.valueOf(false), null, Long.valueOf(this.axx), this.amk});
    }
}
