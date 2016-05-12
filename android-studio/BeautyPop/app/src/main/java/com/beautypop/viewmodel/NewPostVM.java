package com.beautypop.viewmodel;

import com.beautypop.app.AppController;
import com.beautypop.util.SelectedImage;
import com.beautypop.util.ViewUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

public class NewPostVM {
    public Long id;
    public Long catId;
    public String title, body;
    public double price;
    public String conditionType;
    public double originalPrice;
    public Boolean withPhotos;
    public List<File> images;
    public Boolean freeDelivery;
    public String countryCode;
    public String deviceType;
    public String image;
    public String mediaId;
    public NewPostVM(Long catId, String title, String body, double price,
                     ViewUtil.PostConditionType conditionType, List<SelectedImage> selectedPostImages,
                     double originalPrice, Boolean freeDelivery, String countryCode) {
        this(-1L, catId, title, body, price, conditionType, selectedPostImages, originalPrice, freeDelivery, countryCode);
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public NewPostVM(){}

    public NewPostVM(Long id, Long catId, String title, String body, double price,
                     ViewUtil.PostConditionType conditionType, List<SelectedImage> selectedImages,
                     double originalPrice, Boolean freeDelivery, String countryCode) {
        this.id = id;
        this.catId = catId;
        this.title = title;
        this.body = body;
        this.price = price;
        this.conditionType = conditionType.name();
        this.originalPrice = originalPrice;
        this.withPhotos = (selectedImages != null && selectedImages.size() > 0);
        this.images = new ArrayList<>();
        for (SelectedImage selectedImage : selectedImages) {
            this.images.add(selectedImage.getFile());
        }

        this.freeDelivery = freeDelivery;
        this.countryCode = countryCode;
        this.deviceType = AppController.DeviceType.ANDROID.name();
    }

    public MultipartTypedOutput toMultipart() {
        MultipartTypedOutput multipartTypedOutput = new MultipartTypedOutput();
        multipartTypedOutput.addPart("id", new TypedString(id+""));
        multipartTypedOutput.addPart("catId", new TypedString(catId+""));
        multipartTypedOutput.addPart("title", new TypedString(title));
        multipartTypedOutput.addPart("body", new TypedString(body));
        multipartTypedOutput.addPart("price", new TypedString(price+""));
        multipartTypedOutput.addPart("conditionType", new TypedString(conditionType));
        multipartTypedOutput.addPart("originalPrice", new TypedString(originalPrice+""));
        multipartTypedOutput.addPart("freeDelivery", new TypedString(freeDelivery.toString()));
        multipartTypedOutput.addPart("countryCode", new TypedString(countryCode.toString()));
        multipartTypedOutput.addPart("withPhotos", new TypedString(withPhotos.toString()));
        multipartTypedOutput.addPart("deviceType", new TypedString(deviceType.toString()));

        int i = 0;
        for (File image : images) {
            //image = ImageUtil.resizeAsJPG(image);
            TypedFile typedFile = new TypedFile("application/octet-stream", image);
            multipartTypedOutput.addPart("image"+i, typedFile);
            i++;
        }
        return multipartTypedOutput;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }
}
