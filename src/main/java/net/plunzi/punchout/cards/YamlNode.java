package net.plunzi.punchout.cards;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.Set;

/**
 * Lightweight helper wrapper over {@link ConfigurationSection} to simplify reading values and handling missing keys.
 */
public final class YamlNode {

    private final String path;
    private final ConfigurationSection section;

    private YamlNode(String path, ConfigurationSection section) {
        this.path = path;
        this.section = section;
    }

    public static YamlNode of(String path, ConfigurationSection section) {
        return new YamlNode(path, section);
    }

    public boolean isEmpty() {
        return section == null;
    }

    public YamlNode child(String key) {
        if (section == null) {
            return new YamlNode(fullPath(key), null);
        }
        ConfigurationSection sub = section.getConfigurationSection(key);
        return new YamlNode(fullPath(key), sub);
    }

    public Set<String> keys() {
        if (section == null) {
            return Collections.emptySet();
        }
        return section.getKeys(false);
    }

    public String string(String key) {
        if (section == null || !section.contains(key)) {
            throw new CardRenderConfigException("missing required string setting '" + fullPath(key) + "'");
        }
        return section.getString(key);
    }

    public double number(String key) {
        if (section == null || !section.contains(key)) {
            throw new CardRenderConfigException("missing required numeric setting '" + fullPath(key) + "'");
        }
        return section.getDouble(key);
    }

    public double number(String key, double defaultValue) {
        if (section == null || !section.contains(key)) {
            return defaultValue;
        }
        return section.getDouble(key, defaultValue);
    }

    private String fullPath(String key) {
        if (path.isEmpty()) {
            return key;
        }
        return path + "." + key;
    }
}
