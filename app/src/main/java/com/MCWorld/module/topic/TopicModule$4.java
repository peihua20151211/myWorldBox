package com.MCWorld.module.topic;

import com.MCWorld.framework.base.http.io.Response.ErrorListener;
import com.MCWorld.framework.base.http.toolbox.error.VolleyError;
import com.MCWorld.framework.base.log.HLog;
import com.MCWorld.framework.base.notification.EventNotifyCenter;
import com.MCWorld.module.n;

class TopicModule$4 implements ErrorListener {
    final /* synthetic */ TopicModule aCF;
    final /* synthetic */ long aCG;
    final /* synthetic */ long aCH;

    TopicModule$4(TopicModule this$0, long j, long j2) {
        this.aCF = this$0;
        this.aCG = j;
        this.aCH = j2;
    }

    public void onErrorResponse(VolleyError error) {
        HLog.error(this, "requestCommentRemove onErrorResponse e = " + error, new Object[0]);
        EventNotifyCenter.notifyEvent(n.class, n.awt, new Object[]{Boolean.valueOf(false), Long.valueOf(this.aCG), Long.valueOf(this.aCH)});
    }
}
