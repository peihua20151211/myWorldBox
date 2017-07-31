package com.huluxia.module.topic;

import com.huluxia.framework.base.http.io.Response.Listener;
import com.huluxia.framework.base.json.Json;
import com.huluxia.framework.base.log.HLog;
import com.huluxia.framework.base.notification.EventNotifyCenter;
import com.huluxia.module.h;

/* compiled from: TopicModule2 */
class k$22 implements Listener<String> {
    final /* synthetic */ String aBQ;
    final /* synthetic */ k aCN;

    k$22(k this$0, String str) {
        this.aCN = this$0;
        this.aBQ = str;
    }

    public void onResponse(String response) {
        try {
            b info = (b) Json.parseJsonObject(response, b.class);
            if (info == null || !info.isSucc()) {
                EventNotifyCenter.notifyEvent(h.class, h.ars, new Object[]{Boolean.valueOf(false), this.aBQ, null});
                return;
            }
            EventNotifyCenter.notifyEvent(h.class, h.ars, new Object[]{Boolean.valueOf(true), this.aBQ, info});
        } catch (Exception e) {
            HLog.error(this, "requestResourceTopic e = " + e + ", response = " + response, new Object[0]);
            EventNotifyCenter.notifyEvent(h.class, h.ars, new Object[]{Boolean.valueOf(false), this.aBQ, null});
        }
    }
}
