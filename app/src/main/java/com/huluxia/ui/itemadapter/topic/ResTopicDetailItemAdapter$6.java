package com.huluxia.ui.itemadapter.topic;

import android.view.View;
import android.view.View.OnClickListener;
import com.huluxia.data.topic.a;
import com.huluxia.framework.base.notification.EventNotifyCenter;
import com.huluxia.module.n;
import com.huluxia.t;

class ResTopicDetailItemAdapter$6 implements OnClickListener {
    final /* synthetic */ ResTopicDetailItemAdapter aWL;
    final /* synthetic */ a aWN;

    ResTopicDetailItemAdapter$6(ResTopicDetailItemAdapter this$0, a aVar) {
        this.aWL = this$0;
        this.aWN = aVar;
    }

    public void onClick(View v) {
        if (ResTopicDetailItemAdapter.e(this.aWL) != null && ResTopicDetailItemAdapter.e(this.aWL).studio != null) {
            EventNotifyCenter.notifyEventUiThread(n.class, n.axl, new Object[]{ResTopicDetailItemAdapter.a(this.aWL), Integer.valueOf(ResTopicDetailItemAdapter.e(this.aWL).studio.id)});
        } else if (ResTopicDetailItemAdapter.e(this.aWL) == null || ResTopicDetailItemAdapter.e(this.aWL).user == null) {
            EventNotifyCenter.notifyEventUiThread(n.class, n.axl, new Object[]{ResTopicDetailItemAdapter.a(this.aWL), Integer.valueOf(this.aWN.id)});
        } else {
            t.f(ResTopicDetailItemAdapter.a(this.aWL), ResTopicDetailItemAdapter.e(this.aWL).user.userID);
        }
    }
}
