package hlx.ui.heroslist;

import com.huluxia.framework.base.http.io.Response.ErrorListener;
import com.huluxia.framework.base.http.toolbox.error.VolleyError;
import com.huluxia.framework.base.notification.EventNotifyCenter;
import com.huluxia.module.n;

/* compiled from: RankingModuleWrapper */
class d$4 implements ErrorListener {
    final /* synthetic */ int aBO;
    final /* synthetic */ d bYA;

    d$4(d this$0, int i) {
        this.bYA = this$0;
        this.aBO = i;
    }

    public void onErrorResponse(VolleyError error) {
        EventNotifyCenter.notifyEvent(n.class, n.awG, new Object[]{Boolean.valueOf(false), Integer.valueOf(this.aBO), null});
    }
}
