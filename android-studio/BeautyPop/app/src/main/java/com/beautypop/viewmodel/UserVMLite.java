package com.beautypop.viewmodel;

import org.joda.time.DateTime;

import java.io.Serializable;

public class UserVMLite implements Serializable {
    public Long id;
    public String displayName;
    public String name;
    public String email;
    public String firstName;
    public String lastName;
    public Long numViews = 0L;
    public Long numLikes = 0L;
    public Long numFollowings = 0L;
    public Long numFollowers = 0L;
    public Long numProducts = 0L;
    public Long numComments = 0L;
    public Long numConversationsAsSender = 0L;
    public Long numConversationsAsRecipient = 0L;
    public Long numCollections = 0L;
    public Long numReviews = 0L;
    public Double averageReviewScore = 0.0;
    public boolean isFollowing = false;

    // for feed
    public long offset;

    // admin readyonly fields
    public Long createdDate;
    public Long lastLogin;
    public String lastLoginUserAgent;
    public Long totalLogin;
    public boolean isLoggedIn = false;
    public boolean isFbLogin = false;
    public boolean emailProvidedOnSignup = false;
    public boolean emailValidated = false;
    public boolean accountVerified = false;
    public boolean newUser = false;
    public boolean isAdmin = false;
    public boolean isPromotedSeller = false;
    public boolean isVerifiedSeller = false;
    public boolean isRecommendedSeller = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getNumFollowings() {
        return numFollowings;
    }

    public void setNumFollowings(Long numFollowings) {
        this.numFollowings = numFollowings;
    }

    public Long getNumFollowers() {
        return numFollowers;
    }

    public void setNumFollowers(Long numFollowers) {
        this.numFollowers = numFollowers;
    }

    public Long getNumProducts() {
        return numProducts;
    }

    public void setNumProducts(Long numProducts) {
        this.numProducts = numProducts;
    }

    public Long getNumViews() {
        return numViews;
    }

    public void setNumViews(Long numViews) {
        this.numViews = numViews;
    }

    public Long getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(Long numLikes) {
        this.numLikes = numLikes;
    }

    public Long getNumComments() {
        return numComments;
    }

    public void setNumComments(Long numComments) {
        this.numComments = numComments;
    }

    public Long getNumConversationsAsSender() {
        return numConversationsAsSender;
    }

    public void setNumConversationsAsSender(Long numConversationsAsSender) {
        this.numConversationsAsSender = numConversationsAsSender;
    }

    public Long getNumConversationsAsRecipient() {
        return numConversationsAsRecipient;
    }

    public void setNumConversationsAsRecipient(Long numConversationsAsRecipient) {
        this.numConversationsAsRecipient = numConversationsAsRecipient;
    }

    public Long getNumCollections() {
        return numCollections;
    }

    public void setNumCollections(Long numCollections) {
        this.numCollections = numCollections;
    }

    public Long getNumReviews() {
        return numReviews;
    }

    public void setNumReviews(Long numReviews) {
        this.numReviews = numReviews;
    }

    public Double getAverageReviewScore() {
        return averageReviewScore;
    }

    public void setAverageReviewScore(Double averageReviewScore) {
        this.averageReviewScore = averageReviewScore;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(boolean isFollowing) {
        this.isFollowing = isFollowing;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public Long getLastLogin() {
        return lastLogin;
    }

    public void setLastLoginUserAgent(String lastLoginUserAgent) {
        this.lastLoginUserAgent = lastLoginUserAgent;
    }

    public String getLastLoginUserAgent() {
        return lastLoginUserAgent;
    }

    public void setLastLogin(Long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Long getTotalLogin() {
        return totalLogin;
    }

    public void setTotalLogin(Long totalLogin) {
        this.totalLogin = totalLogin;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public boolean isFbLogin() {
        return isFbLogin;
    }

    public void setIsFbLogin(boolean isFbLogin) {
        this.isFbLogin = isFbLogin;
    }

    public boolean isEmailProvidedOnSignup() {
        return emailProvidedOnSignup;
    }

    public void setEmailProvidedOnSignup(boolean emailProvidedOnSignup) {
        this.emailProvidedOnSignup = emailProvidedOnSignup;
    }

    public boolean isEmailValidated() {
        return emailValidated;
    }

    public void setEmailValidated(boolean emailValidated) {
        this.emailValidated = emailValidated;
    }

    public boolean isAccountVerified() {
        return accountVerified;
    }

    public void setAccountVerified(boolean accountVerified) {
        this.accountVerified = accountVerified;
    }

    public boolean isNewUser() {
        return newUser;
    }

    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean isPromotedSeller() {
        return isPromotedSeller;
    }

    public void setIsPromotedSeller(boolean isPromotedSeller) {
        this.isPromotedSeller = isPromotedSeller;
    }

    public boolean isVerifiedSeller() {
        return isVerifiedSeller;
    }

    public void setIsVerifiedSeller(boolean isVerifiedSeller) {
        this.isVerifiedSeller = isVerifiedSeller;
    }

    public boolean isRecommendedSeller() {
        return isRecommendedSeller;
    }

    public void setIsRecommendedSeller(boolean isRecommendedSeller) {
        this.isRecommendedSeller = isRecommendedSeller;
    }

    @Override
    public String toString() {
        return "id=" + id + "\n" +
                "name=" + lastName + " " + firstName + "\n" +
                "email=" + email + "\n" +
                "emailValidated=" + emailValidated + "\n" +
                "accountVerified=" + accountVerified + "\n" +
                "fbLogin=" + isFbLogin + "\n" +
                "signupDate=" + new DateTime(createdDate) + "\n" +
                "lastLogin=" + new DateTime(lastLogin) + "\n" +
                "lastLoginUserAgent=" + lastLoginUserAgent + "\n" +
                "totalLogin=" + totalLogin + "\n" +
                "numViews=" + numViews + "\n" +
                "numLikes=" + numLikes + "\n" +
                "numFollowers=" + numFollowers + "\n" +
                "numFollowings=" + numFollowings + "\n" +
                "numProducts=" + numProducts + "\n" +
                "numComments=" + numComments + "\n" +
                "numConversationsAsSender=" + numConversationsAsSender + "\n" +
                "numConversationsAsRecipient=" + numConversationsAsRecipient;
    }
}
