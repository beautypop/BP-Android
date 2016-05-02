package com.beautypop.viewmodel;

public class UserVM extends UserVMLite {
    public String aboutMe;
    public LocationVM location;
    public SettingsVM settings;
	public Double averageReviewScore;
	public Integer numReviews;


	public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public LocationVM getLocation() {
        return location;
    }

    public void setLocation(LocationVM location) {
        this.location = location;
    }

    public SettingsVM getSettings() {
        return settings;
    }

    public void setSettings(SettingsVM settings) {
        this.settings = settings;
    }

	public Double getAverageReviewScore() {
		return averageReviewScore;
	}

	public void setAverageReviewScore(Double averageReviewScore) {
		this.averageReviewScore = averageReviewScore;
	}

	public Integer getNumReviews() {
		return numReviews;
	}

	public void setNumReviews(Integer numReviews) {
		this.numReviews = numReviews;
	}
}

