
package com.mycompany.app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class JPATest {

    public static void main(String[] args) {
        SpringApplication.run(JPATest.class, args);
    }

    // no @Bean needed here because we use a @Component (DemoRunner) below
}

/**
 * Separate component so @Transactional actually applies.
 * Spring will auto-wire the repositories and run this at startup.
 */
@Component
class DemoRunner implements CommandLineRunner {

    private final AddressBookRepository abRepo;
    private final BuddyInfoRepository buddyRepo;

    DemoRunner(AddressBookRepository abRepo, BuddyInfoRepository buddyRepo) {
        this.abRepo = abRepo;
        this.buddyRepo = buddyRepo;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // --- persist one BuddyInfo by itself ---
        buddyRepo.save(new BuddyInfo("Jared", "232 Falconia Ave", "34162086"));

        // --- Persist AddressBook with 2 buddies (tests OneToMany + cascade) ---
        BuddyInfo alice = new BuddyInfo("Alice", "1 Main St", "5551234");
        BuddyInfo bob   = new BuddyInfo("Bob",   "2 Pine Ave", "5555678");

        AddressBook book = new AddressBook();
        book.addBuddy(alice);
        book.addBuddy(bob);

        book = abRepo.save(book); // CascadeType.ALL should save the two BuddyInfo rows too

        // --- Clear & reload equivalent: just re-fetch from the repository ---
        Optional<AddressBook> reloadedOpt = abRepo.findById(book.getId());
        AddressBook reloaded = reloadedOpt.orElseThrow();
        System.out.println("Reloaded book id=" + reloaded.getId()
                + " buddies=" + reloaded.getBuddyList().size());

        // --- Show all BuddyInfo rows currently in the DB ---
        Iterable<BuddyInfo> all = buddyRepo.findAll();
        long total = buddyRepo.count();
        System.out.println("Total buddies in DB: " + total);
        for (BuddyInfo b : all) {
            System.out.println(b.getId() + " -> " + b);
        }

        // --- (Optional) Test orphanRemoval: remove one buddy from the book ---
        long before = buddyRepo.count();

        if (!reloaded.getBuddyList().isEmpty()) {
            // remove first buddy and save; orphanRemoval=true should delete the child row
            reloaded.removeBuddy(0);
            abRepo.save(reloaded);
        }

        long after = buddyRepo.count();
        System.out.println("Buddy rows before=" + before + " after=" + after);
    }
}




