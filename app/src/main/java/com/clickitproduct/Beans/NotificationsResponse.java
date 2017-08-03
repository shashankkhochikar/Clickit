
package com.clickitproduct.Beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NotificationsResponse {

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("data")
    @Expose
    private List<NotificationsDatum> data = null;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<NotificationsDatum> getData() {
        return data;
    }

    public void setData(List<NotificationsDatum> data) {
        this.data = data;
    }

}
