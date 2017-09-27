package com.sixt.service.framework.ports.logs.logger;

import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

final class MarkersMapper {

    private final Map<String, Object> markers = new LinkedHashMap<>();

    MarkersMapper(
        final String operationType,
        final JsonObject jsonContainedMarkers,
        final String[] markerNames
    ) {
        markers.put("operationType", operationType);

        if (markerNames.length > 0) {
            Arrays.stream(markerNames).forEach(
                markerToAdd -> {
                    if (jsonContainedMarkers.has(markerToAdd)) {
                        markers.put(markerToAdd, jsonContainedMarkers.get(markerToAdd));
                    }
                }
            );
        }
    }

    public Map<String, Object> markers() {
        return markers;
    }
}
