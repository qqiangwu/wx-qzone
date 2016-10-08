package me.wuqq.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.val;
import me.wuqq.core.BadCredentialException;
import me.wuqq.core.Fetcher;
import me.wuqq.util.FetcherUtil;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

/**
 * Created by wuqq on 16-9-30.
 */
@Component
public class FetcherImpl implements Fetcher {
    private static final String COOKIE_P_UIN = "p_uin";
    private static final String COOKIE_P_SKEY = "p_skey";
    private static final int RECORD_PAGE_SIZE = 20;
    private static final String ENDPOINT = "https://h5.qzone.qq.com/proxy/domain/taotao.qq.com/cgi-bin/emotion_cgi_msglist_v6";

    private final String mCookie;
    private final String mQQ;
    private final int mGTK;
    private final OkHttpClient mClient;
    private final ObjectMapper mMapper;

    public FetcherImpl(final @Value("${nonOptionArgs}") String cookie) throws BadCredentialException {
        Objects.requireNonNull(cookie, "Cookie required");

        mClient = new OkHttpClient();
        mMapper = new ObjectMapper();

        try {
            val pskey = FetcherUtil.getCookieEntry(cookie, COOKIE_P_SKEY);
            val uin = FetcherUtil.getCookieEntry(cookie, COOKIE_P_UIN);

            mCookie = cookie;
            mQQ = uin.substring(2);
            mGTK = FetcherUtil.computeGTK(pskey);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialException(e);
        }
    }

    @Override
    public JsonNode fetchMessagesFromOffset(final int offset) throws BadCredentialException {
        val url = this.prepareUrl(offset);
        val response = this.doFetch(url);

        return this.checkAndRetrieve(response);
    }

    @SneakyThrows
    private HttpUrl prepareUrl(final int offset) {
        val endpoint = new URI(ENDPOINT);
        val url = new HttpUrl.Builder()
                .scheme(endpoint.getScheme())
                .host(endpoint.getHost())
                .encodedPath(endpoint.getPath())
                .addQueryParameter("uin", mQQ)
                .addQueryParameter("inCharset", "utf-8")
                .addQueryParameter("outCharset", "utf-8")
                .addQueryParameter("hostUin", mQQ)
                .addQueryParameter("notice", String.valueOf(0))
                .addQueryParameter("sort", String.valueOf(0))
                .addQueryParameter("pos", String.valueOf(offset))
                .addQueryParameter("num", String.valueOf(RECORD_PAGE_SIZE))
                .addQueryParameter("code_version", String.valueOf(1))
                .addQueryParameter("need_private_comment", String.valueOf(1))
                .addQueryParameter("g_tk", String.valueOf(mGTK))
                .addQueryParameter("callback", "x")
                .build();
        return url;
    }

    @SneakyThrows
    private Response doFetch(HttpUrl url) {
        val request = new Request.Builder()
                .url(url)
                .addHeader("Cookie", mCookie)
                .build();
        val response = mClient.newCall(request).execute();

        return response;
    }

    private JsonNode checkAndRetrieve(final Response response) throws BadCredentialException {
        switch (response.code()) {
            case 200:
                val jsonpStr = retrieveJsonpBody(response);
                return this.verifyJsonp(jsonpStr);

            case 403:
                throw new BadCredentialException();

            default:
                throw new RuntimeException("Error in QZone server");
        }
    }

    @SneakyThrows
    private String retrieveJsonpBody(final Response response) {
        val body = response.body().string();

        if (!body.startsWith("x(")) {
            throw new RuntimeException(String.format("Bad response: %s", body));
        }

        val content = body.substring(2, body.length() - 2);

        return content;
    }

    private JsonNode verifyJsonp(final String jsonpBody) throws BadCredentialException {
        try {
            val json = mMapper.readTree(jsonpBody);

            if (!this.isValidResponse(json)) {
                throw new BadCredentialException();
            }

            return json;
        } catch (IOException e) {
            throw new RuntimeException("Bad QZone response", e);
        }
    }

    private boolean isValidResponse(final JsonNode json) {
        return json.path("code").asInt() == 0;
    }

    @Override
    public String getTargetQQ() {
        return mQQ;
    }

    @Override
    public int getPageSize() {
        return RECORD_PAGE_SIZE;
    }
}
