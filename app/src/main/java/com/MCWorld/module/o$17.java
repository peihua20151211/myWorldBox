package com.MCWorld.module;

import com.MCWorld.framework.base.http.io.Response.ErrorListener;
import com.MCWorld.framework.base.http.toolbox.error.VolleyError;
import com.MCWorld.framework.base.log.HLog;
import com.MCWorld.framework.base.notification.EventNotifyCenter;

/* compiled from: ModuleRequestWrapper */
class o$17 implements ErrorListener {
    final /* synthetic */ int axp;

    o$17(int i) {
        this.axp = i;
    }

    public void onErrorResponse(VolleyError error) {
        HLog.error(this, "requestMapCategory onErrorResponse e = " + error, new Object[0]);
        EventNotifyCenter.notifyEvent(n.class, 3077, new Object[]{Boolean.valueOf(false), Integer.valueOf(this.axp), null});
    }
}
