package com.MCWorld.version;

import com.MCWorld.framework.base.log.HLog;
import com.MCWorld.framework.base.notification.CallbackHandler;
import com.MCWorld.framework.base.notification.EventNotifyCenter.MessageHandler;

/* compiled from: VersionMemCache */
class g$1 extends CallbackHandler {
    final /* synthetic */ g boy;

    g$1(g this$0) {
        this.boy = this$0;
    }

    @MessageHandler(message = 266)
    public void onServiceRestart() {
        HLog.info("VersionMemCache", "service restart recv..........", new Object[0]);
        g.a(this.boy);
    }
}
