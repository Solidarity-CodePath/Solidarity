package com.example.solidarity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DistanceMatrix {

    private int distance;
    private String distanceText;

    public DistanceMatrix(JSONObject jsonObject) throws JSONException {
        JSONArray rows = (JSONArray) jsonObject.get("rows");
        JSONObject rowsObject = (JSONObject) rows.get(0);
        JSONArray elements = (JSONArray) rowsObject.get("elements");
        JSONObject elementsObject = (JSONObject) elements.get(0);
        JSONObject distanceObject = (JSONObject) elementsObject.get("distance");
        distanceText = (String) distanceObject.get("text");

        //String is in format "6 mi" or "6.4 mi", or "1,779 mi", so need to reformat before converting to int
        String distanceConvert = distanceText.replaceAll(",", "");
        if (distanceConvert.contains(".")) {
            distanceConvert = distanceConvert.substring(0, distanceConvert.indexOf("."));
        }
        if (distanceConvert.contains(" ")) {
            distanceConvert = distanceConvert.substring(0, distanceConvert.indexOf(" "));
        }
        distance = Integer.valueOf(distanceConvert);

    }

    public String getDistanceText() {
        return distanceText;
    }

    public int getDistance() {
        return distance;
    }


}
