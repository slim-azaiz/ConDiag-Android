/*
 * Copyright (C) 2013 The Android Open Source Project
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

package info.androidhive.gmail.control_diagnostic.control.basicmultitouch;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;

import info.androidhive.gmail.R;
import info.androidhive.gmail.control_diagnostic.control.OnSwipeTouchListener;

import static info.androidhive.gmail.control_diagnostic.control.ControlActivity.postCommand;


public class TouchActivity extends AppCompatActivity {
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TouchDisplayView mView = new TouchDisplayView(this );

        setContentView(R.layout.touch_layout);
        Log.d("TOUCH","fffff");

    }


}
