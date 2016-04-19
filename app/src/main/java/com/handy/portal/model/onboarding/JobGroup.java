package com.handy.portal.model.onboarding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jtse on 4/18/16.
 * <p/>
 * TODO: JIA: make this class reflect the actual JSON coming from the server
 */
public class JobGroup
{
    public List<Job> jobs;
    public String title;

    public JobGroup(String title)
    {
        this.title = title;
        jobs = new ArrayList<>();
    }
}
