package amidst.settings;

import java.util.function.Supplier;
import java.util.prefs.Preferences;

import amidst.documentation.NotNull;
import amidst.mojangapi.world.Dimension;

public interface Setting<T> extends Supplier<T> {
	@Override
	@NotNull
	T get();

	void set(@NotNull T value);

	public static <T> Setting<T> createImmutable(T value) {
		return new ImmutableSetting<>(value);
	}

	public static Setting<String> createString(Preferences preferences, String key, @NotNull String defaultValue) {
		return new SettingBase<>(
			defaultValue,
			value -> preferences.get(key, value),
			value -> preferences.put(key, value));
	}

	public static Setting<Boolean> createBoolean(Preferences preferences, String key, boolean defaultValue) {
		return new SettingBase<>(
			defaultValue,
			value -> preferences.getBoolean(key, value),
			value -> preferences.putBoolean(key, value));
	}

	public static Setting<Dimension> createDimension(Preferences preferences, String key, Dimension defaultValue) {
		return new SettingBase<>(
			defaultValue,
			value -> Dimension.fromId(preferences.getInt(key, value.getId())),
			value -> preferences.putInt(key, value.getId()));
	}

	public static <T extends Enum<T>> Setting<T> createEnum(Preferences preferences, String key, T defaultValue) {
		Class<T> enumType = defaultValue.getDeclaringClass();
		return new SettingBase<>(defaultValue, value -> {
			String stored = preferences.get(key, null);
			try {
				return stored == null ? value : Enum.valueOf(enumType, stored);
			} catch (IllegalArgumentException e) {
				return value;
			}
		}, value -> preferences.put(key, value.name()));
	}

	public static <T> Setting<T> createDummy(T defaultValue) {
		return new SettingBase<>(defaultValue, value -> value, value -> {
		});
	}

	@FunctionalInterface
	public static interface SettingListener<T> {
		public void onSettingChanged(T oldValue, T newValue);
	}

	public default Setting<T> withListener(SettingListener<T> listener) {
		final Setting<T> setting = this;
		return new Setting<T>() {
			@Override
			public T get() {
				return setting.get();
			}

			@Override
			public void set(T value) {
				T previous = setting.get();
				setting.set(value);
				listener.onSettingChanged(previous, value);
			}
		};
	}
}
