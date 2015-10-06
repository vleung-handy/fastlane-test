package com.handy.portal.model.definitions;


import com.google.gson.annotations.SerializedName;

import java.util.regex.Pattern;

public class FieldDefinition
{
    @SerializedName("display_name")
    String displayName;

    @SerializedName("hint_text")
    String hintText;

    @SerializedName("error_message")
    String errorMessage;

    @SerializedName("pattern")
    String pattern;

    private Pattern compiledPattern;

//    public FieldDefinition()
//    {
//        compiledPattern = Pattern.compile(pattern); //pre-cache
//    }

    public String getDisplayName()
    {
        return displayName;
    }

    public Pattern getCompiledPattern() {
        if(compiledPattern==null)
        {
            compiledPattern = Pattern.compile(pattern);
        }
        return compiledPattern;
    }

    public String getPattern()
    {
        return pattern;
    }

    public String getHintText()
    {
        return hintText;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }
}
