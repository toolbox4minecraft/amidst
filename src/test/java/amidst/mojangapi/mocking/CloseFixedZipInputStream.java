package amidst.mojangapi.mocking;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.zip.ZipInputStream;

import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public class CloseFixedZipInputStream extends ZipInputStream {
	public CloseFixedZipInputStream(InputStream in, Charset charset) {
		super(in, charset);
	}

	@Override
	public void close() throws IOException {
		// noop
	}

	public Closeable getCloseable() {
		return super::close;
	}
}
