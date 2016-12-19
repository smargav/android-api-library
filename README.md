A library of many utility classes &amp; widgets. This library project is created to be used in all other projects.
Developers can save lot of time by using these readymade classes in their projects. 

### Features:

1. Utility class for Shared Preferences based on Gson. `PreferencesUtil` makes serializing/deserializing Objects into Shared preferences easy.

* AppLogger - A logger wrapper on top of Microlog4android. Just initialize AppLogger in your Application class using `init` and use the Singleton instance everywhere.
* AppLogger - A logger wrapper on top of Microlog4android. Just initialize AppLogger in your Application class using `init` and use the Singleton instance everywhere. 
Eg: in Application.onCreate() `AppLogger.init(this, "folderinsdcard", AlarmManager.INTERVAL_DAY * 7);`
* WebSession - A wrapper class that uses OkHttp library to make GET/POST class. No need to write boilerplate code.
* ProgressAsyncTask - An AsyncTask with ProgressDialog. This is handly when you want to make Network calls in Async Task and show a interdeterminate dialog to user.
* RemoteImageLoader - Dumped the classes from an Open Source project [Used for convenience]
* Simple Crop Image - Dumped the classes from an Open Source project [Used for convenience]
* Generic BaseAdapter for ListView - It is cumbersome to always extend BaseAdapter and provide your implementation. BaseAdapter2 solves this problem. 
* ToastUtils - Create normal and centered toast messages easily
* Validator - Dumped classes from an Open Source project
* Handy Widgets - CircleImageView, DateTimePicker, DateTimePickerDialog, MultiSelectionSpinner, RangeSeekBar, RemoteImageView



