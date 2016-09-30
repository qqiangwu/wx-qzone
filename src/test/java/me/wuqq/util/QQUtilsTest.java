package me.wuqq.util;

import lombok.val;
import me.wuqq.core.Fetcher.InvalidCredentialException;
import me.wuqq.domain.Credential;
import okhttp3.OkHttpClient;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by wuqq on 16-9-30.
 */
public class QQUtilsTest {
    @Test
    @Ignore
    public void testFetch() throws InvalidCredentialException {
        val httpclient = new OkHttpClient.Builder().build();
        val credential = Credential.fromCookie("p_uin=o0373490201; p_skey=GYatlPmdAIAz9uEvWPA8basA0JrkyCy7hn6jpbwc23U_");
        val content = QQUtils.rawFetch(httpclient, credential, 0);

        System.out.println(content);
    }
}
