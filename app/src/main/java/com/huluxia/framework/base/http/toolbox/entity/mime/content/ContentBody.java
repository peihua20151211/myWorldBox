package com.huluxia.framework.base.http.toolbox.entity.mime.content;

import java.io.IOException;
import java.io.OutputStream;

public interface ContentBody extends ContentDescriptor {
    String getFilename();

    void writeTo(OutputStream outputStream) throws IOException;
}
