package com.handy.portal.flow;

public abstract class SetupSteps
{
    public static class SaveProviderProfile extends FlowStep
    {
        @Override
        public boolean shouldExecute()
        {
            return false;
        }

        @Override
        public void execute()
        {

        }
    }
}
