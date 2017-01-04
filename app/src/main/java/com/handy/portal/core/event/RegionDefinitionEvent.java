package com.handy.portal.core.event;

import android.content.Context;

import com.handy.portal.core.model.definitions.FormDefinitionWrapper;
import com.handy.portal.data.DataManager;

public abstract class RegionDefinitionEvent extends HandyEvent
{
    public static class RequestFormDefinitions extends RequestEvent
    {
        public final Context context;
        public final String region;

        public RequestFormDefinitions(String region, Context context)
        {
            this.context = context;
            this.region = region;
        }
    }

    public static class ReceiveFormDefinitionsSuccess extends ReceiveSuccessEvent
    {
        public final FormDefinitionWrapper formDefinitionWrapper;

        public ReceiveFormDefinitionsSuccess(FormDefinitionWrapper formDefinitionWrapper)
        {
            this.formDefinitionWrapper = formDefinitionWrapper;
        }
    }

    public static class ReceiveFormDefinitionsError extends ReceiveErrorEvent
    {
        public ReceiveFormDefinitionsError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }
}
