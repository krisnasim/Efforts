package com.android.efforts.customclass;

/**
 * Created by jonat on 05/05/2017.
 */

import android.graphics.Bitmap;
import android.util.Log;

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

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by carson@convox.org on 5/10/15.
 */
public class ProtoBufRequest<ReqT extends Message, RespT extends Message> extends Request<RespT> {

    private ReqT request;
    private Bitmap image_request;
    private final Class<RespT> responseType;
    private final Listener<RespT> listener;
    private Map<String, String> headers;
    private static final String PROTOCOL_CONTENT_TYPE = "application/x-protobuf, application/json";
    private static final int SOCKET_TIMEOUT = 30000;

    public ProtoBufRequest(int method, String url, ReqT data, Class<RespT> responseType,
                           Listener<RespT> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = listener;
        this.request = data;
        this.responseType = responseType;
    }

    public ProtoBufRequest(int method, String url, ReqT data, Class<RespT> responseType, Map<String, String> headers,
                           Listener<RespT> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.headers = headers;
        this.listener = listener;
        this.request = data;
        this.responseType = responseType;
    }

    public ProtoBufRequest(String url, Class<RespT> responseType, Map<String, String> headers,
                           Listener<RespT> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, url, errorListener);
        this.headers = headers;
        this.listener = listener;
        //this.request = data;
        this.responseType = responseType;
    }

    //testing for image upload only
    public ProtoBufRequest(int method, String url, Bitmap data, Class<RespT> responseType, Map<String, String> headers,
                           Listener<RespT> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.headers = headers;
        this.listener = listener;
        this.image_request = data;
        this.responseType = responseType;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
//        Map<String, String> headers = new HashMap<String, String>();
//        headers.put("Charset", "UTF-8");
//        headers.put("Content-Type", PROTOCOL_CONTENT_TYPE);
//        headers.put("Accept", PROTOCOL_CONTENT_TYPE);

        return headers;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (request == null) {
            if(image_request == null) {
                return super.getBody();
            }
            else {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image_request.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                return stream.toByteArray();
            }
        }
        return request.toByteArray();
    }


    @Override
    protected Response<RespT> parseNetworkResponse(NetworkResponse response) {
        try {
            if (responseType == null) {
                throw new IllegalArgumentException("The response type was never provided.");
            }
//            RespT responseInstance = responseType.newInstance();

            return (Response<RespT>) Response.success(
                    responseType.getMethod("parseFrom", byte[].class).invoke(null, response.data),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            Log.d("valiError", e.getMessage());
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
    protected void deliverResponse(RespT response) {
        listener.onResponse(response);
    }
}
