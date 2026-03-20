# kotlinx-serialization
-keepattributes *Annotation*, InnerClasses, Signature, Exceptions, SourceFile, LineNumberTable
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}
-keepclassmembers class * {
    @kotlinx.serialization.SerialName *;
}
-keepclassmembers class **$serializer {
    public static final **$serializer INSTANCE;
}

# Keep the serializer's descriptor
-keepclassmembers class * implements kotlinx.serialization.KSerializer {
    public kotlinx.serialization.descriptors.SerialDescriptor getDescriptor();
}
