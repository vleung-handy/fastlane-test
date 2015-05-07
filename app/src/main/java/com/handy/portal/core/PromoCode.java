package com.handy.portal.core;

public final class PromoCode
{
    public enum Type
    {
        UNKNOWN, COUPON, VOUCHER
    }

    private Type type;
    private String code, uniq;
    private int serviceId;

    public PromoCode(final Type type, final String code)
    {
        this.type = type;
        this.code = code;
    }

    public final Type getType()
    {
        return type;
    }

    final void setType(final Type type)
    {
        this.type = type;
    }

    public final String getCode()
    {
        return code;
    }

    final void setCode(final String code)
    {
        this.code = code;
    }

    public final String getUniq()
    {
        return uniq;
    }

    public final void setUniq(final String uniq)
    {
        this.uniq = uniq;
    }

    public final int getServiceId()
    {
        return serviceId;
    }

    public final void setServiceId(final int serviceId)
    {
        this.serviceId = serviceId;
    }
}
