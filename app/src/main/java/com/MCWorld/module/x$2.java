package com.MCWorld.module;

import com.MCWorld.framework.base.http.io.Response.ErrorListener;
import com.MCWorld.framework.base.http.toolbox.error.VolleyError;
import com.MCWorld.framework.base.log.HLog;
import com.MCWorld.framework.base.notification.EventNotifyCenter;

/* compiled from: SkinModule */
class x$2 implements ErrorListener {
    final /* synthetic */ x axw;

    x$2(x this$0) {
        this.axw = this$0;
    }

    public void onErrorResponse(VolleyError error) {
        HLog.error(this, "requestSkinNew onErrorResponse e = " + error, new Object[0]);
        EventNotifyCenter.notifyEvent(n.class, n.avH, new Object[]{Boolean.valueOf(false), null});
    }
}
