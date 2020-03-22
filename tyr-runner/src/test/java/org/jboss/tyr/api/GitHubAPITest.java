package org.jboss.tyr.api;

import org.jboss.tyr.InvalidPayloadException;
import org.junit.Test;

import javax.inject.Inject;

public class GitHubAPITest {

    @Inject
    GitHubAPI git;

    @Test(expected = NullPointerException.class)
    public void testUpdateCommitStatusNullParameters() {
        git.updateCommitStatus(null, null, null, null, null, null);
    }

    @Test(expected = InvalidPayloadException.class)
    public void testGetJsonWithCommitsNullParameter() throws InvalidPayloadException {
        git.getCommitsJSON(null);
    }
}
