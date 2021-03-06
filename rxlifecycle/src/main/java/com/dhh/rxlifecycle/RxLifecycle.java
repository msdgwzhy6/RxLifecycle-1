package com.dhh.rxlifecycle;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.lang.reflect.Field;

/**
 * Created by dhh on 2017/9/25.
 */

public class RxLifecycle {
    private static final String FRAGMENT_TAG = "lifecycle_tag";

    /**
     * @param context ensure context can be cast {@link Activity}
     */
    public static void injectRxLifecycle(Context context) {
        with(context);
    }

    public static LifecycleManager with(Activity activity) {
        if (activity instanceof FragmentActivity) {
            return with((FragmentActivity) activity);
        }
        FragmentManager fm = activity.getFragmentManager();
        Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new LifecycleFragment();
            fm.beginTransaction().add(fragment, FRAGMENT_TAG).commitAllowingStateLoss();
            fm.executePendingTransactions();
        }
        return (LifecycleManager) fragment;
    }

    private static LifecycleManager with(FragmentActivity activity) {
        android.support.v4.app.FragmentManager fm = activity.getSupportFragmentManager();
        android.support.v4.app.Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new LifecycleV4Fragment();
            fm.beginTransaction().add(fragment, FRAGMENT_TAG).commitNowAllowingStateLoss();
        }

        return (LifecycleManager) fragment;
    }

    public static LifecycleManager with(Fragment fragment) {
        return with(fragment.getActivity());
    }

    public static LifecycleManager with(android.support.v4.app.Fragment fragment) {
        return with(fragment.getActivity());
    }

    /**
     * @param context ensure context can be cast {@link Activity}
     */
    public static LifecycleManager with(Context context) {
        if (context instanceof AppCompatActivity) {
            return with((FragmentActivity) context);
        }
        if (context instanceof Activity) {
            return with((Activity) context);
        }
        throw new ClassCastException(context.getClass().getSimpleName() + " can\'t cast Activity !");
    }

    public static LifecycleManager with(View view) {
        return with(view.getContext());
    }


    private static void injectRxLifecycle(Object object) {
        if (object instanceof View) {
            with((View) object);
        } else {
            with(object);
        }
    }

    private static LifecycleManager with(Object object) {
        if (object instanceof Context) {
            return with((Context) object);
        }
        for (Field field : object.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(object);
                if (value instanceof Context) {
                    return with((Context) value);
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        throw new ClassCastException(object.getClass().getSimpleName() + " can\'t convert Context !");
    }

}
