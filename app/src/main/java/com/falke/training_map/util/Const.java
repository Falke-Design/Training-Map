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

import android.Manifest;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class Const {

    public static final String P_F_LOC = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String P_C_LOC = Manifest.permission.ACCESS_COARSE_LOCATION;

    // velocity has to be exceeded that lean-angle and gForce become valid
    public static final float MIN_SPEED_IN_KMH_ACCEPT_MEASUREMENT = 20;
}
