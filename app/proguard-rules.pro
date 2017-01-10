-keepattributes SourceFile,LineNumberTable
-keep class com.jachness.blockcalls.** { *; }
-keepclassmembers class ** {
    public void onEvent*(**);
}
-keep public class  **.ITelephony {
  *;
}