package com.handy.portal.data;

public class EnvironmentManager
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
        S, Q1, Q2, Q3, Q4, Q5, Q6;

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
