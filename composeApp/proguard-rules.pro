# Room rules
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>(...);
}
-keep class * extends androidx.room.RoomDatabase
-keep class androidx.room.Room
-keep class androidx.room.util.TableInfo
-keep class androidx.room.util.TableInfo$Column
-keep class androidx.room.util.TableInfo$ForeignKey
-keep class androidx.room.util.TableInfo$Index
-keep class * extends androidx.room.Entity
-keep class * extends androidx.room.Dao

# Kotlin Serialization rules
-keepclassmembers class ** {
    *** Companion;
}
-keepclasseswithmembers class ** {
    *** serializer(...);
}
-keepattributes *Annotation*, InnerClasses, EnclosingMethod, Signature, Exceptions

# Compose rules
-keepclassmembers class androidx.compose.runtime.Recomposer { *; }
-keep class androidx.compose.runtime.CompositionImpl { *; }
-keep class androidx.compose.ui.platform.** { *; }

# Keep Gson
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.turnforge.** { *; }
