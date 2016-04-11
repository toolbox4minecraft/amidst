package amidst.settings;

import java.util.prefs.Preferences;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.mojangapi.world.Dimension;

@Immutable
public enum Settings {
	;

	public static <T> Setting<T> createImmutable(T value) {
		return new ImmutableSetting<T>(value);
	}

	public static Setting<String> createString(Preferences preferences, String key, @NotNull String defaultValue) {
		// @formatter:off
		return new SettingBase<String>(defaultValue,
				value -> preferences.get(key, value),
				value -> preferences.put(key, value));
		// @formatter:on
	}

	public static Setting<Boolean> createBoolean(Preferences preferences, String key, boolean defaultValue) {
		// @formatter:off
		return new SettingBase<Boolean>(defaultValue,
				value -> preferences.getBoolean(key, value),
				value -> preferences.putBoolean(key, value));
		// @formatter:on
	}

	public static Setting<Dimension> createDimension(Preferences preferences, String key, Dimension defaultValue) {
		// @formatter:off
		return new SettingBase<Dimension>(defaultValue,
				value -> Dimension.from(preferences.getInt(key, value.getId())),
				value -> preferences.putInt(key, value.getId()));
		// @formatter:on
	}

	public static <T> Setting<T> createDummy(T defaultValue) {
		// @formatter:off
		return new SettingBase<T>(defaultValue,
				value -> value,
				value -> {});
		// @formatter:on
	}

	public static <T> Setting<T> createDummyWithListener(T defaultValue, Runnable listener) {
		return createWithListener(createDummy(defaultValue), listener);
	}

	public static <T> Setting<T> createWithListener(Setting<T> setting, Runnable listener) {
		return new Setting<T>() {
			@Override
			public T get() {
				return setting.get();
			}

			@Override
			public void set(T value) {
				setting.set(value);
				listener.run();
			}
		};
	}
}
