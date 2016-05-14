package com.beautypop.viewmodel;

/**
 * Created by JAY GANESH on 4/25/2016.
 */
public class NewReviewVM {
	private Long conversationOrderId;
	private Double score;
	private String review;
	private Boolean forSeller;

	public Long getConversationOrderId() {
		return conversationOrderId;
	}

	public void setConversationOrderId(Long conversationOrderId) {
		this.conversationOrderId = conversationOrderId;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public Boolean getForSeller() {
		return forSeller;
	}

	public void setForSeller(Boolean forSeller) {
		this.forSeller = forSeller;
	}
}
