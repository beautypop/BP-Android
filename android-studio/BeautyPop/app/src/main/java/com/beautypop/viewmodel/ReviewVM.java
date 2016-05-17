package com.beautypop.viewmodel;

/**
 * Created by JAY GANESH on 2/22/2016.
 */
public class ReviewVM {

	private Long id;
	private Long userId;
	private String userName;
	private Long postId;
    private Long postImageId;
	private String review;
	private Double score;
	public Long reviewDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

    public Long getPostImageId() {
        return postImageId;
    }

    public void setPostImageId(Long postImageId) {
        this.postImageId = postImageId;
    }

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

    public Long getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Long reviewDate) {
        this.reviewDate = reviewDate;
    }
}
