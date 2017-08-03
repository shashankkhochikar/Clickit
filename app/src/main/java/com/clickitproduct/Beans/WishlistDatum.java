
package com.clickitproduct.Beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WishlistDatum 
{

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("profile_pic")
    @Expose
    private String profilePic;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("last_updated")
    @Expose
    private String lastUpdated;
    @SerializedName("products")
    @Expose
    private List<WishlistProduct> products = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public List<WishlistProduct> getProducts() {
        return products;
    }

    public void setProducts(List<WishlistProduct> products) {
        this.products = products;
    }

}
