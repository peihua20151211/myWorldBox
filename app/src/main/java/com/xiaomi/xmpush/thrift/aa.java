package com.xiaomi.xmpush.thrift;

import com.tencent.open.SocialConstants;
import java.io.Serializable;
import java.util.BitSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.thrift.b;
import org.apache.thrift.meta_data.g;
import org.apache.thrift.protocol.c;
import org.apache.thrift.protocol.f;
import org.apache.thrift.protocol.i;
import org.apache.thrift.protocol.k;

public class aa implements Serializable, Cloneable, b<aa, a> {
    public static final Map<a, org.apache.thrift.meta_data.b> i;
    private static final k j = new k("XmPushActionUnRegistrationResult");
    private static final c k = new c("debug", (byte) 11, (short) 1);
    private static final c l = new c("target", (byte) 12, (short) 2);
    private static final c m = new c("id", (byte) 11, (short) 3);
    private static final c n = new c("appId", (byte) 11, (short) 4);
    private static final c o = new c(SocialConstants.TYPE_REQUEST, (byte) 12, (short) 5);
    private static final c p = new c("errorCode", (byte) 10, (short) 6);
    private static final c q = new c("reason", (byte) 11, (short) 7);
    private static final c r = new c("packageName", (byte) 11, (short) 8);
    public String a;
    public j b;
    public String c;
    public String d;
    public z e;
    public long f;
    public String g;
    public String h;
    private BitSet s = new BitSet(1);

    public enum a {
        DEBUG((short) 1, "debug"),
        TARGET((short) 2, "target"),
        ID((short) 3, "id"),
        APP_ID((short) 4, "appId"),
        REQUEST((short) 5, SocialConstants.TYPE_REQUEST),
        ERROR_CODE((short) 6, "errorCode"),
        REASON((short) 7, "reason"),
        PACKAGE_NAME((short) 8, "packageName");
        
        private static final Map<String, a> i = null;
        private final short j;
        private final String k;

        static {
            i = new HashMap();
            Iterator it = EnumSet.allOf(a.class).iterator();
            while (it.hasNext()) {
                a aVar = (a) it.next();
                i.put(aVar.a(), aVar);
            }
        }

        private a(short s, String str) {
            this.j = s;
            this.k = str;
        }

        public String a() {
            return this.k;
        }
    }

    static {
        Map enumMap = new EnumMap(a.class);
        enumMap.put(a.DEBUG, new org.apache.thrift.meta_data.b("debug", (byte) 2, new org.apache.thrift.meta_data.c((byte) 11)));
        enumMap.put(a.TARGET, new org.apache.thrift.meta_data.b("target", (byte) 2, new g((byte) 12, j.class)));
        enumMap.put(a.ID, new org.apache.thrift.meta_data.b("id", (byte) 1, new org.apache.thrift.meta_data.c((byte) 11)));
        enumMap.put(a.APP_ID, new org.apache.thrift.meta_data.b("appId", (byte) 1, new org.apache.thrift.meta_data.c((byte) 11)));
        enumMap.put(a.REQUEST, new org.apache.thrift.meta_data.b(SocialConstants.TYPE_REQUEST, (byte) 2, new g((byte) 12, z.class)));
        enumMap.put(a.ERROR_CODE, new org.apache.thrift.meta_data.b("errorCode", (byte) 1, new org.apache.thrift.meta_data.c((byte) 10)));
        enumMap.put(a.REASON, new org.apache.thrift.meta_data.b("reason", (byte) 2, new org.apache.thrift.meta_data.c((byte) 11)));
        enumMap.put(a.PACKAGE_NAME, new org.apache.thrift.meta_data.b("packageName", (byte) 2, new org.apache.thrift.meta_data.c((byte) 11)));
        i = Collections.unmodifiableMap(enumMap);
        org.apache.thrift.meta_data.b.a(aa.class, i);
    }

