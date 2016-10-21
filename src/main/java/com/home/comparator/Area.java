package com.home.comparator;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

public class Area {
    private Point firstPoint;
    private Point secondPoint;
    private Point thirdPoint;
    private Point fourthPoint;

    public Area(int x, int y) {
        this.firstPoint = new Point(x, y);
        this.secondPoint = new Point(x, y);
        this.thirdPoint = new Point(x, y);
        this.fourthPoint = new Point(x, y);
    }

    public boolean tryAddPoint(int x, int y, int maxDistance) {
        Point targetPoint = new Point(x, y);

        if (isContainsPoint(targetPoint)) {
            return true;
        }

        List<Point> sortedPoints = getPointsSortedByDistance(targetPoint);

        double distance = getDistance(targetPoint, sortedPoints);

        if (distance <= maxDistance && distance > 0) {
            addPoint(targetPoint, sortedPoints);
            return true;
        }

        return false;
    }

    private void addPoint(Point targetPoint, List<Point> sortedPoints) {
        if (isRectangle()) {
            movePoints(sortedPoints.get(3), targetPoint);
        } else if (isLine()) {
            movePointsOfLine(sortedPoints, targetPoint);
        } else {
            sortedPoints.get(0).setCoordinates(targetPoint);
            sortedPoints.get(1).setCoordinates(targetPoint);
        }
    }

    private double getDistance(Point targetPoint, List<Point> sortedPoints) {
        double distance;
        if (isRectangle()) {
            distance = getDistance(sortedPoints.get(0), sortedPoints.get(1), targetPoint);
        } else if (isLine()) {
            Iterator<Point> iterator = new HashSet<>(sortedPoints).iterator();
            distance = getDistance(iterator.next(), iterator.next(), targetPoint);
        } else {
            distance = sortedPoints.get(0).distance(targetPoint);
        }
        return distance;
    }

    private boolean isRectangle() {
        return getCountOfUniquePoints() == 4;
    }

    private boolean isLine() {
        return getCountOfUniquePoints() == 2;
    }

    private int getCountOfUniquePoints() {
        return new HashSet<>(getPointsAsList()).size();
    }

    private boolean isContainsPoint(Point targetPoint) {
        Point leftTop = findLeftTopPoint();
        Point rightBottom = findRightBottomPoint();

        return targetPoint.getX() >= leftTop.getX()
                && targetPoint.getY() >= leftTop.getY()
                && targetPoint.getX() <= rightBottom.getX()
                && targetPoint.getY() <= rightBottom.getY();
    }

    public Point findLeftTopPoint() {
        List<Point> pointsAsList = getPointsAsList();

        pointsAsList.sort((firstPoint, secondPoint) ->
                (firstPoint.getX() + firstPoint.getY()) - (secondPoint.getX() + secondPoint.getY()));

        return pointsAsList.get(0);
    }

    public Point findRightBottomPoint() {
        List<Point> pointsAsList = getPointsAsList();

        pointsAsList.sort((firstPoint, secondPoint) ->
                (firstPoint.getX() + firstPoint.getY()) - (secondPoint.getX() + secondPoint.getY()));

        return pointsAsList.get(3);
    }

    private void movePointsOfLine(List<Point> sortedPoints, Point targetPoint) {
        Set<Point> points = new HashSet<>(sortedPoints);
        Iterator<Point> iterator = points.iterator();
        Point firstPoint = iterator.next();
        Point secondPoint = iterator.next();

        if (isPointBetweenPointsByX(targetPoint, firstPoint, secondPoint) &&
                !isInlinePoints(targetPoint, firstPoint, secondPoint)) {
            firstPoint.setY(targetPoint.getY());
            secondPoint.setY(targetPoint.getY());
        } else if (isPointBetweenPointsByY(targetPoint, firstPoint, secondPoint) &&
                !isInlinePoints(targetPoint, firstPoint, secondPoint)) {
            firstPoint.setX(targetPoint.getX());
            secondPoint.setX(targetPoint.getX());
        } else {
            double targetDistance = Math.min(firstPoint.distance(targetPoint), secondPoint.distance(targetPoint));

            for (Point sortedPoint : sortedPoints) {
                if (sortedPoint.distance(targetPoint) == targetDistance) {
                    sortedPoint.setCoordinates(targetPoint);
                }
            }
        }
    }

    private void movePoints(Point excludePoint, Point targetPoint) {
        List<Entry<Point, Point>> borders = getBordersWithoutPoint(excludePoint);

        Point cornerPoint = getCornerPoint(borders);

        moveBorderByY(borders, new Point(cornerPoint.getX(), targetPoint.getY()));
        moveBorderByX(borders, new Point(targetPoint.getX(), cornerPoint.getY()));
    }

    private void moveBorderByY(List<Entry<Point, Point>> borders, Point targetPoint) {
        if (isContainsPoint(targetPoint)) {
            return;
        }

        for (Entry<Point, Point> border : borders) {
            if (isPointBetweenPoints(targetPoint, border.getKey(), border.getValue()) &&
                    !isInlinePoints(targetPoint, border.getKey(), border.getValue())) {
                if (isPointBetweenPointsByX(targetPoint, border.getKey(), border.getValue())) {
                    border.getKey().setY(targetPoint.getY());
                    border.getValue().setY(targetPoint.getY());
                }
            }
        }
    }

