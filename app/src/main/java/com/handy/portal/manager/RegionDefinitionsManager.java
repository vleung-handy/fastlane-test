package com.handy.portal.manager;

import android.content.Context;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.RegionDefinitionEvent;
import com.handy.portal.model.definitions.FormDefinitionWrapper;
import com.handy.portal.util.IOUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

//manager for region specific form definitions, etc
public class RegionDefinitionsManager //TODO: rename
{
    private final Bus bus;
    private final DataManager dataManager;

    private final Cache<String, FormDefinitionWrapper> formDefinitionCache;

    @Inject
    public RegionDefinitionsManager(final Bus bus, final DataManager dataManager)
    {
        this.bus = bus;
        this.bus.register(this);
        this.dataManager = dataManager;
        formDefinitionCache = CacheBuilder.newBuilder()
                .maximumSize(100)
//                .expireAfterWrite(1, TimeUnit.HOURS)
                .build();
    }

    @Subscribe
    public void onRequestFormDefinitions(final RegionDefinitionEvent.RequestFormDefinitions event)
    {
        //TODO: make a network call instead?
        FormDefinitionWrapper formDefinitionWrapper;
        String region = event.region.toLowerCase();
        Context context = event.context;
        if(formDefinitionCache.getIfPresent(region)==null)
        {
            String path = "region/" + region + "/form_definitions.json"; //TODO: cleanup
            String fileContents = IOUtils.loadJSONFromAsset(context, path);
            Gson gson = new Gson();
            formDefinitionWrapper = gson.fromJson(fileContents, FormDefinitionWrapper.class);
            formDefinitionCache.put(region, formDefinitionWrapper);
        }
        else
        {
            formDefinitionWrapper = formDefinitionCache.getIfPresent(region);
        }

        bus.post(new RegionDefinitionEvent.ReceiveFormDefinitionsSuccess(formDefinitionWrapper));

    }

}