    public void a(f fVar) {
        fVar.g();
        while (true) {
            c i = fVar.i();
            if (i.b == (byte) 0) {
                fVar.h();
                if (f()) {
                    i();
                    return;
                }
                throw new org.apache.thrift.protocol.g("Required field 'errorCode' was not found in serialized data! Struct: " + toString());
            }
            switch (i.c) {
                case (short) 1:
                    if (i.b != (byte) 11) {
                        i.a(fVar, i.b);
                        break;
                    } else {
                        this.a = fVar.w();
                        break;
                    }
                case (short) 2:
                    if (i.b != (byte) 12) {
                        i.a(fVar, i.b);
                        break;
                    }
                    this.b = new j();
                    this.b.a(fVar);
                    break;
                case (short) 3:
                    if (i.b != (byte) 11) {
                        i.a(fVar, i.b);
                        break;
                    } else {
                        this.c = fVar.w();
                        break;
                    }
                case (short) 4:
                    if (i.b != (byte) 11) {
                        i.a(fVar, i.b);
                        break;
                    } else {
                        this.d = fVar.w();
                        break;
                    }
                case (short) 5:
                    if (i.b != (byte) 12) {
                        i.a(fVar, i.b);
                        break;
                    }
                    this.e = new z();
                    this.e.a(fVar);
                    break;
                case (short) 6:
                    if (i.b != (byte) 10) {
                        i.a(fVar, i.b);
                        break;
                    }
                    this.f = fVar.u();
                    a(true);
                    break;
                case (short) 7:
                    if (i.b != (byte) 11) {
                        i.a(fVar, i.b);
                        break;
                    } else {
                        this.g = fVar.w();
                        break;
                    }
                case (short) 8:
                    if (i.b != (byte) 11) {
                        i.a(fVar, i.b);
                        break;
                    } else {
                        this.h = fVar.w();
                        break;
                    }
                default:
                    i.a(fVar, i.b);
                    break;
            }
            fVar.j();
        }
    }

    public void a(boolean z) {
        this.s.set(0, z);
    }

    public boolean a() {
        return this.a != null;
    }

    public boolean a(aa aaVar) {
        if (aaVar == null) {
            return false;
        }
        boolean a = a();
        boolean a2 = aaVar.a();
        if ((a || a2) && (!a || !a2 || !this.a.equals(aaVar.a))) {
            return false;
        }
        a = b();
        a2 = aaVar.b();
        if ((a || a2) && (!a || !a2 || !this.b.a(aaVar.b))) {
            return false;
        }
        a = c();
        a2 = aaVar.c();
        if ((a || a2) && (!a || !a2 || !this.c.equals(aaVar.c))) {
            return false;
        }
        a = d();
        a2 = aaVar.d();
        if ((a || a2) && (!a || !a2 || !this.d.equals(aaVar.d))) {
            return false;
        }
        a = e();
        a2 = aaVar.e();
        if (((a || a2) && (!a || !a2 || !this.e.a(aaVar.e))) || this.f != aaVar.f) {
            return false;
        }
        a = g();
        a2 = aaVar.g();
        if ((a || a2) && (!a || !a2 || !this.g.equals(aaVar.g))) {
            return false;
        }
        a = h();
        a2 = aaVar.h();
        return !(a || a2) || (a && a2 && this.h.equals(aaVar.h));
    }

