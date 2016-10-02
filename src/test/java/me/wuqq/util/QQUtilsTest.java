package me.wuqq.util;

import lombok.val;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by wuqq on 16-9-30.
 */
public class QQUtilsTest {
    @Test
    @Ignore(value = "New cookie required for each test")
    public void testFetch() throws BadCredentialException {
        val cookie = "p_uin=o0373490201; p_skey=gKKYIG5niKzkjxEMyLc9zHwk6Vu7k*O-ChhBRj1F1dA_";
        val fetcher = new QZoneFetcher(cookie);
        val content = fetcher.fetchMessagesFromOffset(0);

        Assert.assertEquals("373490201", fetcher.getTargetQQ());

        System.out.println(content);
    }
}
