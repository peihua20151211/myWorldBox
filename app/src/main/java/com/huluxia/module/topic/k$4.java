package com.huluxia.module.topic;

import com.huluxia.framework.base.http.io.Response.ErrorListener;
import com.huluxia.framework.base.http.toolbox.error.VolleyError;
import com.huluxia.framework.base.log.HLog;
import com.huluxia.framework.base.notification.EventNotifyCenter;
import com.huluxia.module.h;

/* compiled from: TopicModule2 */
class k$4 implements ErrorListener {
    final /* synthetic */ k aCN;

    k$4(k this$0) {
        this.aCN = this$0;
    }

    public void onErrorResponse(VolleyError error) {
        HLog.error(this, "requestCommentCreate onErrorResponse e = " + error, new Object[0]);
        EventNotifyCenter.notifyEventUiThread(h.class, h.arM, new Object[]{Boolean.valueOf(false), null});
    }
}
