package org.jboss.tyr.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.jboss.tyr.TestUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({GitHubAPI.class})
@PowerMockIgnore({"javax.xml.*", "org.xml.sax.*"})
public class GitHubAPITest {

    @Before
    public void before() throws Exception {
        PowerMockito.spy(GitHubAPI.class);
        PowerMockito.doReturn(null).when(GitHubAPI.class, TestUtils.READ_TOKEN);
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateCommitStatusNullParameters() {
        GitHubAPI.updateCommitStatus(null, null, null, null, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void testGetJsonWithCommitsNullParameter() {
        GitHubAPI.getCommitsJSON(null);
    }
}
