package com.happ.models;

import java.util.List;

/**
 * Created by dante on 11/9/16.
 */
public class RouteResponse {

    public List<Route> routes;

    public String getPoints() {
        return this.routes.get(0).overview_polyline.points;
    }

    class Route {
        OverviewPolyline overview_polyline;
    }

    class OverviewPolyline {
        String points;
    }
}
