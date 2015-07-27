package com.handy.portal.core;

import android.content.Context;

import com.handy.portal.constant.PrefsKey;
import com.handy.portal.manager.PrefsManager;

import java.util.Properties;

public class EnvironmentModifier
{
    private Environment environment = Environment.S;
    private boolean pinRequestEnabled = true;

    public EnvironmentModifier(Context context, BuildConfigWrapper buildConfigWrapper, PrefsManager prefsManager)
    {
        // only allow environment overrides on debug builds
        if (buildConfigWrapper.isDebug())
        {
            try
            {
                Properties properties = PropertiesReader.getProperties(context, "override.properties");
                boolean disablePinRequest = Boolean.parseBoolean(properties.getProperty("disable_pin_request", "false"));
                String environment = properties.getProperty("environment", "S");
                String token = properties.getProperty("token");

                this.pinRequestEnabled = !disablePinRequest;
                this.environment = Environment.valueOf(environment);
                prefsManager.setString(PrefsKey.AUTH_TOKEN, token);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public Environment getEnvironment()
    {
        return environment;
    }

    public boolean pinRequestEnabled()
    {
        return pinRequestEnabled;
    }

    public void setEnvironment(Environment environment)
    {
        this.environment = environment;
    }

    public enum Environment
    {
        S, Q1, Q2, Q3, Q4, Q5, Q6, Q7, Q8, Q9, Q10;

        public String getName()
        {
            return this.toString();
        }

        public String getPrefix()
        {
            return this.toString().toLowerCase();
        }
    }
}
