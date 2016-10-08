package me.wuqq.util;

import lombok.val;

/**
 * Created by wuqq on 16-10-8.
 */
public abstract class FetcherUtil {

    public static final int computeGTK(final String skey) {
        int hash = 5381;

        for (val ch: skey.toCharArray()) {
            hash += (hash << 5) + ch;
        }

        return hash & 0x7fffffff;
    }

    public static final String getCookieEntry(final String cookie, final String key) {
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
}
