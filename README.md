# DrawView

![drawview](https://github.com/ByoxCode/DrawView/blob/master/destacada.png)


Android view that allows the user to create drawings. Draw anything you like in your Android device from simple view.

Customize draw settings like draw color, draw width or draw tools. 

Undo or redo actions it is possible with DrawView.

![undo redo gif](https://github.com/ByoxCode/DrawView/blob/master/2016.11.10_17.57.50.gif)
![text tool gif](https://github.com/ByoxCode/DrawView/blob/master/2016.11.10_18.00.25.gif)
![save bitmap gif](https://github.com/ByoxCode/DrawView/blob/master/2016.11.10_18.03.14.gif)

View example [APK](https://play.google.com/store/apps/details?id=com.byox.drawviewproject)

Usage
--------

With Gradle:
```groovy
  compile 'com.byox.drawview:drawview:1.0.2'
```
or Maven:
```xml
<dependency>
  <groupId>com.byox.drawview</groupId>
  <artifactId>drawview</artifactId>
  <version>1.0.2</version>
  <type>pom</type>
</dependency>
```

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
        app:dv_draw_font_family="default_font"
        app:dv_draw_font_size="12"
        app:dv_draw_mode="draw"
        app:dv_draw_style="stroke"
        app:dv_draw_tool="pen"
        app:dv_draw_width="4" />
```

Attributes
--------
Please visit [Wiki](https://github.com/ByoxCode/DrawView/wiki)



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
