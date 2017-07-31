package com.huluxia.module;

import com.huluxia.framework.base.http.io.Response.ErrorListener;
import com.huluxia.framework.base.http.toolbox.error.VolleyError;
import com.huluxia.framework.base.log.HLog;
import com.huluxia.framework.base.notification.EventNotifyCenter;

/* compiled from: UploadModule */
class aa$12 implements ErrorListener {
    final /* synthetic */ aa axK;

    aa$12(aa this$0) {
        this.axK = this$0;
    }

    public void onErrorResponse(VolleyError error) {
        HLog.verbose("UploadModule", "upload error:[%s]", new Object[]{error.toString()});
        EventNotifyCenter.notifyEvent(n.class, n.awI, new Object[]{Boolean.valueOf(false), "发送帖子失败\n网络问题"});
    }
}
