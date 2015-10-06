package com.handy.portal.model.definitions;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class FormDefinitionWrapper //TODO: restructure this
{
    @SerializedName("region")
    String region;

    @SerializedName("form_definitions")
    Map<String, FieldDefinitionsWrapper> formDefinitions;

    public Map<String, FieldDefinitionsWrapper> getFormDefinitions()
    {
        return formDefinitions;
    }

    public String getRegion()
    {
        return region;
    }

    public Map<String, FieldDefinition> getFieldDefinitionsForForm(String formKey)
    {
        return (formDefinitions == null || formDefinitions.get(formKey) == null) ? null : formDefinitions.get(formKey).getFieldDefinitionMap();
    }
}
