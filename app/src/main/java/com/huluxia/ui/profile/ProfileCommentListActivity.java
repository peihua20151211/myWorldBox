package com.huluxia.ui.profile;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import com.huluxia.bbs.b.g;
import com.huluxia.bbs.b.i;
import com.huluxia.bbs.b.m;
import com.huluxia.data.j;
import com.huluxia.data.topic.CommentItem;
import com.huluxia.data.topic.TopicItem;
import com.huluxia.http.profile.f;
import com.huluxia.t;
import com.huluxia.ui.base.HTBaseTableActivity;
import com.huluxia.ui.itemadapter.profile.ProfileCommentItemAdapter;
import com.huluxia.widget.pulltorefresh.PullToRefreshListView;

public class ProfileCommentListActivity extends HTBaseTableActivity {
    private f bfG;
    private long userid = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(i.activity_topic_list);
        this.userid = getIntent().getLongExtra(FriendListActivity.EXTRA_USER_ID, 0);
        bo(this.userid);
        Log.i("ProfileCommentListActivity", Long.toString(this.userid));
        this.aJh = (PullToRefreshListView) findViewById(g.listViewData);
        super.a(g.listViewData, new ProfileCommentItemAdapter(this, this.arrayList));
        this.bfG = new f();
        this.bfG.w(this.userid);
        this.bfG.a(this);
        this.aJh.PT();
    }

    private void bo(long userid) {
        ej(getResources().getString(userid == j.ep().getUserid() ? m.my_comments : m.his_comments));
    }

    public void a(AdapterView<?> adapterView, View arg1, int position, long arg3) {
        CommentItem data = (CommentItem) this.arrayList.get(position);
        TopicItem topic = data.getTopicItem();
        topic.setCategoryName(data.getTopicCategory().getTitle());
        t.a((Context) this, topic);
    }

    public void reload() {
        this.bfG.aK("0");
        this.bfG.setCount(20);
        this.bfG.execute();
    }

    public void EY() {
        this.bfG.execute();
    }
}
