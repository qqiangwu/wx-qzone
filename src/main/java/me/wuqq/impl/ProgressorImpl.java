package me.wuqq.impl;

import lombok.val;
import me.wuqq.core.Progressor;
import org.springframework.stereotype.Component;

/**
 * Created by wuqq on 16-9-30.
 */
@Component
public class ProgressorImpl implements Progressor {
    long mMax = 0;
    long mCurrent = 0;
    long mCurrentTick = 0;

    @Override
    public void init(final long maxProgress) {
        System.out.println();
        System.out.println("Start processing");

        mMax = maxProgress;

        this.initialRender();
    }

    @Override
    public void advance(final long advancedProgress) {
        mCurrent += advancedProgress;

        this.tick();
    }

    @Override
    public void done() {
        this.tick();

        if (mCurrentTick <= 50) {
            System.out.print('*');
        }

        System.out.println();
        System.out.println("Processing done");
    }

    private final void initialRender() {
        System.out.println();
        System.out.println("0%   10   20   30   40   50   60   70   80   90   100%");
        System.out.println("|----|----|----|----|----|----|----|----|----|----|");
    }

    private final void tick() {
        val ticksNeeded = (int) (50.0 * mCurrent / mMax);

        while (mCurrentTick < ticksNeeded) {
            System.out.print('*');
            ++mCurrentTick;
        }
    }
}
