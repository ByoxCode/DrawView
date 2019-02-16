# DrawView

![drawview](https://github.com/ByoxCode/DrawView/blob/master/destacada.png)

![DrawView](https://img.shields.io/badge/drawview-1.3.1-blue.svg) ![DrawViewSnapshot](https://img.shields.io/badge/snapshot-1.0.1-purple.svg) ![Platform](https://img.shields.io/badge/platform-Android-green.svg) ![Language](https://img.shields.io/badge/language-Java-red.svg)

Android view that allows the user to create drawings. Draw anything you like in your Android device from simple view.

Customize draw settings like draw color, draw width or draw tools.

Undo or redo actions it is possible with DrawView.

![undo redo gif](https://github.com/ByoxCode/DrawView/blob/master/2016.11.10_17.57.50.gif)
![text tool gif](https://github.com/ByoxCode/DrawView/blob/master/2016.11.10_18.00.25.gif)
![save bitmap gif](https://github.com/ByoxCode/DrawView/blob/master/2016.11.10_18.03.14.gif)
![zoom gif](https://github.com/ByoxCode/DrawView/blob/master/2017.04.01_04.53.15.gif)
![camera gif](https://github.com/ByoxCode/DrawView/blob/master/2017.05.04_02.56.52.gif)

View example [APK](https://play.google.com/store/apps/details?id=com.byox.drawviewproject)

Usage
--------

With Gradle:
```groovy
  implementation 'com.byox.drawview:drawview:X.X.X'
```
or Maven:
```xml
<dependency>
  <groupId>com.byox.drawview</groupId>
  <artifactId>drawview</artifactId>
  <version>X.X.X</version>
  <type>pom</type>
</dependency>
```

Where **X.X.X** is the current version of DrawView

Snapshot
--------

Import from:
```groovy
  maven { url "https://raw.githubusercontent.com/ByoxCode/DrawView/snapshot/aar/snapshots/" }
```

With Gradle:
```groovy
  implementation 'com.byox.drawview:drawview:X.X.X-SNAPSHOT'
```
or Maven:
```xml
<dependency>
  <groupId>com.byox.drawview</groupId>
  <artifactId>drawview</artifactId>
  <version>X.X.X-SNAPSHOT</version>
  <type>pom</type>
</dependency>
```

Where **X.X.X** is the current version of DrawView Snapshot

How to use
--------
Add DrawView to your layout

```xml
  <com.byox.drawview.views.DrawView
        android:id="@+id/draw_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:dv_draw_alpha="255"
        app:dv_draw_anti_alias="true"
        app:dv_draw_color="@color/colorAccent"
        app:dv_draw_corners="round"
        app:dv_draw_dither="true"
        app:dv_draw_enable_zoom="true"
        app:dv_draw_font_family="default_font"
        app:dv_draw_font_size="12"
        app:dv_draw_max_zoom_factor="8"
        app:dv_draw_mode="draw"
        app:dv_draw_style="stroke"
        app:dv_draw_tool="pen"
        app:dv_draw_width="4"
        app:dv_draw_zoomregion_maxscale="5"
        app:dv_draw_zoomregion_minscale="2"
        app:dv_draw_zoomregion_scale="4" />
```

Attributes
--------
Please visit [Wiki](https://github.com/ByoxCode/DrawView/wiki)

Donate
--------
If this library is useful for you, and you like it, and if want to contribute for develop more libraries, you can buy me a beer. [![PayPal](https://www.paypalobjects.com/webstatic/mktg/logo/pp_cc_mark_37x23.jpg)](https://paypal.me/IngMedinaCruz)

Apps that uses DrawView
--------

Please feel free to contact me if you like to appear in this section.

License
--------

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
