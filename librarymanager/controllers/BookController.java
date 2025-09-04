package librarymanager.controllers;

import librarymanager.entities.Book;
import librarymanager.entities.Loan;
import librarymanager.repositories.BookRepository;
import librarymanager.repositories.LoanRepository;
import librarymanager.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


@RestController
    public class BookController {

    @Autowired
    private BookRepository bookRepo;
    @Autowired
    private ReviewRepository reviewRepo;
    @Autowired
    private LoanRepository loanRepo;

    public void getRating(Book bookParam){
        String avgRating = reviewRepo.findAverageRatingByBookId((long) bookParam.getId());
        bookParam.setAverageRating(avgRating);
        if(Objects.equals(bookParam.getAverageRating(), null)){
            bookParam.setAverageRating("no reviews found");
        }
    }

    @GetMapping("/get/book/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        Optional<Book> optionalBook = bookRepo.findById(id);
        if(optionalBook.isPresent()){
            Book book = optionalBook.get();
            getRating(book);
            return ResponseEntity.ok(book);
        }else{
            return ResponseEntity.badRequest().body("Book not found with id " + id);
        }
    }

    @GetMapping("/get/books")
    public List<Book> getAllBooks(){
        List<Book> allBooks = bookRepo.findAll();

        for (Book book : allBooks) {
            if(book != null) {
                getRating(book);
            }
        }

        return allBooks;
    }
    public ResponseEntity<?> bookValidation(Book book){
        if (book.getTitle() == null) {
            return ResponseEntity.badRequest().body("Book MUST have a title!");
        }
        if (book.getAuthor() == null) {
            return ResponseEntity.badRequest().body("Book MUST have an author!");
        }
        if(book.getPublicationYear()==null){
            return ResponseEntity.badRequest().body("Book MUST have a publication year!");
        }
        if(book.getPublicationYear() < 0){
            return ResponseEntity.badRequest().body("Book MUST have a valid publication year!");
        }

        return ResponseEntity.ok(book);
    }
    @PostMapping("/post/book")
    public ResponseEntity<?> addBook(@RequestBody Book addBook) {
        ResponseEntity<?> validation = bookValidation(addBook);
        if(!validation.getStatusCode().is2xxSuccessful()){
            return validation;
        }
        Book savedBook = bookRepo.save(addBook);
        return ResponseEntity.ok(savedBook);
    }

    @DeleteMapping("/delete/book/{id}")
    public ResponseEntity<?> deleteBookById(@PathVariable Long id){
        Optional<Book> optionalBook = bookRepo.findById(id);
        if(optionalBook.isPresent()){
            Book book = optionalBook.get();
            if(Objects.equals(book.getLoanStatus(), "Loaned")){
                List<Loan> loans = loanRepo.findLoansByBookId(id);
                loanRepo.deleteAll(loans);
//                loans.forEach(loan->loanRepo.save(loan));
            }
            bookRepo.deleteById(id);
        }else{
            return ResponseEntity.badRequest().body("Book not found with id " + id);
        }
        return ResponseEntity.ok("Book " + id + " deleted!");
    }

    @PutMapping("/put/book/{id}")
    public ResponseEntity<?> updateBook(@RequestBody Book newBook, @PathVariable Long id){
        Optional<Book> optionalBook = bookRepo.findById(id);
        if(optionalBook.isPresent()) {
            Book oldBook = optionalBook.get();
            oldBook.setAuthor(newBook.getAuthor());
            oldBook.setTitle(newBook.getTitle());
            oldBook.setPublicationYear(newBook.getPublicationYear());
            bookRepo.save(oldBook);

            return ResponseEntity.ok("Book " + id + " updated!");
        }else{
            return ResponseEntity.badRequest().body("Book with id " + id + " does not exist!");
        }
    }

    @GetMapping("/get/search/{bookName}")
    public  ResponseEntity<?> searchBook(@PathVariable String bookName) {
        if(bookRepo.findByTitle(bookName)!=null){
            Book book = bookRepo.findByTitle(bookName);
            getRating(book);
            return ResponseEntity.ok(book);
        }else{
            return ResponseEntity.badRequest().body("Book name does not exist!");
        }
    }

}
