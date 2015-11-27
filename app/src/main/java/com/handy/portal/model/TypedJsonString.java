package com.handy.portal.model;

import retrofit.mime.TypedString;

public class TypedJsonString extends TypedString
{
    private static final String APPLICATION_JSON = "application/json";

    public TypedJsonString(String string) { super(string); }

    @Override
    public String mimeType() { return APPLICATION_JSON; }
}
