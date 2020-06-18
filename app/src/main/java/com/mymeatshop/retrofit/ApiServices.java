package com.mymeatshop.retrofit;


import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface ApiServices {
    @POST("login.php")
    Call<JsonObject> getLogin(@Body JsonObject object, @Header("androidId") String AndroidId);

    @POST("user_register.php")
    Call<JsonObject> getSignUp(@Body JsonObject object, @Header("androidId") String AndroidId);

    @POST("total_order.php")
    Call<JsonObject> getOrderHistory(@Body JsonObject object, @Header("androidId") String AndroidId);

    @POST("changePassword.php")
    Call<JsonObject> getChangePassword(@Body JsonObject object, @Header("androidId") String AndroidId);

    @POST("cartitem.php")
    Call<JsonObject> getCartItem(@Body JsonObject object, @Header("androidId") String AndroidId);

    @POST("editcart.php")
    Call<JsonObject> getCartUpdate(@Body JsonObject object, @Header("androidId") String AndroidId);

    @POST("deletecartitem.php")
    Call<JsonObject> getDeleteCartItem(@Body JsonObject object, @Header("androidId") String AndroidId);

    @GET
    Call<JsonObject> getPopularCategories(@Url String url, @Header("androidId") String AndroidId);

    @POST("best_seller.php")
    Call<JsonObject> getBestSellerAndDeal(@Body JsonObject object,@Header("androidId") String AndroidId);

    @POST("update_profile.php")
    Call<JsonObject> getChangePersonalDetails(@Body JsonObject object, @Header("androidId") String AndroidId);

    @POST("productgetbycate_id.php")
    Call<JsonObject> getProductListByCategory(@Body JsonObject object, @Header("androidId") String AndroidId);

    @POST("productbyid.php")
    Call<JsonObject> getProductDetail(@Body JsonObject object, @Header("androidId") String AndroidId);

    @POST("addtocart.php")
    Call<JsonObject> getAddtoCart(@Body JsonObject object, @Header("androidId") String AndroidId);

    @POST("shipping-information.php")
    Call<JsonObject> getAddShippingInformation(@Body JsonObject object, @Header("androidId") String AndroidId);

    @POST("get_billing.php")
    Call<JsonObject> getAddressList(@Body JsonObject object, @Header("androidId") String AndroidId);

    @POST("updateadd.php")
    Call<JsonObject> getUpdateAddress(@Body JsonObject object, @Header("androidId") String AndroidId);

    @POST("deleteadd.php")
    Call<JsonObject> getDeleteAddress(@Body JsonObject object, @Header("androidId") String AndroidId);

    @POST("order.php")
    Call<JsonObject> getOrderPlace(@Body JsonObject object, @Header("androidId") String AndroidId);

    @POST("addpaymentResponse.php")
    Call<JsonObject> getaddpaymentResponse(@Body JsonObject object, @Header("androidId") String AndroidId);

    @POST("addwishlist.php")
    Call<JsonObject> getAddProductToWishlist(@Body JsonObject object, @Header("androidId") String AndroidId);

    @POST("get_wishlist.php")
    Call<JsonObject> getWishList(@Body JsonObject object, @Header("androidId") String AndroidId);

    @POST("deletewishlist.php")
    Call<JsonObject> getDeleteWishlistItem(@Body JsonObject object, @Header("androidId") String AndroidId);

    @POST("reset_password.php")
    Call<JsonObject> getResetPassword(@Body JsonObject object, @Header("androidId") String AndroidId);

    @POST("change_password.php")
    Call<JsonObject> getForgotPassword(@Body JsonObject object, @Header("androidId") String AndroidId);

    @POST("guest_cartitem.php")
    Call<JsonObject> getGuestCartItem(@Body JsonObject object);

    @POST("search.php")
    Call<JsonObject> getSearchItem(@Body JsonObject object);

    @POST("mail.php")
    Call<String> postEnquiry(@Body JsonObject object);

    @POST("coupan.php")
    Call<JsonObject> postCoupon(@Body JsonObject object);

    @POST("live_data.php")
    Call<JsonObject> postCurrentOrder(@Body JsonObject object);

    @POST("customer_location.php")
    Call<JsonObject> postTrackOrder(@Body JsonObject object);

    @POST("delivery_rating.php")
    Call<JsonObject> postDeliveryRating(@Body JsonObject object);

    @POST("live_location.php")
    Call<JsonObject> postLiveLocation(@Body JsonObject object);
    @POST("orderdetail_id.php")
    Call<JsonObject> getOrderDetails(@Body JsonObject object);

    @POST("slogin.php")
    Call<JsonObject> socialLogin(@Body JsonObject object);

    @POST("sociallogin.php")
    Call<JsonObject> socialRegister(@Body JsonObject object);

    @POST("guest_cartupdate.php")
    Call<JsonObject> getGuestDeleteItem(@Body JsonObject object);

    @POST("guest-carts/{token}/items")
    Call<JsonObject> getGuestAddToCart(@Body JsonObject object, @Path("token") String token);

    @POST("guest-carts/{token}/items")
    Call<JsonObject> getGuestEditCart(@Body JsonObject object, @Path("token") String token);

    @POST("guest-carts/{token}/shipping-information")
    Call<JsonObject> getGuestShipAddress(@Body JsonObject object, @Path("token") String token);

    @PUT("guest-carts/{token}/order")
    Call<String> getGuestOrderPlace(@Body JsonObject object, @Path("token") String token);

    @POST("guest-cart.php")
    Call<JsonObject> getGuestToken();

    @GET("contactus.php")
    Call<JsonObject> getContactUs();

    @GET("pincode.php")
    Call<JsonObject> getPincodes();

}
