package com.handy.portal.core;

import android.content.Context;

import java.util.Properties;

public class EnvironmentModifier
{
    private String environment = "s";
    private boolean pinRequestEnabled = true;

    public EnvironmentModifier(Context context, BuildConfigWrapper buildConfigWrapper)
    {
        // only allow environment overrides on debug builds
        if (buildConfigWrapper.isDebug())
        {
            try
            {
                Properties properties = PropertiesReader.getProperties(context, "override.properties");
                boolean disablePinRequest = Boolean.parseBoolean(properties.getProperty("disable_pin_request", "false"));
                String environment = properties.getProperty("environment", "s");

                this.pinRequestEnabled = !disablePinRequest;
                this.environment = environment;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public String getEnvironmentPrefix()
    {
        return environment;
    }

    public boolean pinRequestEnabled()
    {
        return pinRequestEnabled;
    }

    public void setEnvironmentPrefix(String environmentPrefix)
    {
        this.environment = environmentPrefix;
    }

    public enum Environment
    {
        S, Q1, Q2, Q3, Q4, Q5, Q6, Q7, Q8, Q9, Q10, Q11, Q12;

        public String getPrefix()
        {
            return this.toString().toLowerCase();
        }
    }
}
