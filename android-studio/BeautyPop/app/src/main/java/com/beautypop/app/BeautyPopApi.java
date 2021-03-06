package com.beautypop.app;

import com.beautypop.viewmodel.ActivityVM;
import com.beautypop.viewmodel.AdminCommentVM;
import com.beautypop.viewmodel.AdminConversationVM;
import com.beautypop.viewmodel.CategoryVM;
import com.beautypop.viewmodel.CollectionVM;
import com.beautypop.viewmodel.CommentVM;
import com.beautypop.viewmodel.ConversationOrderVM;
import com.beautypop.viewmodel.ConversationVM;
import com.beautypop.viewmodel.CountryVM;
import com.beautypop.viewmodel.FeaturedItemVM;
import com.beautypop.viewmodel.ReviewVM;
import com.beautypop.viewmodel.GameBadgeVM;
import com.beautypop.viewmodel.LocationVM;
import com.beautypop.viewmodel.MessageVM;
import com.beautypop.viewmodel.NewCommentVM;
import com.beautypop.viewmodel.NewConversationOrderVM;
import com.beautypop.viewmodel.NewReportedPostVM;
import com.beautypop.viewmodel.NotificationCounterVM;
import com.beautypop.viewmodel.NewReviewVM;
import com.beautypop.viewmodel.PostVM;
import com.beautypop.viewmodel.PostVMLite;
import com.beautypop.viewmodel.ResponseStatusVM;
import com.beautypop.viewmodel.EditUserInfoVM;
import com.beautypop.viewmodel.SellerVM;
import com.beautypop.viewmodel.SettingsVM;
import com.beautypop.viewmodel.UserVM;
import com.beautypop.viewmodel.UserVMLite;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedFile;

public interface BeautyPopApi {

    @POST("/login/mobile")
    public void loginByEmail(@Query("email") String email, @Query("password") String password, Callback<Response> cb);

    @POST("/authenticate/mobile/facebook")
    public void loginByFacebook(@Query("access_token") String access_token, Callback<Response> cb);

    @POST("/signup")
    public void signUp(@Query("lname") String lname,
                       @Query("fname") String fname,
                       @Query("email") String email,
                       @Query("password") String password,
                       @Query("repeatPassword") String repeatPassword,
                       Callback<Response> cb);

    @FormUrlEncoded
    @POST("/saveSignupInfo")
    public void signUpInfo(@Field("parent_displayname") String parent_displayname,
                           @Field("parent_location") Integer parent_location,
                           @Query("key") String key,
                           Callback<Response> cb);

    @GET("/api/get-districts")
    public void getDistricts(@Query("key") String key, Callback<List<LocationVM>> cb);

    @GET("/api/get-countries")
    public void getCountries(@Query("key") String key, Callback<List<CountryVM>> cb);

    @GET("/api/init-new-user")
    public void initNewUser(@Query("key") String key, Callback<UserVM> cb);

    @GET("/api/get-user-info")
    public void getUserInfo(@Query("key") String key, Callback<UserVM> cb);

    @GET("/api/get-user/{id}")
    public void getUser(@Path("id") Long id, @Query("key") String key, Callback<UserVM> cb);

    @GET("/api/get-featured-items/{itemType}")
    public void getFeaturedItems(@Path("itemType") String itemType, @Query("key") String key,Callback<List<FeaturedItemVM>> cb);

    @POST("/api/report-post")
    public void reportPost(@Body NewReportedPostVM newReportedPostVM, @Query("key") String key, Callback<Response> cb);

    //
    // Home feeds
    //

    @GET("/api/get-home-explore-feed/{offset}")
    public void getHomeExploreFeed(@Path("offset") Long offset, @Query("key") String key, Callback<List<PostVMLite>> callback);

    @GET("/api/get-home-following-feed/{offset}")
    public void getHomeFollowingFeed(@Path("offset") Long offset, @Query("key") String key, Callback<List<PostVMLite>> callback);

    //
    // Category feeds
    //

    @GET("/api/get-category-popular-feed/{id}/{conditionType}/{offset}")
    public void getCategoryPopularFeed(@Path("offset") Long offset, @Path("id") Long id, @Path("conditionType") String conditionType, @Query("key") String key, Callback<List<PostVMLite>> callback);

