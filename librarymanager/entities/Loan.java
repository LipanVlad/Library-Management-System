package librarymanager.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int bookId;
    private String loanStartingDate;
    public String loanEndingDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getLoanStartingDate() {
        return loanStartingDate;
    }

    public void setLoanStartingDate(String loanStartingDate) {
        this.loanStartingDate = loanStartingDate;
    }

    public String getLoanEndingDate() {
        return loanEndingDate;
    }

    public void setLoanEndingDate(String loanEndingDate) {
        this.loanEndingDate = loanEndingDate;
    }
}
