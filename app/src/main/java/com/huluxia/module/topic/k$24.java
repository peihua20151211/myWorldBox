package com.huluxia.module.topic;

import com.huluxia.data.topic.TopicItem;
import com.huluxia.framework.base.http.io.Response.ErrorListener;
import com.huluxia.framework.base.http.toolbox.error.VolleyError;
import com.huluxia.framework.base.notification.EventNotifyCenter;
import com.huluxia.module.h;

/* compiled from: TopicModule2 */
class k$24 implements ErrorListener {
    final /* synthetic */ k aCN;
    final /* synthetic */ TopicItem aCQ;
    final /* synthetic */ boolean aCR;

    k$24(k this$0, TopicItem topicItem, boolean z) {
        this.aCN = this$0;
        this.aCQ = topicItem;
        this.aCR = z;
    }

    public void onErrorResponse(VolleyError error) {
        EventNotifyCenter.notifyEvent(h.class, h.aru, new Object[]{Boolean.valueOf(false), null, Long.valueOf(this.aCQ.getPostID()), Boolean.valueOf(this.aCR)});
    }
}
