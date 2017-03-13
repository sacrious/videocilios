package videos.domicilios.com.videocilios.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import videos.domicilios.com.videocilios.Base.BaseFragment;
import videos.domicilios.com.videocilios.Base.BasePresenter;

/**
 * Created by Sergio on 3/12/17.
 */

public abstract class AppFragment <P extends BasePresenter> extends BaseFragment {
    protected P presenter;

    protected abstract P createPresenter();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        presenter = createPresenter();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.dettachView();
        }
    }
}
