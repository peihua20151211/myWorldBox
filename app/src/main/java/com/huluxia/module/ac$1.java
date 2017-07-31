package com.huluxia.module;

import com.huluxia.data.map.f;
import com.huluxia.framework.base.http.io.Response.Listener;
import com.huluxia.framework.base.json.Json;
import com.huluxia.framework.base.log.HLog;
import com.huluxia.framework.base.notification.EventNotifyCenter;

/* compiled from: WoodModule */
class ac$1 implements Listener<String> {
    final /* synthetic */ ac aBD;

    ac$1(ac this$0) {
        this.aBD = this$0;
    }

    public void onResponse(String response) {
        try {
            f info = (f) Json.parseJsonObject(response, f.class);
            EventNotifyCenter.notifyEvent(n.class, n.avM, new Object[]{Boolean.valueOf(true), info});
        } catch (Exception e) {
            HLog.error(this, "requestWoodNew e = " + e + ", response = " + response, new Object[0]);
            EventNotifyCenter.notifyEvent(n.class, n.avM, new Object[]{Boolean.valueOf(false), null});
        }
    }
}
