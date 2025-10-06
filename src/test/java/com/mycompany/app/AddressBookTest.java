package com.mycompany.app;

import org.junit.Test;
import static org.junit.Assert.*;

public class AddressBookTest {

    @Test
    public void addAndRemoveBuddy() {
        AddressBook book = new AddressBook();
        assertEquals(0, book.size());

        BuddyInfo paul = new BuddyInfo("Paul", "Carleton", "613");
        book.addBuddy(paul);
        assertEquals(1, book.size());
        assertEquals(paul, book.getBuddyList().get(0));

        BuddyInfo removed = book.removeBuddy(0);
        assertEquals(paul, removed);
        assertEquals(0, book.size());
    }

    @Test
    public void removeInvalidIndexReturnsNull() {
        AddressBook book = new AddressBook();
        assertNull(book.removeBuddy(-1));
        assertNull(book.removeBuddy(5));
    }
}
