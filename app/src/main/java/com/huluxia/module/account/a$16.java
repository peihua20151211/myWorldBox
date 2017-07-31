package com.huluxia.module.account;

import com.huluxia.framework.base.http.io.Response.Listener;
import com.huluxia.framework.base.json.Json;
import com.huluxia.framework.base.notification.EventNotifyCenter;
import com.huluxia.module.h;
import com.huluxia.module.w;

/* compiled from: AccountModule */
class a$16 implements Listener<String> {
    final /* synthetic */ a aBH;
    final /* synthetic */ boolean aBI;
    final /* synthetic */ int aBJ;

    a$16(a this$0, boolean z, int i) {
        this.aBH = this$0;
        this.aBI = z;
        this.aBJ = i;
    }

    public void onResponse(String response) {
        try {
            w info = (w) Json.parseJsonObject(response, w.class);
            if (info == null || !info.isSucc()) {
                EventNotifyCenter.notifyEventUiThread(h.class, h.arC, new Object[]{Boolean.valueOf(false), Boolean.valueOf(this.aBI), Integer.valueOf(this.aBJ)});
                return;
            }
            EventNotifyCenter.notifyEventUiThread(h.class, h.arC, new Object[]{Boolean.valueOf(true), Boolean.valueOf(this.aBI), Integer.valueOf(this.aBJ)});
        } catch (Exception e) {
            EventNotifyCenter.notifyEventUiThread(h.class, h.arC, new Object[]{Boolean.valueOf(false), Boolean.valueOf(this.aBI), Integer.valueOf(this.aBJ)});
        }
    }
}
