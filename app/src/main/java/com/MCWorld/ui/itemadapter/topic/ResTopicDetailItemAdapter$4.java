package com.MCWorld.ui.itemadapter.topic;

import android.view.View;
import android.view.View.OnClickListener;

class ResTopicDetailItemAdapter$4 implements OnClickListener {
    final /* synthetic */ ResTopicDetailItemAdapter aWL;

    ResTopicDetailItemAdapter$4(ResTopicDetailItemAdapter this$0) {
        this.aWL = this$0;
    }

    public void onClick(View arg0) {
        if (ResTopicDetailItemAdapter.b(this.aWL) != null) {
            ResTopicDetailItemAdapter.b(this.aWL).a(true, null);
        }
    }
}
