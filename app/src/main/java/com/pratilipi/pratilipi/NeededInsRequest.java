package com.pratilipi.pratilipi;

/**
 * Created by Nitish on 29-05-2015.
 */
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.InputStream;

public class NeededInsRequest extends Request<byte[]> {
    private final Response.Listener<byte[]> mListener;

    public NeededInsRequest(int method, String url, Response.Listener<byte[]> listener,
                            Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        // this request would never use cache.
        setShouldCache(false);
        mListener = listener;
    }

    @Override
    protected void deliverResponse(byte[] response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        if (response instanceof MyNetworkResponse) {
            // take the InputStream here.
            InputStream ins = ((MyNetworkResponse) response).ins;
        }
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
    }
}
