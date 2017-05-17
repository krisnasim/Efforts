package com.android.efforts.customclass;

/**
 * Created by jonat on 05/05/2017.
 */

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.protobuf.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by carson@convox.org on 5/10/15.
 */
public class ProtoBufRequest<ReqT extends Message, JSONObject extends Message> extends Request<JSONObject> {

    private ReqT request;
    private final Class<JSONObject> responseType;
    private final Listener<JSONObject> listener;
    private static final String PROTOCOL_CONTENT_TYPE = "application/x-protobuf";
    private static final int SOCKET_TIMEOUT = 30000;

    public ProtoBufRequest(int method, String url, ReqT data, Class<JSONObject> responseType,
                           Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = listener;
        this.request = data;
        this.responseType = responseType;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Charset", "UTF-8");
        headers.put("Content-Type", PROTOCOL_CONTENT_TYPE);
        headers.put("Accept", PROTOCOL_CONTENT_TYPE);

        return headers;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (request == null) {
            return super.getBody();
        }
        return request.toByteArray();
    }


    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            if (responseType == null) {
                throw new IllegalArgumentException("The response type was never provided.");
            }
            JSONObject responseInstance = responseType.newInstance();
            return (Response<JSONObject>) Response.success(
                    responseInstance.newBuilderForType().mergeFrom(response.data).build(),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    public RetryPolicy getRetryPolicy() {
        RetryPolicy retryPolicy = new DefaultRetryPolicy(SOCKET_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        return retryPolicy;
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        listener.onResponse(response);
    }
}
