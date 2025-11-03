# Library-Management-System
A backend Library Management System built with Spring Boot, Hibernate, and PostgreSQL, offering a REST API for managing books, loans, and reviews.

## Features
- Book management options (CRUD) 
- Loan tracking with start/end dates
- Review and rating system (0-10 scale)
- Automatic average rating calculation
- Book search by title
- Loan status tracking

## Technologies used
- Spring Boot 3.x
- PostgreSQL
- Spring Data JPA with Hibernate
- Maven

## API Endpoints

### Books
- `GET /get/books` - Get all books
- `GET /get/book/{id}` - Get book by ID  
- `GET /get/search/{bookName}` - Search book by title
- `POST /post/book` - Add new book
- `PUT /put/book/{id}` - Update book
- `DELETE /delete/book/{id}` - Delete book and all loans associated with it

### Loans
- `GET /get/loans` - Get all loans
- `POST /post/loan` - Create loan
- `PUT /put/loan/{id}` - Update loan
- `DELETE /delete/loan/{id}` - Delete loan and updates the book's loan status

### Reviews
- `GET /get/reviews` - Get all reviews
- `GET /get/review/{id}` - Get review by ID
- `POST /post/review` - Add review
- `PUT /put/review/{id}` - Update review
- `DELETE /delete/review/{id}` - Delete review and recalculates the book's rating
