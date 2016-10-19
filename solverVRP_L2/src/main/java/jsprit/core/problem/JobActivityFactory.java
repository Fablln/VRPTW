package jsprit.core.problem;

import java.util.List;

import jsprit.core.problem.job.Job;

/**
 * JobActivityFactory that creates the activities to the specified job.
 */
public interface JobActivityFactory {

    public List<AbstractActivity> createActivities(Job job);

}
