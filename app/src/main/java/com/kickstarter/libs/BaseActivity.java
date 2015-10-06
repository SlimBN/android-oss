package com.kickstarter.libs;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.kickstarter.libs.qualifiers.RequiresPresenter;

import timber.log.Timber;

public class BaseActivity<PresenterType extends Presenter> extends AppCompatActivity {
  protected PresenterType presenter;
  private static final String PRESENTER_KEY = "presenter";

  /**
   * Get presenter.
   *
   * @deprecated TODO: Refactor parent/child presenters for activities and their views.
   */
  @Deprecated
  public PresenterType presenter() {
    return presenter;
  }

  @CallSuper
  @Override
  protected void onCreate(@Nullable final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Timber.d("onCreate %s", this.toString());

    fetchPresenter(savedInstanceState);
  }

  @CallSuper
  @Override
  protected void onStart() {
    super.onStart();
    Timber.d("onStart %s", this.toString());
  }

  @CallSuper
  @Override
  protected void onResume() {
    super.onResume();
    Timber.d("onResume %s", this.toString());

    fetchPresenter(null);
    if (presenter != null) {
      presenter.onResume(this);
    }
  }

  @CallSuper
  @Override
  protected void onPause() {
    super.onPause();
    Timber.d("onPause %s", this.toString());

    if (presenter != null) {
      presenter.onPause();
    }
  }

  @CallSuper
  @Override
  protected void onStop() {
    super.onStop();
    Timber.d("onStop %s", this.toString());
  }

  @CallSuper
  @Override
  protected void onDestroy() {
    super.onDestroy();
    Timber.d("onDestroy %s", this.toString());

    if (isFinishing()) {
      if (presenter != null) {
        Presenters.getInstance().destroy(presenter);
        presenter = null;
      }
    }
  }

  @CallSuper
  @Override
  protected void onSaveInstanceState(@NonNull final Bundle outState) {
    super.onSaveInstanceState(outState);
    Timber.d("onSaveInstanceState %s", this.toString());

    final Bundle presenterEnvelope = new Bundle();
    if (presenter != null) {
      Presenters.getInstance().save(presenter, presenterEnvelope);
    }

    outState.putBundle(PRESENTER_KEY, presenterEnvelope);
  }

  private void fetchPresenter(@Nullable final Bundle presenterEnvelope) {
    if (presenter == null) {
      final RequiresPresenter annotation = getClass().getAnnotation(RequiresPresenter.class);
      final Class<PresenterType> presenterClass = annotation == null ? null : (Class<PresenterType>) annotation.value();
      if (presenterClass != null) {
        presenter = Presenters.getInstance().fetch(this,
          presenterClass,
          BundleUtils.maybeGetBundle(presenterEnvelope, PRESENTER_KEY));
      }
    }
  }
}
