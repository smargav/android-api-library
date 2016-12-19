A library of many utility classes &amp; widgets. This library project is created to be used in all other projects.
Developers can save lot of time by using these readymade classes in their projects. 

### Features:

1. Utility class for Shared Preferences based on Gson. `PreferencesUtil` makes serializing/deserializing Objects into Shared preferences easy.

* AppLogger - A logger wrapper on top of Microlog4android. Just initialize AppLogger in your Application class using `init` and use the Singleton instance everywhere.
* AppLogger - A logger wrapper on top of Microlog4android. Just initialize AppLogger in your Application class using `init` and use the Singleton instance everywhere. 
Eg: in Application.onCreate() `AppLogger.init(this, "folderinsdcard", AlarmManager.INTERVAL_DAY * 7);`
* WebSession - A wrapper class that uses OkHttp library to make GET/POST class. No need to write boilerplate code.
* ProgressAsyncTask - An AsyncTask with ProgressDialog. This is handly when you want to make Network calls in Async Task and show a interdeterminate dialog to user.
* RemoteImageLoader - Copied the classes from an Open Source project [Used for convenience]
* Simple Crop Image - Copied the classes from an Open Source project [Used for convenience]
* Generic BaseAdapter for ListView - It is cumbersome to always extend BaseAdapter and provide your implementation. BaseAdapter2 solves this problem. 
* ToastUtils - Create normal and centered toast messages easily
* Validator - Dumped classes from an Open Source project
* Handy Widgets - CircleImageView, DateTimePicker, DateTimePickerDialog, MultiSelectionSpinner, RangeSeekBar, RemoteImageView
* Commons-io - Copied all classes from Apache commons-io project. Google Play Store does not allow commons-io APIs citing them as "native" library, hence copied them in the library. 
* LogReaderScreen - This allows you to provide a nice UI for all the logs from text file. All you need to do is pass the Log path and rest is taken care of. The screen shows continuous real-time logs as they are created. 

### Usage
 
Add maven repo in your `build.gradle` file
   
    maven {url 'https://github.com/smargav/android-api-library/raw/maven-repo/dist'}

and add 

   dependencies {
      compile('com.smargav:api:1.0@aar') {
          transitive = true
      }
   }


