package sachin.dev.myrestaurant.Model;

import com.google.gson.annotations.SerializedName;

public class Restaurant {
    @SerializedName("ID")
    private int id;
    @SerializedName("Name")
    private String name;
    @SerializedName("Address")
    private String address;
    @SerializedName("Phone")
    private String phone;
    @SerializedName("Image")
    private String image;
    @SerializedName("PaymentUrl")
    private String paymentUrl;
    private float lat,lng;
    @SerializedName("UserOwner")
    private int userOwner;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public int getUserOwner() {
        return userOwner;
    }

    public void setUserOwner(int userOwner) {
        this.userOwner = userOwner;
    }
}
