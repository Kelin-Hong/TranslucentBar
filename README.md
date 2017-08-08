# Background
Android 4.4 (KitKat) introduced translucent system UI styling for status bar, then Android 5.0+ offers simple ways to enable translucency in Activity（ ``` android:windowTranslucentStatus```） and tint color for status bar,But how to perfectly achieve translucent system UI styling for status bar is troublesome as before，particularly  compatible with KitKat. Here are some trouble you must care about:
- FitsSystemWindows is puzzling，how to accurately use fitsSystemWindows is full of challenges（hint: it would get different effects between KitKat and Lollipop. CoordinatorLayout、AppBarLayout、CollapsingToolbarLayout has reimplemented FitsSystemWindows）
- How to make view(ImageView) over status bar easily When use CollapsingToolbarLayout 
- How to tint color for status bar and action bar expediently（especial on KitKat）
- How to make translucent status bar apply on Fragment and freely change color or style when fragment transform.
- How to make status bar translucent or transparent expediently (only Lollipop)
- ....


## TranslucentBar ##
TranslucentBar is come to resolve all problem we mention above. This library offers a simple way to translucent system UI styling and tint color for status bar,you don't need to care for any about "FitsSystemWindows" and status bar colors. it can work above API 19(KitKat 4.4).

![](art/9.gif) 

## Demo ##

Apk Download:[TranslucentBar.apk](art/TranslucentBar.apk) 

## Download ##

```groovy
  compile 'com.kelin.translucentbar:library:0.8.0' 
```

## Usage

#### 1、Values-Styles

You must first enable translucency and remove ActionBar and WindowTitle in your Activity. Just  add following styles，then set it for your Activity in AndroidManifest.xml.After that,you must make sure add toolbar in your layout to obtain action bar in your view.

**values**

```xml
<style name="AppTheme.TranslucentStatusBar">
    <item name="windowActionBar">false</item>
    <item name="windowNoTitle">true</item>
</style>
```

**values-v19**

```xml
<style name="AppTheme.TranslucentStatusBar">
    <item name="windowActionBar">false</item>
    <item name="windowNoTitle">true</item>
    <item name="android:windowTranslucentStatus">true</item>
</style>
```

#### 2、Activity

**translucent**

 make status bar translucent and tint color,if you don't set any colors,toolbar would be tinted with @color/colorPrimary  and status bar would be tinted with @color/colorPrimaryDark

**transparent**

when set windowTranslucentStatus true, system will add translucent gray shade above of status bar,this is standard style of material design，but some times，you may want to remove the shade, for example: if the color of ActionBar is white and now show a gray shape above of white status bar is so ugly exactly！

```java 
 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ....
        TranslucentBarManager translucentBarManager = new TranslucentBarManager(this);
        translucentBarManager.translucent(this);
        //or translucentBarManager.transparent(this);
        .....
    }
```
translucent(SampleActivity.this) |transparent(SampleActivity.this)
---- | ---
![](art/1.png) |![](art/2.png)
![](art/3.gif)|![](art/4.gif)


**Tint Color**
```java 
 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ......
        TranslucentBarManager translucentBarManager = new TranslucentBarManager(this);   
        translucentBarManager.translucent(this,android.R.color.holo_purple);
        //or translucentBarManager.transparent(this,android.R.color.holo_purple);
        .....
    }
```


translucent(activity,color) | transparent(activity,color) 
---- | ---
![](art/5.png) | ![](art/6.png)
![](art/7.gif) | ![](art/8.gif)



  

#### 3、Fragment

 Nothing less than Activity ,but  translucent(transparent) method must call in onCreateView  to take effect.
```java
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_image_over_statusbar, container, false);
    TranslucentBarManager translucentBarManager = new TranslucentBarManager(this);
    translucentBarManager.translucent(this, view, android.R.color.holo_orange_dark);
    return view;
 }
 ```
translucent(fragment,rootView,color) | transparent(fragment,rootView,color) 
---- | ---
![](art/9.gif)| ![](art/10.gif)







## License
   ```
 Copyright 2016 Kelin Hong
    
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
    
 http://www.apache.org/licenses/LICENSE-2.0
    
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
    
   ```