package librarymanager.controllers;

import librarymanager.entities.Book;
import librarymanager.entities.Review;
import librarymanager.repositories.BookRepository;
import librarymanager.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
public class ReviewController {

    @Autowired
    ReviewRepository reviewRepo;
    @Autowired
    BookRepository bookRepo;
    @Autowired
    librarymanager.controllers.BookController bookController;


    public ResponseEntity<?> reviewValidation(Review review){
        if (review.getReviewDate() == null) {
            return ResponseEntity.badRequest().body("Review MUST have a review date!");
        }
        if (review.getReviewText() == null) {
            return ResponseEntity.badRequest().body("Review MUST have a review text!");
        }
        if(review.getBookId()==0){
            return ResponseEntity.badRequest().body("Review MUST have a book id!");
        }
        if(review.getRating() == null){
            return ResponseEntity.badRequest().body("Review MUST have a rating!");
        }
        if(review.getRating() > 10 || review.getRating() < 0){
            return ResponseEntity.badRequest().body("Review MUST have a valid rating! (0-10)");
        }
        return ResponseEntity.ok(review);
    }


    @PostMapping("/post/review")
    public ResponseEntity<?> addReview(@RequestBody Review addReview) {
        ResponseEntity<?> validation = reviewValidation(addReview);
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }
        int bookId = addReview.getBookId();
        Optional<Book> optionalBook = bookRepo.findById((long) bookId);
        if (optionalBook.isPresent()) {
            reviewRepo.save(addReview);
            return ResponseEntity.ok("Review added!");
        } else {
            return ResponseEntity.badRequest().body("Review must be associated with an existing book id");
        }
    }

    @GetMapping("/get/reviews")
    public List<Review> getAllReviews() {
        return reviewRepo.findAll();
    }

    @GetMapping("/get/review/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable Long id) {
        Optional<Review> optionalReview = reviewRepo.findById(id);
        if(optionalReview.isPresent()){
            Review review = optionalReview.get();
            return ResponseEntity.ok(review);
        }else{
            return ResponseEntity.badRequest().body("Id " + id + " does not exist!");
        }
    }

    @DeleteMapping("/delete/review/{id}")
    public ResponseEntity<?> deleteReviewById(@PathVariable Long id){
        Optional<Review> optionalReview = reviewRepo.findById(id);
        if(optionalReview.isEmpty()) {
            return ResponseEntity.badRequest().body("Review with id " + id + " does not exist!");
        }
        Review review = optionalReview.get();
        int reviewBookId = review.getBookId();
        reviewRepo.deleteById(id);

        Optional<Book> optionalBook = bookRepo.findById((long) reviewBookId);

            Book book = optionalBook.get();
            bookController.getRating(book);
            bookRepo.save(book);

        return ResponseEntity.ok("Review " + id + " deleted!");
    }

    @PutMapping("/put/review/{id}")
    public ResponseEntity<?> updateReview(@RequestBody Review newReview, @PathVariable Long id){
        Optional<Review> optionalReview = reviewRepo.findById(id);
        if(optionalReview.isPresent()) {
            Review oldReview = optionalReview.get();
            oldReview.setReviewDate(newReview.getReviewDate());
            oldReview.setReviewText(newReview.getReviewText());
            oldReview.setBookId(newReview.getBookId());
            oldReview.setRating(newReview.getRating());

            reviewRepo.save(oldReview);
            return ResponseEntity.ok("Review " + id + " updated!");
        }else{
            return ResponseEntity.badRequest().body("Review with id " + id + " does not exist!");
        }
    }
}