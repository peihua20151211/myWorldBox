package com.umeng.analytics.game;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.io.Serializable;
import u.aly.s;
import u.aly.u;

/* compiled from: GameState */
public class b {
    public String a;
    public String b;
    private Context c;
    private final String d = "um_g_cache";
    private final String e = "single_level";
    private final String f = "stat_player_level";
    private final String g = "stat_game_level";
    private a h = null;

    /* compiled from: GameState */
    static class a implements Serializable {
        private static final long a = 20140327;
        private String b;
        private long c;
        private long d;

        public a(String str) {
            this.b = str;
        }

        public boolean a(String str) {
            return this.b.equals(str);
        }

        public void a() {
            this.d = System.currentTimeMillis();
        }

        public void b() {
            this.c += System.currentTimeMillis() - this.d;
            this.d = 0;
        }

        public void c() {
            a();
        }

        public void d() {
            b();
        }

        public long e() {
            return this.c;
        }

        public String f() {
            return this.b;
        }
    }

    public b(Context context) {
        this.c = context;
    }

    public a a(String str) {
        this.h = new a(str);
        this.h.a();
        return this.h;
    }

    public void a() {
        if (this.h != null) {
            this.h.b();
            Editor edit = this.c.getSharedPreferences("um_g_cache", 0).edit();
            edit.putString("single_level", s.a(this.h));
            edit.putString("stat_player_level", this.b);
            edit.putString("stat_game_level", this.a);
            edit.commit();
        }
    }

    public void b() {
        SharedPreferences a = u.a(this.c, "um_g_cache");
        String string = a.getString("single_level", null);
        if (string != null) {
            this.h = (a) s.a(string);
            if (this.h != null) {
                this.h.c();
            }
        }
        if (this.b == null) {
            this.b = a.getString("stat_player_level", null);
        }
        if (this.a == null) {
            this.a = a.getString("stat_game_level", null);
        }
    }

    public a b(String str) {
        if (this.h != null) {
            this.h.d();
            if (this.h.a(str)) {
                a aVar = this.h;
                this.h = null;
                return aVar;
            }
        }
        return null;
    }
}
