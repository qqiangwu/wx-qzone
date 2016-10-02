package me.wuqq.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.val;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

/**
 * Created by wuqq on 2016/10/2.
 */
public final class QZoneFetcher {
    public static final String COOKIE_P_UIN = "p_uin";
    public static final String COOKIE_P_SKEY = "p_skey";
    public static final int RECORD_PAGE_SIZE = 20;
    public static final String ENDPOINT = "https://h5.qzone.qq.com/proxy/domain/taotao.qq.com/cgi-bin/emotion_cgi_msglist_v6";

    private final String mCookie;
    private final String mQQ;
    private final int mGTK;
    private final OkHttpClient mClient;
    private final ObjectMapper mMapper;

    public QZoneFetcher(final String cookie) throws BadCredentialException {
        Objects.requireNonNull(cookie, "Cookie required");

        mClient = new OkHttpClient();
        mMapper = new ObjectMapper();

        try {
            val pskey = getCookieEntry(cookie, COOKIE_P_SKEY);
            val uin = getCookieEntry(cookie, COOKIE_P_UIN);

            mCookie = cookie;
            mQQ = uin.substring(2);
            mGTK = QZoneFetcher.computeGTK(pskey);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialException(e);
        }
    }

    private static final String getCookieEntry(final String cookie, final String key) {
        val pos = cookie.indexOf(key);

        if (pos == -1) {
            throw new IllegalArgumentException(key + " required");
        }

        try {
            val endPos = cookie.indexOf(';', pos);
            val adjustedEndPos = endPos == -1 ? cookie.length() : endPos;
            val value = cookie.substring(pos, adjustedEndPos);

            return value.split("=")[1];
        } catch (Exception ignored) {
            throw new IllegalArgumentException(key + " required");
        }
    }

    private static final int computeGTK(final String skey) {
        int hash = 5381;

        for (val ch: skey.toCharArray()) {
            hash += (hash << 5) + ch;
        }

        return hash & 0x7fffffff;
    }

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

    public String getTargetQQ() {
        return mQQ;
    }
}
