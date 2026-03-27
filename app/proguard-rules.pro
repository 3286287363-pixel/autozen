# AutoZen ProGuard Rules

# ==================== Kotlin ====================
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }

# ==================== Hilt / Dagger ====================
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }
-keepclassmembers class * {
    @javax.inject.Inject <init>(...);
    @javax.inject.Inject <fields>;
}

# ==================== Room ====================
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public static ** INSTANCE;
}

# ==================== Retrofit / OkHttp ====================
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
# Gson serialization (WeatherResponse)
-keep class com.autozen.network.weather.** { *; }
-keepclassmembers class com.autozen.network.weather.** { *; }

# ==================== Compose ====================
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ==================== AutoZen Models ====================
-keep class com.autozen.obd.model.** { *; }
-keep class com.autozen.data.trip.TripEntity { *; }
-keep class com.autozen.dashboard.model.** { *; }

# ==================== Google Maps ====================
-keep class com.google.android.gms.maps.** { *; }
-keep class com.google.maps.android.** { *; }

# ==================== General ====================
-dontwarn com.google.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
