package cn.kanyun.webview_demo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel类
 */
public class UrlViewModel extends ViewModel {

    private MutableLiveData<String> url;

    public LiveData<String> getUrl() {
        if (url == null) {
            url = new MutableLiveData<String>();
            url.setValue("");
        }
        return url;
    }

    /**
     * 更改Url
     *
     * @param newUrl
     */
    public void modifyUrl(String newUrl) {
        if (newUrl.startsWith("http://") || newUrl.startsWith("https://")) {
            url.setValue(newUrl);
        } else {
            url.setValue("http://" + newUrl);
        }
    }

}
