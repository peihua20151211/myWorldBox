package com.MCWorld.module;

import com.MCWorld.framework.base.http.io.Response.Listener;
import com.MCWorld.framework.base.json.Json;
import com.MCWorld.framework.base.log.HLog;
import com.MCWorld.framework.base.notification.EventNotifyCenter;
import com.MCWorld.r;
import com.MCWorld.r.a;

/* compiled from: UploadModule */
class aa$13 implements Listener<String> {
    final /* synthetic */ aa axK;
    final /* synthetic */ r axP;

    aa$13(aa this$0, r rVar) {
        this.axK = this$0;
        this.axP = rVar;
    }

    public void onResponse(String response) {
        if (aa.DS()) {
            String str = "UploadModule";
            String str2 = "LSPrint UploadModule rec SendForumPost [response:%s]";
            Object[] objArr = new Object[1];
            objArr[0] = response == null ? "空" : response;
            HLog.verbose(str, str2, objArr);
        }
        try {
            w info = (w) Json.parseJsonObject(response, w.class);
            if (aa.DS()) {
                HLog.verbose("TAG", " LS Printf info Code:[%d]; MSG:[%s]!", new Object[]{Integer.valueOf(info.code), info.msg.toString()});
                HLog.verbose("TAG", " LS Printf response:[%s]!", new Object[]{response});
            }
            if (info.isSucc()) {
                EventNotifyCenter.notifyEvent(n.class, n.awC, new Object[]{Boolean.valueOf(true), info.msg});
                return;
            }
            r.ck().j(a.jG, "resTyep:" + this.axP.axr + " - rec status:" + info.status + " - code:" + info.code);
            EventNotifyCenter.notifyEvent(n.class, n.awC, new Object[]{Boolean.valueOf(false), info.msg});
        } catch (Exception e) {
            HLog.error(this, "requestJsInputCount e = " + e + ", response = " + response, new Object[0]);
            EventNotifyCenter.notifyEvent(n.class, n.awC, new Object[]{Boolean.valueOf(false), "解析数据错误"});
            r.ck().j(a.jG, "resTyep:" + this.axP.axr + " - JsonParseError");
        }
    }
}
