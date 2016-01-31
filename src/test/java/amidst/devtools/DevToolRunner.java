package amidst.devtools;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import amidst.mojangapi.file.MojangApiParsingException;

/**
 * Eclipse does not allow to run the main directly as a Java Application,
 * because it is placed in the test directory. Ensure that these tests are
 * ignored when creating a commit, so it will be ignored by travis ci when
 * creating a new release.
 */
@Ignore
public class DevToolRunner {
	@Test
	public void generateTestData() throws IOException,
			MojangApiParsingException {
		GenerateWorldTestData.main(new String[0]);
	}
}
