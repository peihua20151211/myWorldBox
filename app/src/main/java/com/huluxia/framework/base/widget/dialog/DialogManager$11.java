package com.huluxia.framework.base.widget.dialog;

import android.view.View;
import android.view.View.OnClickListener;
import com.huluxia.framework.base.widget.dialog.DialogManager.OkCancelDialogListener;

class DialogManager$11 implements OnClickListener {
    final /* synthetic */ DialogManager this$0;
    final /* synthetic */ OkCancelDialogListener val$l;

    DialogManager$11(DialogManager this$0, OkCancelDialogListener okCancelDialogListener) {
        this.this$0 = this$0;
        this.val$l = okCancelDialogListener;
    }

    public void onClick(View v) {
        this.this$0.mDialog.dismiss();
        if (this.val$l != null) {
            this.val$l.onCancel();
        }
    }
}
