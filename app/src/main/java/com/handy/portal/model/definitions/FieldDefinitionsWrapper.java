package com.handy.portal.model.definitions;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class FieldDefinitionsWrapper //TODO: restructure this
{
    @SerializedName("field_definitions")
    Map<String, FieldDefinition> fieldDefinitionMap;

    public Map<String, FieldDefinition> getFieldDefinitionMap()
    {
        return fieldDefinitionMap;
    }


}
