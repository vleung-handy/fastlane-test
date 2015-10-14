package com.handy.portal.ui.constructor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public abstract class ViewConstructor<T>
{
    private Context context;

    protected abstract int getLayoutResourceId();

    protected abstract boolean constructView(ViewGroup container, T item);

    public ViewConstructor(@NonNull Context context)
    {
        this.context = context;
    }

    public void create(ViewGroup container, T item)
    {
        View view = LayoutInflater.from(getContext()).inflate(getLayoutResourceId(), container, false);
        ButterKnife.inject(this, view);
        if (constructView(container, item))
        {
            container.addView(view);
        }
    }

    public Context getContext()
    {
        return context;
    }
}
