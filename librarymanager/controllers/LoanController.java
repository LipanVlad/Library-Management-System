package librarymanager.controllers;

import librarymanager.entities.Book;
import librarymanager.entities.Loan;
import librarymanager.repositories.BookRepository;
import librarymanager.repositories.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
public class LoanController {
    @Autowired
    LoanRepository loanRepo;
    @Autowired
    BookRepository bookRepo;
    @Autowired
    librarymanager.controllers.BookController bookController;

    public ResponseEntity<?> loanValidation(Loan loan){
        if (loan.getLoanEndingDate() == null) {
            return ResponseEntity.badRequest().body("Loan MUST have an ending date!");
        }
        if (loan.getLoanStartingDate() == null) {
            return ResponseEntity.badRequest().body("Loan MUST have a starting date!");
        }
        if(loan.getBookId()==0){
            return ResponseEntity.badRequest().body("Loan MUST have a book id!");
        }

        return ResponseEntity.ok(loan);
    }

    @PostMapping("/post/loan")
    public ResponseEntity<?> loanABook(@RequestBody Loan addLoan) {
        ResponseEntity<?> validation = loanValidation(addLoan);
        if(!validation.getStatusCode().is2xxSuccessful()){
            return validation;
        }
        int bookId = addLoan.getBookId();
        Optional<Book> optionalBook = bookRepo.findById((long) bookId);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            if (Objects.equals(book.getLoanStatus(), "Not loaned")) {
                book.setLoanStatus("Loaned");
            } else if (Objects.equals(book.getLoanStatus(), "Loaned")) {
                return ResponseEntity.badRequest().body("Book is already loaned");
            }
            loanRepo.save(addLoan);
            return ResponseEntity.ok("Loan added!");
        } else {
            return ResponseEntity.badRequest().body("Loan must be associated with an existing book id");
        }
    }

    @GetMapping("/get/loans")
    public List<Loan> getAllLoans() {
        return loanRepo.findAll();
    }

    @DeleteMapping("/delete/loan/{id}")
    public ResponseEntity<?> deleteLoanById(@PathVariable Long id) {
        Optional<Loan> optionalLoan = loanRepo.findById(id);
        if (optionalLoan.isPresent()) {
            Loan loan = optionalLoan.get();
            int bookId = loan.getBookId();
            Optional<Book> optionalBook = bookRepo.findById((long) bookId);
            if (optionalBook.isPresent()) {
                Book book = optionalBook.get();
                book.setLoanStatus("Not loaned");
            }
            loanRepo.deleteById(id);
            return ResponseEntity.ok("Loan deleted!");
        } else {
            return ResponseEntity.badRequest().body("Loan does not exist");
        }
    }

    @PutMapping("/put/loan/{id}")
    public ResponseEntity<?> updateLoan(@RequestBody Loan newLoan, @PathVariable Long id){
        Optional<Loan> optionalLoan = loanRepo.findById(id);
        if(optionalLoan.isPresent()) {
            Loan oldLoan = optionalLoan.get();
            int oldBookId = oldLoan.getBookId();
            int newBookId = newLoan.getBookId();
            if (oldBookId != newBookId) {
                Optional<Book> optionalOldBook = bookRepo.findById((long) oldBookId);
                if (optionalOldBook.isPresent()) {
                    Book oldBook = optionalOldBook.get();
                    oldBook.setLoanStatus("Not loaned");
                    bookRepo.save(oldBook);
                }
                Optional<Book> optionalNewBook = bookRepo.findById((long) newBookId);
                if (optionalNewBook.isEmpty()) {
                    return ResponseEntity.badRequest().body("Book with id " + newBookId + " not found!");
                }
                Book newBook = optionalNewBook.get();
                newBook.setLoanStatus("Loaned");
                bookRepo.save(newBook);
            }
            oldLoan.setLoanEndingDate(newLoan.getLoanEndingDate());
            oldLoan.setLoanStartingDate(newLoan.getLoanStartingDate());
            oldLoan.setBookId(newLoan.getBookId());
            loanRepo.save(oldLoan);
            return ResponseEntity.ok("Loan " + id + " updated!");
        }else{
            return ResponseEntity.badRequest().body("Loan with id " + id + " does not exist!");
        }
    }
}

