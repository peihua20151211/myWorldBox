package com.MCWorld.module.profile;

import com.MCWorld.framework.base.http.io.Response.Listener;
import com.MCWorld.framework.base.json.Json;
import com.MCWorld.framework.base.notification.EventNotifyCenter;
import com.MCWorld.module.h;
import com.MCWorld.module.w;

/* compiled from: ProfileModule */
class g$7 implements Listener<String> {
    final /* synthetic */ g aCs;

    g$7(g this$0) {
        this.aCs = this$0;
    }

    public void onResponse(String response) {
        try {
            w info = (w) Json.parseJsonObject(response, w.class);
            if (info == null || !info.isSucc()) {
                EventNotifyCenter.notifyEvent(h.class, h.arc, new Object[]{Boolean.valueOf(false), info.msg});
                return;
            }
            EventNotifyCenter.notifyEvent(h.class, h.arc, new Object[]{Boolean.valueOf(true), null});
        } catch (Exception e) {
            EventNotifyCenter.notifyEvent(h.class, h.arc, new Object[]{Boolean.valueOf(false), "访问失败\n网络问题"});
        }
    }
}
