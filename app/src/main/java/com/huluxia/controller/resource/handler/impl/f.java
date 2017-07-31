package com.huluxia.controller.resource.handler.impl;

import com.huluxia.controller.b;
import com.huluxia.controller.resource.bean.ResTaskInfo;
import com.huluxia.framework.base.utils.UtilsFunction;
import java.io.File;

/* compiled from: GbcHandler */
public class f extends p {
    public f(ResTaskInfo info) {
        super(info);
        if (UtilsFunction.empty(info.dir)) {
            info.dir = b.dE().dG() + File.separator + "GBC" + File.separator + info.filename;
        }
    }

    public String getSuffix() {
        return "zip";
    }
}
