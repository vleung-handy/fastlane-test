package com.handy.portal.ui.element;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public abstract class ViewConstructor<T>
{
    private Context context;

    abstract int getLayoutResourceId();

    abstract boolean constructView(ViewGroup container, T item);

    public void create(Context context, ViewGroup container, T item)
    {
        this.context = context;
        View view = LayoutInflater.from(context).inflate(getLayoutResourceId(), container, false);
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
