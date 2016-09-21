package com.handy.portal.manager;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.RegionDefinitionEvent;
import com.handy.portal.library.util.IOUtils;
import com.handy.portal.model.definitions.FormDefinitionWrapper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

import javax.inject.Inject;

//manager for region specific form definitions, etc
public class RegionDefinitionsManager
{
    private final EventBus bus;

    private final Cache<String, FormDefinitionWrapper> formDefinitionCache;

    @Inject
    public RegionDefinitionsManager(final EventBus bus)
    {
        this.bus = bus;
        this.bus.register(this);
        formDefinitionCache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .build();
    }

    @Subscribe
    public void onRequestFormDefinitions(final RegionDefinitionEvent.RequestFormDefinitions event)
    {
        //TODO: make a network call instead?
        FormDefinitionWrapper formDefinitionWrapper = null;
        String region = event.region.toLowerCase();
        Context context = event.context;
        if (formDefinitionCache.getIfPresent(region) == null)
        {
            String path = "region/" + region + "/form_definitions.json"; //TODO: cleanup

            try
            {
                String fileContents = IOUtils.loadJSONFromAsset(context, path);
                try
                {
                    formDefinitionWrapper = (new Gson()).fromJson(fileContents, FormDefinitionWrapper.class);//TODO: add exception handling
                    formDefinitionCache.put(region, formDefinitionWrapper);
                }
                catch (JsonSyntaxException ex)
                {
                    Crashlytics.logException(ex);
                }

            }
            catch (IOException e)
            {
                Crashlytics.logException(e);
            }

        }
        else
        {
            formDefinitionWrapper = formDefinitionCache.getIfPresent(region);
        }

        if (formDefinitionWrapper == null)
        {
            //TODO: set proper error object
            bus.post(new RegionDefinitionEvent.ReceiveFormDefinitionsError(new DataManager.DataManagerError(DataManager.DataManagerError.Type.CLIENT)));

        }
        else
        {
            bus.post(new RegionDefinitionEvent.ReceiveFormDefinitionsSuccess(formDefinitionWrapper));

        }

    }

}
