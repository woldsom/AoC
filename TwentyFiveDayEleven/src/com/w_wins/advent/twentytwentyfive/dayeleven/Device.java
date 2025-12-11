package com.w_wins.advent.twentytwentyfive.dayeleven;

import com.w_wins.common.CollectorUtil;
import com.w_wins.common.Functions;
import com.w_wins.common.Strings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record Device(String label, Set<Device> outputs) {
    public static Device ofName(final String name) {
        return new Device(name, new HashSet<>());
    }

    public static Map.Entry<String, Set<String>> parse(final String line) {
        final List<String> parts = Strings.split(line, ": ").toList();
        return Map.entry(parts.getFirst(), Strings.split(parts.getLast()).collect(Collectors.toSet()));
    }

    public static Map<String, Device> deviceMap(final Map<String, Set<String>> nameMap) {
        final Set<Device> leaves = new HashSet<>();
        final Map<String, Device> devices = new HashMap<>(nameMap.keySet().stream().map(Functions.entry(Device::ofName)).collect(CollectorUtil.toMap()));
        devices.forEach((name, device) -> nameMap.get(name).stream().map(k -> {
            final Device child = devices.get(k);
            if (child == null) {
                final Device leaf = ofName(k);
                leaves.add(leaf);
                return leaf;
            }
            return child;
        }).forEach(device.outputs()::add));
        leaves.forEach(leaf -> {
            devices.put(leaf.label(), leaf);
            nameMap.put(leaf.label(), new HashSet<>());
        });
        return devices;
    }

    @Override
    public String toString() {
        return "Device \"" + label + "\" with children " + outputs().stream().map(Device::label).collect(Collectors.joining(",")) + ".";
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Device device = (Device) o;
        return label.equals(device.label);
    }

    @Override
    public int hashCode() {
        return label.hashCode();
    }
}
