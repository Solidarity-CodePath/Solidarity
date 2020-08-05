package com.example.solidarity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DistanceMatrix {

    private int distance;
    private String distanceText;

    public DistanceMatrix() {}

    public static JSONArray getJsonArray(JSONObject jsonObject) throws JSONException {
        JSONArray rows = (JSONArray) jsonObject.get("rows");
        JSONObject rowsObject = (JSONObject) rows.get(0);
        JSONArray elements = (JSONArray) rowsObject.get("elements");
        return elements;
    }


    public static DistanceMatrix fromJson(JSONObject jsonObject) throws JSONException {
        DistanceMatrix distanceMatrix = new DistanceMatrix();

        JSONObject distanceObject = (JSONObject) jsonObject.get("distance");
        distanceMatrix.distanceText = (String) distanceObject.get("text");

        //String is in format "6 mi" or "6.4 mi", or "1,779 mi", so need to reformat before converting to int
        String distanceConvert = distanceMatrix.distanceText.replaceAll(",", "");
        if (distanceConvert.contains("ft")) {
            distanceConvert = "0";
        }
        if (distanceConvert.contains(".")) {
            distanceConvert = distanceConvert.substring(0, distanceConvert.indexOf("."));
        }
        if (distanceConvert.contains(" ")) {
            distanceConvert = distanceConvert.substring(0, distanceConvert.indexOf(" "));
        }
        distanceMatrix.distance = Integer.valueOf(distanceConvert);

        return distanceMatrix;

    }

    public String getDistanceText() {
        return distanceText;
    }

    public int getDistance() {
        return distance;
    }

    public static List<DistanceMatrix> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<DistanceMatrix> distMatrices = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            distMatrices.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return distMatrices;
    }


}
