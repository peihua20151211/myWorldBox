package com.huluxia.module.profile;

import android.content.Context;
import com.huluxia.framework.base.http.io.Response.Listener;
import com.huluxia.framework.base.log.HLog;
import com.huluxia.framework.base.notification.EventNotifyCenter;
import com.huluxia.module.a;
import com.huluxia.module.h;

/* compiled from: ProfileModule */
class g$28 implements Listener<d> {
    final /* synthetic */ g aCs;
    final /* synthetic */ Context val$context;
    final /* synthetic */ int val$start;

    g$28(g this$0, int i, Context context) {
        this.aCs = this$0;
        this.val$start = i;
        this.val$context = context;
    }

    public /* synthetic */ void onResponse(Object obj) {
        a((d) obj);
    }

    public void a(d friendships) {
        HLog.debug(this, "requestFollowerList response %s", new Object[]{friendships});
        if (a.isDataSucc(friendships)) {
            EventNotifyCenter.notifyEvent(h.class, h.arQ, new Object[]{Boolean.valueOf(true), friendships, Integer.valueOf(this.val$start), this.val$context});
            return;
        }
        EventNotifyCenter.notifyEvent(h.class, h.arQ, new Object[]{Boolean.valueOf(false), null, Integer.valueOf(this.val$start), this.val$context});
    }
}