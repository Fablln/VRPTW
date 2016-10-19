package jsprit.core.algorithm.ruin;

import java.util.Iterator;

import jsprit.core.problem.job.Job;

/**
* Created by schroeder on 07/01/15.
*/
public interface JobNeighborhoods {

    public Iterator<Job> getNearestNeighborsIterator(int nNeighbors, Job neighborTo);

    public void initialise();

}