    public int b(aa aaVar) {
        if (!getClass().equals(aaVar.getClass())) {
            return getClass().getName().compareTo(aaVar.getClass().getName());
        }
        int compareTo = Boolean.valueOf(a()).compareTo(Boolean.valueOf(aaVar.a()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (a()) {
            compareTo = org.apache.thrift.c.a(this.a, aaVar.a);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(b()).compareTo(Boolean.valueOf(aaVar.b()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (b()) {
            compareTo = org.apache.thrift.c.a(this.b, aaVar.b);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(c()).compareTo(Boolean.valueOf(aaVar.c()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (c()) {
            compareTo = org.apache.thrift.c.a(this.c, aaVar.c);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(d()).compareTo(Boolean.valueOf(aaVar.d()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (d()) {
            compareTo = org.apache.thrift.c.a(this.d, aaVar.d);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(e()).compareTo(Boolean.valueOf(aaVar.e()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (e()) {
            compareTo = org.apache.thrift.c.a(this.e, aaVar.e);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(f()).compareTo(Boolean.valueOf(aaVar.f()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (f()) {
            compareTo = org.apache.thrift.c.a(this.f, aaVar.f);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(g()).compareTo(Boolean.valueOf(aaVar.g()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (g()) {
            compareTo = org.apache.thrift.c.a(this.g, aaVar.g);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        compareTo = Boolean.valueOf(h()).compareTo(Boolean.valueOf(aaVar.h()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (h()) {
            compareTo = org.apache.thrift.c.a(this.h, aaVar.h);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        return 0;
    }

    public void b(f fVar) {
        i();
        fVar.a(j);
        if (this.a != null && a()) {
            fVar.a(k);
            fVar.a(this.a);
            fVar.b();
        }
        if (this.b != null && b()) {
            fVar.a(l);
            this.b.b(fVar);
            fVar.b();
        }
        if (this.c != null) {
            fVar.a(m);
            fVar.a(this.c);
            fVar.b();
        }
        if (this.d != null) {
            fVar.a(n);
            fVar.a(this.d);
            fVar.b();
        }
        if (this.e != null && e()) {
            fVar.a(o);
            this.e.b(fVar);
            fVar.b();
        }
        fVar.a(p);
        fVar.a(this.f);
        fVar.b();
        if (this.g != null && g()) {
            fVar.a(q);
            fVar.a(this.g);
            fVar.b();
        }
        if (this.h != null && h()) {
            fVar.a(r);
            fVar.a(this.h);
            fVar.b();
        }
        fVar.c();
        fVar.a();
    }

    public boolean b() {
        return this.b != null;
    }

    public boolean c() {
        return this.c != null;
    }

    public /* synthetic */ int compareTo(Object obj) {
        return b((aa) obj);
    }

    public boolean d() {
        return this.d != null;
    }

    public boolean e() {
        return this.e != null;
    }

    public boolean equals(Object obj) {
        return (obj != null && (obj instanceof aa)) ? a((aa) obj) : false;
    }

    public boolean f() {
        return this.s.get(0);
    }

    public boolean g() {
        return this.g != null;
    }

    public boolean h() {
        return this.h != null;
    }

    public int hashCode() {
        return 0;
    }

    public void i() {
        if (this.c == null) {
            throw new org.apache.thrift.protocol.g("Required field 'id' was not present! Struct: " + toString());
        } else if (this.d == null) {
            throw new org.apache.thrift.protocol.g("Required field 'appId' was not present! Struct: " + toString());
        }
    }

    public String toString() {
        Object obj = null;
        StringBuilder stringBuilder = new StringBuilder("XmPushActionUnRegistrationResult(");
        Object obj2 = 1;
        if (a()) {
            stringBuilder.append("debug:");
            if (this.a == null) {
                stringBuilder.append("null");
            } else {
                stringBuilder.append(this.a);
            }
            obj2 = null;
        }
        if (b()) {
            if (obj2 == null) {
                stringBuilder.append(", ");
            }
            stringBuilder.append("target:");
            if (this.b == null) {
                stringBuilder.append("null");
            } else {
                stringBuilder.append(this.b);
            }
        } else {
            obj = obj2;
        }
        if (obj == null) {
            stringBuilder.append(", ");
        }
        stringBuilder.append("id:");
        if (this.c == null) {
            stringBuilder.append("null");
        } else {
            stringBuilder.append(this.c);
        }
        stringBuilder.append(", ");
        stringBuilder.append("appId:");
        if (this.d == null) {
            stringBuilder.append("null");
        } else {
            stringBuilder.append(this.d);
        }
        if (e()) {
            stringBuilder.append(", ");
            stringBuilder.append("request:");
            if (this.e == null) {
                stringBuilder.append("null");
            } else {
                stringBuilder.append(this.e);
            }
        }
        stringBuilder.append(", ");
        stringBuilder.append("errorCode:");
        stringBuilder.append(this.f);
        if (g()) {
            stringBuilder.append(", ");
            stringBuilder.append("reason:");
            if (this.g == null) {
                stringBuilder.append("null");
            } else {
                stringBuilder.append(this.g);
            }
        }
        if (h()) {
            stringBuilder.append(", ");
            stringBuilder.append("packageName:");
            if (this.h == null) {
                stringBuilder.append("null");
            } else {
                stringBuilder.append(this.h);
            }
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
