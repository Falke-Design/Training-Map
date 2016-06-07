/*
 * Copyright (c) 2016 Martin Pfeffer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.falke.training_map.util;

import java.text.DecimalFormat;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class FormatUtils {

    public static String formatAngle(float angle) {
        return Integer.toString(Math.round(angle)) + "°";
    }


    public static String formatMaxSpeed(float maxSpeed, boolean isMetric) {
        maxSpeed *= (isMetric ? 3.6f : 2.23694f);
        return Integer.toString(Math.round(maxSpeed)) + (isMetric ? " km/h" : " mph");
    }


    public static String formatG(float maxG_force) {
        return new DecimalFormat("0.00").format(maxG_force) + " G";
    }


    public static String formatDuration(long duration) {
        int durationInSec = (int) (duration / 1000);
        int hours = durationInSec / 3600;
        int minutes = (durationInSec % 3600) / 60;
        int seconds = durationInSec % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }


    public static String formatDistance(float distance, boolean isMetric) {
        distance /= (isMetric ? 1000f : 1609.34f);
        return new DecimalFormat("0.##").format(distance) + (isMetric ? " km" : " mi.");
    }


    public static String formatAverageSpeed(float distance, long duration, boolean isMetric) {
        float speed = distance / (duration / 1000f);
        if (isMetric) {
            return Integer.toString(Math.round(speed * 3.6f)) + " km/h";
        } else {
            return Integer.toString(Math.round(speed * 2.23694f)) + " mph";
        }
    }

}
