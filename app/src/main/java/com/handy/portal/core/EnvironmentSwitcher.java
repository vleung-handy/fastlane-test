package com.handy.portal.core;

public class EnvironmentSwitcher
{
    private Environment environment = Environment.S;
    private boolean pinRequestEnabled = true;

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

    public void setPinRequestEnabled(boolean pinRequestEnabled)
    {
        this.pinRequestEnabled = pinRequestEnabled;
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
