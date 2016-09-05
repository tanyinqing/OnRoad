package com.xlw.onroad;

import android.test.InstrumentationTestCase;

/**
 * Created by xinliwei on 2015/7/5.
 */
public class ExampeTest extends InstrumentationTestCase {
    public void test() throws Exception {
        final int expected = 1;
        final int reality = 5;
        assertEquals(expected, reality);
    }
}
