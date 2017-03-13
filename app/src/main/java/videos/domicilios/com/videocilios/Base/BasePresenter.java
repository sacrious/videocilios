package videos.domicilios.com.videocilios.Base;

import android.util.Log;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import videos.domicilios.com.videocilios.Rest.ApiClient;
import videos.domicilios.com.videocilios.Rest.ApiInterface;

/**
 * Created by Sergio on 3/12/17.
 */

@SuppressWarnings("all")
public class BasePresenter<V> {
    public V view;
    protected ApiInterface api;
    private CompositeSubscription compositeSubscription;
    private Subscriber subscriber;

    public void attachView(V view) {
        this.view = view;
        api = ApiClient.getRetrofit().create(ApiInterface.class);
    }

    public void dettachView() {
        this.view = null;
        onUnsubscribe();
    }

    public void onUnsubscribe() {
        if (compositeSubscription != null && compositeSubscription.hasSubscriptions()) {
            compositeSubscription.unsubscribe();
            Log.e("TAG", "onUnsubscribe: ");
        }
    }

    protected void addSubscribe(Observable observable, Subscriber subscriber) {
        this.subscriber = subscriber;
        if (compositeSubscription == null) {
            compositeSubscription = new CompositeSubscription();
        }
        compositeSubscription.add(observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber));
    }

    public void stop() {
        if (subscriber != null) {
            subscriber.unsubscribe();
        }
    }
}
