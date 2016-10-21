package com.home.comparator;

import java.util.ArrayList;
import java.util.List;

public class AreaHolder {
    private List<Area> areas = new ArrayList<>();

    public synchronized void addPoint(int x, int y, int maxDistance) {
        for (Area area : areas) {
            if (area.tryAddPoint(x, y, maxDistance)) {
                return;
            }
        }

        areas.add(new Area(x,y));
    }

    public List<Area> getAreas() {
        return areas;
    }
}
