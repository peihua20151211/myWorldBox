package com.huluxia.module;

import com.huluxia.data.studio.g;
import com.huluxia.framework.base.http.io.Response.Listener;
import com.huluxia.framework.base.notification.EventNotifyCenter;

/* compiled from: StudioModule */
class z$13 implements Listener<g> {
    final /* synthetic */ int axz;

    z$13(int i) {
        this.axz = i;
    }

    public /* synthetic */ void onResponse(Object obj) {
        a((g) obj);
    }

    public void a(g response) {
        if (response == null || !response.isSucc()) {
            EventNotifyCenter.notifyEvent(h.class, h.aso, new Object[]{Integer.valueOf(this.axz), Boolean.valueOf(false), null});
            return;
        }
        EventNotifyCenter.notifyEvent(h.class, h.aso, new Object[]{Integer.valueOf(this.axz), Boolean.valueOf(true), response});
    }
}
