package com.mycompany.app;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddressBookControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String url(String path) {
        return "http://localhost:" + port + "/addressbook/api" + path;
    }

    @Test
    public void testCreateAndGetAddressBook() {
        ResponseEntity<AddressBook> response = restTemplate.postForEntity(url(""), null, AddressBook.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().getId());

        Long id = response.getBody().getId();
        ResponseEntity<AddressBook> getResponse = restTemplate.getForEntity(url("/" + id), AddressBook.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    }

    @Test
    public void testAddAndRemoveBuddy() {
        ResponseEntity<AddressBook> bookResponse = restTemplate.postForEntity(url(""), null, AddressBook.class);
        Long bookId = bookResponse.getBody().getId();

        BuddyInfo buddy = new BuddyInfo("Alice", "123 Main St", "555-1234");
        ResponseEntity<AddressBook> addResponse = restTemplate.postForEntity(
                url("/" + bookId + "/buddies"), buddy, AddressBook.class);

        assertEquals(1, addResponse.getBody().getBuddyList().size());
        assertEquals("Alice", addResponse.getBody().getBuddyList().get(0).getName());

        Long buddyId = addResponse.getBody().getBuddyList().get(0).getId();
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                url("/" + bookId + "/buddies/" + buddyId), HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
    }

    @Test
    public void testNotFoundErrors() {
        ResponseEntity<AddressBook> response = restTemplate.getForEntity(url("/999999"), AddressBook.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
