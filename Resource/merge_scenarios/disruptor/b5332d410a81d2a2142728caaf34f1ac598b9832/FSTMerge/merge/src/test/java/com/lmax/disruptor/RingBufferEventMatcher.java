package com.lmax.disruptor; 

import static org.hamcrest.CoreMatchers.is; 

import org.hamcrest.Description; 
import org.hamcrest.Factory; 
import org.hamcrest.Matcher; 
import org.hamcrest.TypeSafeMatcher; 

final  class  RingBufferEventMatcher  extends TypeSafeMatcher<RingBuffer<Object[]>> {
	
    private final Matcher<?>[] expectedValueMatchers;

	

    private RingBufferEventMatcher(final Matcher<?>[] expectedValueMatchers)
    {
        this.expectedValueMatchers = expectedValueMatchers;
    }


	

    @Factory
    public static RingBufferEventMatcher ringBufferWithEvents(final Matcher<?>... valueMatchers)
    {
        return new RingBufferEventMatcher(valueMatchers);
    }


	

    @Factory
    public static RingBufferEventMatcher ringBufferWithEvents(final Object... values)
    {
        Matcher<?>[] valueMatchers = new Matcher[values.length];
        for (int i = 0; i < values.length; i++)
        {
            final Object value = values[i];
            valueMatchers[i] = is(value);
        }
        return new RingBufferEventMatcher(valueMatchers);
    }


	

    @Override
    public boolean matchesSafely(final RingBuffer<Object[]> ringBuffer)
    {
        boolean matches = true;
        for (int i = 0; i < expectedValueMatchers.length; i++)
        {
            final Matcher<?> expectedValueMatcher = expectedValueMatchers[i];
            matches &= expectedValueMatcher.matches(ringBuffer.get(i)[0]);
        }
        return matches;
    }


	

    @Override
    public void describeTo(final Description description)
    {
        description.appendText("Expected ring buffer with events matching: ");
<<<<<<< /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/result/merge/fstmerge_tmp1647844036883/fstmerge_var1_4643528239924560340

        for (Matcher<?> expectedValueMatcher : expectedValueMatchers)
        {
            expectedValueMatcher.describeTo(description);
        }
=======
        //allOf(expectedValueMatchers).describeTo(description);
>>>>>>> /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/result/merge/fstmerge_tmp1647844036883/fstmerge_var2_15696245446593612
    }



}
