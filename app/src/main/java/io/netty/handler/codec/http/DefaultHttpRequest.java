package io.netty.handler.codec.http;

import io.netty.util.internal.ObjectUtil;

public class DefaultHttpRequest extends DefaultHttpMessage implements HttpRequest {
    private static final int HASH_CODE_PRIME = 31;
    private HttpMethod method;
    private String uri;

    public DefaultHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri) {
        this(httpVersion, method, uri, true);
    }

    public DefaultHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri, boolean validateHeaders) {
        super(httpVersion, validateHeaders, false);
        this.method = (HttpMethod) ObjectUtil.checkNotNull(method, "method");
        this.uri = (String) ObjectUtil.checkNotNull(uri, "uri");
    }

    public DefaultHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri, HttpHeaders headers) {
        super(httpVersion, headers);
        this.method = (HttpMethod) ObjectUtil.checkNotNull(method, "method");
        this.uri = (String) ObjectUtil.checkNotNull(uri, "uri");
    }

    @Deprecated
    public HttpMethod getMethod() {
        return method();
    }

    public HttpMethod method() {
        return this.method;
    }

    @Deprecated
    public String getUri() {
        return uri();
    }

    public String uri() {
        return this.uri;
    }

    public HttpRequest setMethod(HttpMethod method) {
        if (method == null) {
            throw new NullPointerException("method");
        }
        this.method = method;
        return this;
    }

    public HttpRequest setUri(String uri) {
        if (uri == null) {
            throw new NullPointerException("uri");
        }
        this.uri = uri;
        return this;
    }

    public HttpRequest setProtocolVersion(HttpVersion version) {
        super.setProtocolVersion(version);
        return this;
    }

    public int hashCode() {
        return ((((this.method.hashCode() + 31) * 31) + this.uri.hashCode()) * 31) + super.hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof DefaultHttpRequest)) {
            return false;
        }
        DefaultHttpRequest other = (DefaultHttpRequest) o;
        if (method().equals(other.method()) && uri().equalsIgnoreCase(other.uri()) && super.equals(o)) {
            return true;
        }
        return false;
    }

    public String toString() {
        return this;
    }
}
