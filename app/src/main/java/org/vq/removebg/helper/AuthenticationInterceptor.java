package org.vq.removebg.helper;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;

public class AuthenticationInterceptor implements Interceptor {
    private String key;

    public AuthenticationInterceptor(String key) {
        this.key = key;
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder builder = original.newBuilder()
                .header("X-API-Key", key);
        Request request = builder.build();
        return chain.proceed(request);
    }
}
