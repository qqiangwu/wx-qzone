package me.wuqq.domain;

import lombok.Value;
import lombok.val;
import me.wuqq.util.QQUtils;

/**
 * Created by wuqq on 16-9-30.
 */
@Value
public class Credential {
    String cookie;
    String qq;
    int gtk;

    public static final Credential fromCookie(final String cookie) {
        val pskey = getCookieEntry(cookie, "p_skey");
        val uin = getCookieEntry(cookie, "p_uin");

        val qq = uin.substring(2);
        val gtk = QQUtils.computeGTK(pskey);

        return new Credential(cookie, qq, gtk);
    }

    private static final String getCookieEntry(final String cookie, final String key) {
        val pos = cookie.indexOf(key);

        if (pos == -1) {
            throw new IllegalArgumentException("Bad cookie provided");
        }

        try {
            val endPos = cookie.indexOf(';', pos);
            val adjustedEndPos = endPos == -1 ? cookie.length() : endPos;
            val value = cookie.substring(pos, adjustedEndPos);

            return value.split("=")[1];
        } catch (Exception ignored) {
            throw new IllegalArgumentException("Bad cookie provided");
        }
    }
}
