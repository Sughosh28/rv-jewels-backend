package com.rv.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class ReviewDTO implements Serializable {

    private Long id;
    private int rating;
    private String comment;
    private LocalDateTime reviewDate;
    private List<String> reviewImageUrls;
    private String userName;

    public ReviewDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDateTime reviewDate) { this.reviewDate = reviewDate; }

    public List<String> getReviewImageUrls() { return reviewImageUrls; }
    public void setReviewImageUrls(List<String> reviewImageUrls) { this.reviewImageUrls = reviewImageUrls; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}
