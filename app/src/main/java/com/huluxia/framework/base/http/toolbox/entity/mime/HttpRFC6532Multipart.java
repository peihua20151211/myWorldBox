package com.huluxia.framework.base.http.toolbox.entity.mime;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

class HttpRFC6532Multipart extends AbstractMultipartForm {
    private final List<FormBodyPart> parts;

    public HttpRFC6532Multipart(Charset charset, String boundary, List<FormBodyPart> parts) {
        super(charset, boundary);
        this.parts = parts;
    }

    public List<FormBodyPart> getBodyParts() {
        return this.parts;
    }

    protected void formatMultipartHeader(FormBodyPart part, OutputStream out) throws IOException {
        Iterator it = part.getHeader().iterator();
        while (it.hasNext()) {
            AbstractMultipartForm.writeField((MinimalField) it.next(), MIME.UTF8_CHARSET, out);
        }
    }
}
