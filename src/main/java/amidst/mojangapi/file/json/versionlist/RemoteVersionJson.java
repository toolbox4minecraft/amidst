package amidst.mojangapi.file.json.versionlist;

import java.net.URL;

import amidst.documentation.GsonConstructor;
import amidst.mojangapi.file.json.ReleaseType;

public class RemoteVersionJson {
	private volatile String id;
	private volatile ReleaseType type;
	private volatile Inner downloads;

	@GsonConstructor
	public RemoteVersionJson() {
	}

	public String getId() {
		return id;
	}

	public ReleaseType getType() {
		return type;
	}

	public URL getClientUrl() {
		return downloads.client == null ? null : downloads.client.url;
	}

	public URL getServerUrl() {
		return downloads.server == null ? null : downloads.server.url;
	}

	private static class Inner {
		public volatile Download client;
		public volatile Download server;

		@GsonConstructor
		public Inner() {
		}
	}

	private static class Download {
		public volatile URL url;

		@GsonConstructor
		public Download() {
		}
	}
}
