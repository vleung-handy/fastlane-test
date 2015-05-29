package com.handy.portal.data;

public class EnvironmentSwitcher
{
    private Environment environment = Environment.S;

    public Environment getEnvironment()
    {
        return environment;
    }

    public void setEnvironment(Environment environment)
    {
        this.environment = environment;
    }

    public enum Environment
    {
        S, Q1, Q2, Q3, Q4, Q5, Q6, D1, D2, D3, D4, D5, D6, D7, D8, D9;

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