    private void moveBorderByX(List<Entry<Point, Point>> borders, Point targetPoint) {
        if (isContainsPoint(targetPoint)) {
            return;
        }

        for (Entry<Point, Point> border : borders) {
            if (isPointBetweenPoints(targetPoint, border.getKey(), border.getValue()) &&
                    !isInlinePoints(targetPoint, border.getKey(), border.getValue())) {
                if (isPointBetweenPointsByY(targetPoint, border.getKey(), border.getValue())) {
                    border.getKey().setX(targetPoint.getX());
                    border.getValue().setX(targetPoint.getX());
                }
            }
        }
    }

    public List<Entry<Point, Point>> getBorders() {
        return new ArrayList<>(Arrays.asList(
                new SimpleEntry<>(firstPoint, secondPoint),
                new SimpleEntry<>(secondPoint, fourthPoint),
                new SimpleEntry<>(fourthPoint, thirdPoint),
                new SimpleEntry<>(thirdPoint, firstPoint)));
    }

    private List<Entry<Point, Point>> getBordersWithoutPoint(Point excludePoint) {
        List<Entry<Point, Point>> borders = getBorders();

        Iterator<Entry<Point, Point>> iterator = borders.iterator();

        int count = 0;

        while (iterator.hasNext() && count < 2) {
            Entry<Point, Point> target = iterator.next();

            if (target.getValue().equals(excludePoint) || target.getKey().equals(excludePoint)) {
                count++;
                iterator.remove();
            }
        }

        return borders;
    }

    private Point getCornerPoint(List<Entry<Point, Point>> borders) {
        List<Point> points = new ArrayList<>();

        for (Entry<Point, Point> border : borders) {
            if (points.contains(border.getKey())) {
                return border.getKey();
            }

            if (points.contains(border.getValue())) {
                return border.getValue();
            }

            points.add(border.getKey());
            points.add(border.getValue());
        }

        throw new IllegalStateException("Corner not found!");
    }

    private boolean isInlinePoints(Point... points) {
        boolean inlineByX = true;
        boolean inlineByY = true;

        for (int i = 0; i < points.length - 1; i++) {
            if (points[i].getX() != points[i + 1].getX()) {
                inlineByX = false;
            }

            if (points[i].getY() != points[i + 1].getY()) {
                inlineByY = false;
            }
        }

        return inlineByX || inlineByY;
    }

    private double getDistance(Point firstPoint, Point secondPoint, Point targetPoint) {
        if (isInlinePoints(firstPoint, secondPoint, targetPoint)) {
            return Math.min(firstPoint.distance(targetPoint), secondPoint.distance(targetPoint));
        }

        if (isPointBetweenPoints(targetPoint, firstPoint, secondPoint)) {
            return getAltitudeOfTriangle(firstPoint, secondPoint, targetPoint);
        }

        return firstPoint.distance(targetPoint);
    }

    private double getAltitudeOfTriangle(Point firstPoint, Point secondPoint, Point targetPoint) {
        double a = firstPoint.distance(secondPoint);
        double b = targetPoint.distance(firstPoint);
        double c = targetPoint.distance(secondPoint);
        double p = (a + b + c) / 2;

        return ((Math.sqrt(p * (p - a) * (p - b) * (p - c))) / a);
    }

    private boolean isPointBetweenPoints(Point targetPoint, Point firstPoint, Point secondPoint) {
        if (isPointBetweenPointsByX(targetPoint, firstPoint, secondPoint)) {
            return true;
        } else if (isPointBetweenPointsByY(targetPoint, firstPoint, secondPoint)) {
            return true;
        }
        return false;
    }

    private boolean isPointBetweenPointsByY(Point targetPoint, Point firstPoint, Point secondPoint) {
        return isNumberBetweenNumbers(targetPoint.getY(), firstPoint.getY(), secondPoint.getY());
    }

    private boolean isPointBetweenPointsByX(Point targetPoint, Point firstPoint, Point secondPoint) {
        return isNumberBetweenNumbers(targetPoint.getX(), firstPoint.getX(), secondPoint.getX());
    }

    private boolean isNumberBetweenNumbers(int targetNumber, int firstNumber, int secondNumber) {
        int max = Math.max(firstNumber, secondNumber);
        int min = Math.min(firstNumber, secondNumber);
        return targetNumber >= min && targetNumber <= max;
    }

    private List<Point> getPointsSortedByDistance(Point targetPoint) {
        List<Point> points = getPointsAsList();

        points.sort((firstPoint, secondPoint) -> {
            double firstDistance = firstPoint.distance(targetPoint);
            double secondDistance = secondPoint.distance(targetPoint);
            return (int) (firstDistance - secondDistance);
        });

        return points;
    }

    private List<Point> getPointsAsList() {
        return Arrays.asList(firstPoint, secondPoint, thirdPoint, fourthPoint);
    }
}
