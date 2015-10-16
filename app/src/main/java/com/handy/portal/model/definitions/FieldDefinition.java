package com.handy.portal.model.definitions;


import com.google.gson.annotations.SerializedName;

import java.util.regex.Pattern;

public class FieldDefinition
{
    @SerializedName("display_name")
    String displayName;

    @SerializedName("input_type")
    InputType inputType;

    @SerializedName("hint_text")
    String hintText;

    @SerializedName("pattern")
    String pattern;

    private Pattern compiledPattern;

    public enum InputType {
        //TODO: can we make this more concise
        @SerializedName("number")
        NUMBER ("number"),

        @SerializedName("alphanumeric")
        ALPHA_NUMERIC ("alphanumeric");

        private final String value;
        public String getValue()
        {
            return value;
        }
        InputType(String value)
        {
            this.value = value;
        }
    }

    public InputType getInputType()
    {
        return inputType;
    }

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

    public String getHintText()
    {
        return hintText;
    }
}
