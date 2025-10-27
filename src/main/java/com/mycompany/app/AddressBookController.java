package com.mycompany.app;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/addressbook")
public class AddressBookController {

    private final AddressBookRepository addressBookRepository;
    private final BuddyInfoRepository buddyInfoRepository;

    public AddressBookController(AddressBookRepository addressBookRepository,
                                 BuddyInfoRepository buddyInfoRepository) {
        this.addressBookRepository = addressBookRepository;
        this.buddyInfoRepository = buddyInfoRepository;
    }

    // ---------------------------
    // Thymeleaf GUI (Part 1: Traditional Forms)
    // ---------------------------

    // GET /addressbook/list  -> shows all address books
    @GetMapping("/list")
    public String listBuddies(Model model) {
        model.addAttribute("addressBooks", addressBookRepository.findAll());
        return "listBuddies";
    }

    // POST /addressbook/create -> create new address book (form submission)
    @PostMapping("/create")
    public String createAddressBook() {
        addressBookRepository.save(new AddressBook());
        return "redirect:/addressbook/list";
    }

    // GET /addressbook/{id} -> page showing buddies for a specific address book
    @GetMapping("/{id}")
    public String showBook(@PathVariable Long id, Model model) {
        model.addAttribute("book", addressBookRepository.findById(id).orElse(null));
        return "buddies";
    }

    // POST /addressbook/{id}/addBuddy -> add buddy via form
    @PostMapping("/{id}/addBuddy")
    public String addBuddyForm(@PathVariable Long id,
                               @RequestParam String name,
                               @RequestParam String address,
                               @RequestParam String phoneNumber) {
        addressBookRepository.findById(id).ifPresent(book -> {
            BuddyInfo buddy = new BuddyInfo(name, address, phoneNumber);
            BuddyInfo savedBuddy = buddyInfoRepository.save(buddy);
            book.addBuddy(savedBuddy);
            addressBookRepository.save(book);
        });
        return "redirect:/addressbook/" + id;
    }

    // POST /addressbook/{bookId}/removeBuddy/{buddyId} -> remove buddy via form
    @PostMapping("/{bookId}/removeBuddy/{buddyId}")
    public String removeBuddyForm(@PathVariable Long bookId, @PathVariable Long buddyId) {
        addressBookRepository.findById(bookId).ifPresent(book -> {
            book.removeBuddyById(buddyId);
            addressBookRepository.save(book);
            buddyInfoRepository.deleteById(buddyId);
        });
        return "redirect:/addressbook/" + bookId;
    }

    // ---------------------------
    // Single Page Application (Part 2)
    // ---------------------------

    // GET /addressbook/spa -> serve the SPA page
    @GetMapping("/spa")
    public String spa() {
        return "spa";
    }

    // ---------------------------
    // REST JSON API (used by SPA and tests)
    // ---------------------------

    // POST /addressbook/api  -> create an AddressBook
    @PostMapping(path = "/api")
    @ResponseBody
    public ResponseEntity<AddressBook> createBook() {
        AddressBook saved = addressBookRepository.save(new AddressBook());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // GET /addressbook/api/{id} -> get AddressBook (with buddies) as JSON
    @GetMapping(path = "/api/{id}")
    @ResponseBody
    public ResponseEntity<AddressBook> getBook(@PathVariable Long id) {
        return addressBookRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // POST /addressbook/api/{id}/buddies -> add Buddy (JSON body)
    @PostMapping(path = "/api/{id}/buddies")
    @ResponseBody
    public ResponseEntity<AddressBook> addBuddy(@PathVariable Long id, @RequestBody BuddyInfo buddy) {
        return addressBookRepository.findById(id).map(book -> {
            BuddyInfo savedBuddy = buddyInfoRepository.save(buddy);
            book.addBuddy(savedBuddy);
            AddressBook updated = addressBookRepository.save(book);
            return ResponseEntity.ok(updated);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // DELETE /addressbook/api/{bookId}/buddies/{buddyId} -> remove Buddy
    @DeleteMapping(path = "/api/{bookId}/buddies/{buddyId}")
    @ResponseBody
    public ResponseEntity<Void> removeBuddy(@PathVariable Long bookId, @PathVariable Long buddyId) {
        var opt = addressBookRepository.findById(bookId);
        if (opt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        AddressBook book = opt.get();
        book.removeBuddyById(buddyId);
        addressBookRepository.save(book);
        buddyInfoRepository.deleteById(buddyId);
        return ResponseEntity.noContent().build();
    }
}