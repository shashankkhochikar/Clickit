
package com.clickitproduct.Beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServiceDatum {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("expiry_date")
    @Expose
    private String expiryDate;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("aadharno")
    @Expose
    private long aadharno;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("serviceprovidername")
    @Expose
    private String serviceprovidername;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("last_updated")
    @Expose
    private String lastUpdated;
    @SerializedName("approval")
    @Expose
    private long approval;
    @SerializedName("register_date")
    @Expose
    private String registerDate;
    @SerializedName("service_time")
    @Expose
    private ServiceTime serviceTime;
    @SerializedName("images")
    @Expose
    private ServiceImages images;
    @SerializedName("address")
    @Expose
    private ServiceAddress address;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public long getAadharno() {
        return aadharno;
    }

    public void setAadharno(long aadharno) {
        this.aadharno = aadharno;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getServiceprovidername() {
        return serviceprovidername;
    }

    public void setServiceprovidername(String serviceprovidername) {
        this.serviceprovidername = serviceprovidername;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public long getApproval() {
        return approval;
    }

    public void setApproval(long approval) {
        this.approval = approval;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public ServiceTime getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(ServiceTime serviceTime) {
        this.serviceTime = serviceTime;
    }

    public ServiceImages getImages() {
        return images;
    }

    public void setImages(ServiceImages images) {
        this.images = images;
    }

    public ServiceAddress getAddress() {
        return address;
    }

    public void setAddress(ServiceAddress address) {
        this.address = address;
    }

}
