package com.MCWorld.framework.base.http.transport;

import android.support.v4.util.Pair;
import com.MCWorld.framework.base.http.io.Request;
import com.MCWorld.framework.base.http.toolbox.error.AuthFailureError;
import com.MCWorld.framework.base.http.toolbox.error.InvalidParamError;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import org.apache.http.HttpResponse;

public interface HttpStack {
    Pair<HttpURLConnection, HttpResponse> performRequest(Request<?> request, Map<String, String> map, boolean z) throws IOException, AuthFailureError, InvalidParamError;
}
