package com.mycompany.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    // seed a little data so /addressbook/list shows something
    @Bean
    CommandLineRunner seed(AddressBookRepository books, BuddyInfoRepository buddies) {
        return args -> {
            if (books.count() == 0) {
                AddressBook ab1 = books.save(new AddressBook());
                AddressBook ab2 = books.save(new AddressBook());

                BuddyInfo p = buddies.save(new BuddyInfo("Paul", "Carleton", "613"));
                BuddyInfo a = buddies.save(new BuddyInfo("Alice", "Toronto", "416"));

                ab1.addBuddy(p);
                ab1.addBuddy(a);
                books.save(ab1);

                books.save(ab2); // empty book
            }
        };
    }
}

