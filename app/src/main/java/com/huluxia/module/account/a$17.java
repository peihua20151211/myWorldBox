package com.huluxia.module.account;

import com.huluxia.framework.base.http.io.Response.ErrorListener;
import com.huluxia.framework.base.http.toolbox.error.VolleyError;
import com.huluxia.framework.base.log.HLog;
import com.huluxia.framework.base.notification.EventNotifyCenter;
import com.huluxia.module.h;

/* compiled from: AccountModule */
class a$17 implements ErrorListener {
    final /* synthetic */ a aBH;
    final /* synthetic */ boolean aBI;
    final /* synthetic */ int aBJ;

    a$17(a this$0, boolean z, int i) {
        this.aBH = this$0;
        this.aBI = z;
        this.aBJ = i;
    }

    public void onErrorResponse(VolleyError error) {
        HLog.error(this, "setMsgNotification onErrorResponse e = " + error, new Object[0]);
        EventNotifyCenter.notifyEventUiThread(h.class, h.arC, new Object[]{Boolean.valueOf(false), Boolean.valueOf(this.aBI), Integer.valueOf(this.aBJ)});
    }
}
