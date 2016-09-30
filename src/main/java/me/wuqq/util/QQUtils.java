package me.wuqq.util;

import lombok.val;
import me.wuqq.core.Fetcher.InvalidCredentialException;
import me.wuqq.domain.Credential;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by wuqq on 16-9-30.
 */
public abstract class QQUtils {
    public static final int RECORD_PAGE_SIZE = 20;
    public static final String ENDPOINT = "https://h5.qzone.qq.com/proxy/domain/taotao.qq.com/cgi-bin/emotion_cgi_msglist_v6";

    public static final int computeGTK(final String skey) {
        int hash = 5381;

        for (val ch: skey.toCharArray()) {
            hash += (hash << 5) + ch;
        }

        return hash & 0x7fffffff;
    }

    public static final String rawFetch(
            final OkHttpClient client,
            final Credential credential,
            final int offset) throws InvalidCredentialException {
        try {
            val endpoint = new URI(ENDPOINT);
            val url = new HttpUrl.Builder()
                    .scheme(endpoint.getScheme())
                    .host(endpoint.getHost())
                    .encodedPath(endpoint.getPath())
                    .addQueryParameter("uin", credential.getQq())
                    .addQueryParameter("inCharset", "utf-8")
                    .addQueryParameter("outCharset", "utf-8")
                    .addQueryParameter("hostUin", credential.getQq())
                    .addQueryParameter("notice", String.valueOf(0))
                    .addQueryParameter("sort", String.valueOf(0))
                    .addQueryParameter("pos", String.valueOf(offset))
                    .addQueryParameter("num", String.valueOf(RECORD_PAGE_SIZE))
                    .addQueryParameter("code_version", String.valueOf(1))
                    .addQueryParameter("need_private_comment", String.valueOf(1))
                    .addQueryParameter("g_tk", String.valueOf(credential.getGtk()))
                    .addQueryParameter("callback", "x")
                    .build();

            val request = new Request.Builder()
                    .url(url)
                    .addHeader("Cookie", credential.getCookie())
                    .build();
            val response = client.newCall(request).execute();

            switch (response.code()) {
                case 200:
                    return retrieveContent(response);

                case 403:
                    throw new InvalidCredentialException();

                default:
                    throw new RuntimeException("Error in QZone server");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error in network", e);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Impossible");
        }
    }

    private static final String retrieveContent(final Response response) throws IOException {
        val body = response.body().string();

        if (!body.startsWith("x(")) {
            throw new RuntimeException(String.format("Bad response: %s", body));
        }

        val content = body.substring(2, body.length() - 2);

        return content;
    }
}