    @GET("/api/get-category-newest-feed/{id}/{conditionType}/{offset}")
    public void getCategoryNewestFeed(@Path("offset") Long offset, @Path("id") Long id, @Path("conditionType") String conditionType, @Query("key") String key, Callback<List<PostVMLite>> callback);

    @GET("/api/get-category-price-low-high-feed/{id}/{conditionType}/{offset}")
    public void getCategoryPriceLowHighFeed(@Path("offset") Long offset, @Path("id") Long id, @Path("conditionType") String conditionType, @Query("key") String key, Callback<List<PostVMLite>> callback);

    @GET("/api/get-category-price-high-low-feed/{id}/{conditionType}/{offset}")
    public void getCategoryPriceHighLowFeed(@Path("offset") Long offset, @Path("id") Long id, @Path("conditionType") String conditionType, @Query("key") String key, Callback<List<PostVMLite>> callback);

    //
    // User feeds
    //

    @GET("/api/get-user-posted-feed/{id}/{offset}")
    public void getUserPostedFeed(@Path("offset") Long offset, @Path("id") Long id, @Query("key") String key, Callback<List<PostVMLite>> callback);

    @GET("/api/get-user-liked-feed/{id}/{offset}")
    public void getUserLikedFeed(@Path("offset") Long offset, @Path("id") Long id, @Query("key") String key, Callback<List<PostVMLite>> callback);

    @GET("/api/get-followings/{id}/{offset}")
    public void getFollowings(@Path("offset") Long offset, @Path("id") Long id, @Query("key") String key, Callback<List<UserVMLite>> cb);

    @GET("/api/get-followers/{id}/{offset}")
    public void getFollowers(@Path("offset") Long offset, @Path("id") Long id, @Query("key") String key, Callback<List<UserVMLite>> cb);

    //
    // Other feeds
    //

    @GET("/api/get-recommended-sellers")
    public void getRecommendedSellers(@Query("key") String key, Callback<List<UserVMLite>> callback);

    @GET("/api/get-recommended-sellers-feed/{offset}")
    public void getRecommendedSellersFeed(@Path("offset") Long offset, @Query("key") String key, Callback<List<SellerVM>> callback);

    @GET("/api/get-suggested-products/{id}")
    public void getSuggestedProducts(@Path("id") Long id, @Query("key") String key, Callback<List<PostVMLite>> callback);

    //
    // Category + post + comments
    //

    @GET("/api/get-all-categories")
    public void getAllCategories(@Query("key") String key, Callback<List<CategoryVM>> cb);

    @GET("/api/get-category/{id}")
    public void getCategory(@Path("id") Long id, @Query("key") String key, Callback<CategoryVM> cb);

    @GET("/api/get-post/{id}")
    public void getPost(@Path("id") Long id, @Query("key") String key, Callback<PostVM> callback);

    @POST("/api/post/new")
    public void newPost(@Body MultipartTypedOutput attachments, /*@Body NewPostVM newPost,*/ @Query("key") String key, Callback<ResponseStatusVM> cb);

    @POST("/api/post/edit")
    public void editPost(@Body MultipartTypedOutput attachments, /*@Body NewPostVM newPost,*/ @Query("key") String key, Callback<ResponseStatusVM> cb);

