package com.mycompany.app;

import org.junit.Test;
import static org.junit.Assert.*;

public class BuddyInfoTest {

    @Test
    public void constructorAndGettersWork() {
        BuddyInfo b = new BuddyInfo("Alice", "Toronto", "416");
        assertEquals("Alice", b.getName());
        assertEquals("Toronto", b.getAddress());
        assertEquals("416", b.getPhoneNumber());
    }

    @Test
    public void toStringIsReadable() {
        BuddyInfo b = new BuddyInfo("Bob", "Ottawa", "613");
        String s = b.toString();
        assertTrue(s.contains("Bob"));
        assertTrue(s.contains("Ottawa"));
        assertTrue(s.contains("613"));
    }
}
