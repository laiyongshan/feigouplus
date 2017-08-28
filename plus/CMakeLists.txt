# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds it for you.
# Gradle automatically packages shared libraries with your APK.


#file(GLOB native_srcs "src/main/cpp/*.cpp")
#ADD_LIBRARY(native-lib SHARED src/main/cpp/native-lib.cpp    src/main/cpp/Md5.cpp)
file(GLOB native_srcs "src/main/cpp/*.cpp")
add_library( # Sets the name of the library.
            native-lib
           # Md5
           SHARED
             ${native_srcs})


find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )


target_link_libraries( # Specifies the target library.
                       native-lib
                       #libcrypto.a
                       #libssl.a
                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )
