package videos.domicilios.com.videocilios.Interfaces;

/**
 * Created by Sergio on 3/12/17.
 */

public interface IObserver {
    void showLoading();

    void hideLoading();

    void getDataSuccess(Object model);

    void getDataFail(String message);
}
