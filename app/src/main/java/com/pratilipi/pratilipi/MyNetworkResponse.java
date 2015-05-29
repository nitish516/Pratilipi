package com.pratilipi.pratilipi;

/**
 * Created by Nitish on 29-05-2015.
 */
import com.android.volley.NetworkResponse;

import java.io.InputStream;
import java.util.Map;

public class MyNetworkResponse extends NetworkResponse {
    public MyNetworkResponse(int statusCode, byte[] data, InputStream ins,
                             Map<String, String> headers, boolean notModified) {
        super(statusCode, data, headers, notModified);
        this.ins = ins;
    }

    public MyNetworkResponse(byte[] data, InputStream ins) {
        super(data);
        this.ins = ins;
    }

    public MyNetworkResponse(byte[] data, InputStream ins, Map<String, String> headers) {
        super(data, headers);
        this.ins = ins;
    }

    public final InputStream ins;
}
