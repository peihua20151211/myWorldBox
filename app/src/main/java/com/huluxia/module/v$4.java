package com.huluxia.module;

import com.huluxia.framework.base.http.io.Response.ErrorListener;
import com.huluxia.framework.base.http.toolbox.error.VolleyError;
import com.huluxia.framework.base.log.HLog;
import com.huluxia.framework.base.notification.EventNotifyCenter;

/* compiled from: ServerModule */
class v$4 implements ErrorListener {
    final /* synthetic */ v axu;

    v$4(v this$0) {
        this.axu = this$0;
    }

    public void onErrorResponse(VolleyError error) {
        HLog.error(this, "requestMapRanking onErrorResponse e = " + error, new Object[0]);
        EventNotifyCenter.notifyEvent(n.class, 260, new Object[]{Boolean.valueOf(false), null});
    }
}
