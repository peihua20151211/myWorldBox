package com.xiaomi.push.service;

import com.xiaomi.channel.commonutils.misc.a;
import com.xiaomi.push.service.XMPushService.g;
import com.xiaomi.smack.b;

class ao extends g {
    final /* synthetic */ int b;
    final /* synthetic */ byte[] c;
    final /* synthetic */ String d;
    final /* synthetic */ XMPushService e;

    ao(XMPushService xMPushService, int i, int i2, byte[] bArr, String str) {
        this.e = xMPushService;
        this.b = i2;
        this.c = bArr;
        this.d = str;
        super(i);
    }

    public void a() {
        g.b(this.e);
        x.a().a("5");
        a.a(this.b);
        this.e.b.c(b.b());
        this.e.a(this.c, this.d);
    }

    public String b() {
        return "clear account cache.";
    }
}
