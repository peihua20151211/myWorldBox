package com.MCWorld.data;

import com.MCWorld.HTApplication;
import com.MCWorld.framework.base.log.HLog;
import com.MCWorld.framework.base.notification.CallbackHandler;
import com.MCWorld.framework.base.notification.EventNotifyCenter;
import com.MCWorld.framework.base.notification.EventNotifyCenter.MessageHandler;
import com.MCWorld.login.LoginError.LoginErrCode;
import com.MCWorld.login.e;
import com.MCWorld.module.h;
import com.MCWorld.service.i;
import com.MCWorld.utils.ah;

/* compiled from: LoginProxy */
public class f {
    private static final String TAG = "Loginservice-Proxy";
    private static f pk;
    private CallbackHandler pl = new CallbackHandler(this) {
        final /* synthetic */ f pm;

        {
            this.pm = this$0;
        }

        @MessageHandler(message = 1025)
        public void onLogin(boolean succ, String clientid, String email, String encryptPwd, String openid, int code, String msg, LoginErrCode errCode) {
            HLog.info(f.TAG, "proxy login succ %b, clientid %s, msg %s", Boolean.valueOf(succ), clientid, msg);
            if (succ) {
                ah.KZ().bT(email);
                ah.KZ().setPassword(encryptPwd);
                i.EE();
                i.EF();
                HTApplication.bR();
                EventNotifyCenter.notifyEvent(h.class, h.aqW, new Object[0]);
                EventNotifyCenter.notifyEventUiThread(e.class, 1028, Boolean.valueOf(succ), clientid, email, encryptPwd, Integer.valueOf(code), msg, errCode);
                return;
            }
            HLog.error(f.TAG, "login fail %d, %s", Integer.valueOf(code), msg);
            EventNotifyCenter.notifyEventUiThread(e.class, 1028, Boolean.valueOf(false), clientid, email, encryptPwd, Integer.valueOf(code), msg, errCode);
        }
    };

    public static f ek() {
        if (pk == null) {
            pk = new f();
        }
        return pk;
    }

    private f() {
        EventNotifyCenter.add(e.class, this.pl);
    }
}