    @GET("/api/post/delete/{id}")
    public void deletePost(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/api/get-comments/{postId}/{offset}")
    public void getComments(@Path("postId") Long postId, @Path("offset") Long offset, @Query("key") String key, Callback<List<CommentVM>> cb);

    @POST("/api/comment/new")
    public void newComment(@Body NewCommentVM newComment, @Query("key") String key, Callback<ResponseStatusVM> cb);

    @GET("/api/comment/delete/{id}")
    public void deleteComment(@Path("id") Long comment_id, @Query("key") String key, Callback<Response> cb);

    @GET("/api/like-post/{id}")
    public void likePost(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/api/unlike-post/{id}")
    public void unlikePost(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/api/sold-post/{id}")
    public void soldPost(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/api/follow-user/{id}")
    public void followUser(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/api/unfollow-user/{id}")
    public void unfollowUser(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    //
    // Profile
    //

    @Multipart
    @POST("/image/upload-cover-photo")
    public void uploadCoverPhoto(@Part("profile-photo") TypedFile photo, @Query("key") String key, Callback<Response> cb);

    @Multipart
    @POST("/image/upload-profile-photo")
    public void uploadProfilePhoto(@Part("profile-photo") TypedFile photo, @Query("key") String key, Callback<Response> cb);

    @POST("/api/user-info/edit")
    public void editUserInfo(@Body EditUserInfoVM userInfoVM, @Query("key") String key, Callback<UserVM> cb);

    @POST("/api/user-notification-settings/edit")
    public void editUserNotificationSettings(@Body SettingsVM settingsVM, @Query("key") String key, Callback<UserVM> cb);

    @GET("/api/get-user-collections/{userId}")
    public void getUserCollections(@Path("userId") Long userId, @Query("key") String key, Callback<List<CollectionVM>> cb);

    @GET("/api/get-collection/{id}")
    public void getCollection(@Path("id") Long id, @Query("key") String key, Callback<CollectionVM> cb);

    @GET("/api/notification-counter")
    public void getNotificationCounter(@Query("key") String key, Callback<NotificationCounterVM> cb);

    @GET("/api/get-activities/{offset}")
    public void getActivities(@Path("offset") Long offset, @Query("key") String key, Callback<List<ActivityVM>> cb);

    //
    // Game badges
    //

    @GET("/api/get-game-badges/{id}")
    public void getGameBadges(@Path("id") Long id, @Query("key") String key, Callback<List<GameBadgeVM>> cb);

    @GET("/api/get-game-badges-awarded/{id}")
    public void getGameBadgesAwarded(@Path("id") Long id, @Query("key") String key, Callback<List<GameBadgeVM>> cb);

    //
    // Conversation
    //

    @GET("/api/get-user-conversations/{offset}")
    public void getConversations(@Path("offset") Long offset, @Query("key") String key, Callback<List<ConversationVM>> cb);

    @GET("/api/get-post-conversations/{id}")
    public void getPostConversations(@Path("id") Long id, @Query("key") String key, Callback<List<ConversationVM>> cb);

    @GET("/api/get-conversation/{id}")
    public void getConversation(@Path("id") Long id, @Query("key") String key, Callback<ConversationVM> cb);

    @GET("/api/open-conversation/{postId}")
    public void openConversation(@Path("postId") Long postId, @Query("key") String key, Callback<ConversationVM> cb);

    @GET("/api/delete-conversation/{id}")
    public void deleteConversation(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/api/get-messages/{conversationId}/{offset}")
    public void getMessages(@Path("conversationId") Long conversationId, @Path("offset") Long offset, @Query("key") String key, Callback<Response> cb);

    @POST("/api/message/new")
    public void newMessage(@Body MultipartTypedOutput attachments, /*@Body NewMessageVM message,*/ @Query("key") String key, Callback<MessageVM> cb);

    @GET("/image/get-message-image/{id}")
    public void getMessageImage(@Query("key") String key, @Path("id") Long id, Callback<MessageVM> cb);

    @Multipart
    @POST("/image/upload-message-photo")
    public void uploadMessagePhoto(@Query("key") String key, @Part("messageId") Long id, @Part("send-photo0") TypedFile photo, Callback<Response> cb);

    @POST("/api/update-conversation-note")
    public void updateConversationNote(@Body MultipartTypedOutput attachments, /*@Body NewMessageVM message,*/ @Query("key") String key, Callback<Response> cb);

    @GET("/api/update-conversation-order-transaction-state/{id}/{state}")
    public void updateConversationOrderTransactionState(@Path("id") Long id, @Path("state") String state, @Query("key") String key, Callback<Response> cb);

    @GET("/api/highlight-conversation/{id}/{color}")
    public void highlightConversation(@Path("id") Long id, @Path("color") String color, @Query("key") String key, Callback<Response> cb);

    //
    // Conversation Order
    //

    @POST("/api/conversation-order/new")
    public void newConversationOrder(@Body NewConversationOrderVM newConversationOrder, @Query("key") String key, Callback<ConversationOrderVM> cb);

    @GET("/api/conversation-order/new/{conversationId}")
    public void newConversationOrder(@Path("conversationId") Long conversationId, @Query("key") String key, Callback<ConversationOrderVM> cb);

    @GET("/api/conversation-order/cancel/{id}")
    public void cancelConversationOrder(@Path("id") Long id, @Query("key") String key, Callback<ConversationOrderVM> cb);

    @GET("/api/conversation-order/accept/{id}")
    public void acceptConversationOrder(@Path("id") Long id, @Query("key") String key, Callback<ConversationOrderVM> cb);

    @GET("/api/conversation-order/decline/{id}")
    public void declineConversationOrder(@Path("id") Long id, @Query("key") String key, Callback<ConversationOrderVM> cb);

    //
    // GCM
    //

    @POST("/api/save-gcm-key/{gcmKey}/{versionCode}")
    public void saveGCMKey(@Path("gcmKey") String gcmKey, @Path("versionCode") Long versionCode, @Query("key") String key, Callback<Response> cb);

    //
    // Admin
    //

    @GET("/api/delete-account/{id}")
    public void deleteAccount(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/api/adjust-up-post-score/{id}/{points}")
    public void adjustUpPostScore(@Path("id") Long id, @Path("points") Long points, @Query("key") String key, Callback<Response> cb);

    @GET("/api/adjust-down-post-score/{id}/{points}")
    public void adjustDownPostScore(@Path("id") Long id, @Path("points") Long points, @Query("key") String key, Callback<Response> cb);

    @GET("/api/reset-adjust-post-score/{id}")
    public void resetAdjustPostScore(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/api/get-users-by-signup/{offset}")
    public void getUsersBySignup(@Path("offset") Long offset, @Query("key") String key, Callback<List<UserVMLite>> cb);

    @GET("/api/get-users-by-login/{offset}")
    public void getUsersByLogin(@Path("offset") Long offset, @Query("key") String key, Callback<List<UserVMLite>> cb);

    @GET("/api/get-latest-comments/{offset}")
    public void getLatestComments(@Path("offset") Long offset, @Query("key") String key, Callback<List<AdminCommentVM>> cb);

    @GET("/api/get-latest-conversations/{offset}")
    public void getLatestConversations(@Path("offset") Long offset, @Query("key") String key, Callback<List<AdminConversationVM>> cb);

    @GET("/api/get-messages-for-admin/{conversationId}/{offset}")
    public void getMessagesForAdmin(@Path("conversationId") Long conversationId, @Path("offset") Long offset, @Query("key") String key, Callback<Response> cb);

    @GET("/api/get-latest-products/{offset}")
    public void getNewProducts(@Path("offset") Long offset, @Query("key") String key, Callback<List<PostVMLite>> cb);

    @GET("/api/set-product-theme/{id}/{themeId}")
    public void setProductTheme(@Path("id") Long id, @Path("themeId") Long themeId, @Query("key") String key, Callback<Response> cb);

    @GET("/api/set-product-trend/{id}/{trendId}")
    public void setProductTrend(@Path("id") Long id, @Path("trendId") Long trendId, @Query("key") String key, Callback<Response> cb);

    // Search API
    @GET("/search-posts/{searchKey}/{catId}/{offset}")
    public void searchProducts(@Path("searchKey") String searchKey, @Path("catId") Long id, @Path("offset") Long offset, @Query("key") String key, Callback<List<PostVMLite>> callback);

    @GET("/search-users/{searchKey}/{offset}")
	public void searchUsers(@Path("searchKey") String searchKey, @Path("offset") int offset, @Query("key") String key, Callback<List<SellerVM>> callback);

	// Review API
	@POST("/api/review/add")
	public void addReview(@Body NewReviewVM newReviewVM, @Query("key") String key, Callback<Response> cb);

	@GET("/api/get-buyer-reviews-for/{userId}")
	public void getBuyerReviewsFor(@Path("userId") Long id, @Query("key") String key, Callback<List<ReviewVM>> cb);

	@GET("/api/get-seller-reviews-for/{userId}")
	public void getSellerReviewsFor(@Path("userId") Long id, @Query("key") String key, Callback<List<ReviewVM>> cb);

	@GET("/api/review/{conversationOrderId}")
	public void getReview(@Path("conversationOrderId") Long id, @Query("key") String key,Callback<ReviewVM> cb);
}