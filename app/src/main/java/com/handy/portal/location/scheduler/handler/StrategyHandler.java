package com.handy.portal.location.scheduler.handler;

/**
 * handles a schedule strategy
 */
public abstract class StrategyHandler
{
    protected abstract boolean isStrategyExpired();
    protected abstract void buildStrategyBatchUpdatesAndNotifyReady();
    protected abstract void startStrategy();
    protected abstract void stopStrategy();

    public interface StrategyCallbacks<T extends StrategyHandler>
    {
        void onStrategyExpired(T strategyHandler);
    }
}
