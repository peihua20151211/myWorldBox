package com.MCWorld.module;

import com.MCWorld.framework.base.http.io.Response.ErrorListener;
import com.MCWorld.framework.base.http.toolbox.error.VolleyError;
import com.MCWorld.framework.base.log.HLog;

/* compiled from: RecommendAppModule */
class q$9 implements ErrorListener {
    q$9() {
    }

    public void onErrorResponse(VolleyError error) {
        HLog.error(this, "requestEnterRecommendAppCount onErrorResponse e = " + error, new Object[0]);
    }
}
