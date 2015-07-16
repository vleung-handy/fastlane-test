package com.handy.portal.ui.element;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public abstract class ViewInitializer<T>
{
    private Context context;

    abstract int getLayoutResourceId();

    abstract boolean constructView(ViewGroup container, T item);

    public ViewInitializer(@NonNull Context context)
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
