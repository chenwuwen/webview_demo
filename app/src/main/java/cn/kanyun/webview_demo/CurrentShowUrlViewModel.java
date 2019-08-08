package cn.kanyun.webview_demo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel类
 */
public class CurrentShowUrlViewModel extends ViewModel {

    private MutableLiveData<String> showUrl;

    public LiveData<String> getShowUrl() {
        if (showUrl == null) {
            showUrl = new MutableLiveData<String>();
            showUrl.setValue("");
        }
        return showUrl;
    }
    /**
     * 更改Url
     *
     * @param newUrl
     */
    public void modifyShowUrl(String newUrl) {
        showUrl.setValue(newUrl);
    }

}
