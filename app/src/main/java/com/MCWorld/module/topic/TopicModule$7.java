package com.MCWorld.module.topic;

import com.MCWorld.data.topic.f;
import com.MCWorld.framework.base.http.io.Response.Listener;
import com.MCWorld.framework.base.json.Json;
import com.MCWorld.framework.base.log.HLog;
import com.MCWorld.framework.base.notification.EventNotifyCenter;
import com.MCWorld.module.n;

class TopicModule$7 implements Listener<String> {
    final /* synthetic */ TopicModule aCF;

    TopicModule$7(TopicModule this$0) {
        this.aCF = this$0;
    }

    public void onResponse(String response) {
        try {
            f info = (f) Json.parseJsonObject(response, f.class);
            EventNotifyCenter.notifyEvent(n.class, n.awu, new Object[]{Boolean.valueOf(true), info.address});
        } catch (Exception e) {
            HLog.error(this, "requestTopicShare e = " + e + ", response = " + response, new Object[0]);
            EventNotifyCenter.notifyEvent(n.class, n.awu, new Object[]{Boolean.valueOf(false), null});
        }
    }
}
