package me.wuqq.core;

/**
 * Created by wuqq on 16-9-30.
 */
public interface Progressor {
    void init(long maxProgress);

    void advance(long advancedProgress);

    void done();
}
