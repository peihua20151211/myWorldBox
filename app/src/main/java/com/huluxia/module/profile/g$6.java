package com.huluxia.module.profile;

import com.huluxia.framework.base.http.io.Response.ErrorListener;
import com.huluxia.framework.base.http.toolbox.error.VolleyError;
import com.huluxia.framework.base.notification.EventNotifyCenter;
import com.huluxia.module.h;

/* compiled from: ProfileModule */
class g$6 implements ErrorListener {
    final /* synthetic */ g aCs;

    g$6(g this$0) {
        this.aCs = this$0;
    }

    public void onErrorResponse(VolleyError error) {
        EventNotifyCenter.notifyEvent(h.class, h.ard, new Object[]{Boolean.valueOf(false), null, "访问失败"});
    }
}