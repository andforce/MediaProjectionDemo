cmake_minimum_required(VERSION 3.22.1)

#add_definitions("-DNO_CRYPTO")
include_directories(${CMAKE_SOURCE_DIR})

#native-lib
file(GLOB PROJECT_SOURCES "${CMAKE_SOURCE_DIR}/*.c")

add_library(rtmp-lib
        SHARED
        rtmp-hanlde.cpp
        ${PROJECT_SOURCES}
        )

find_library( # Sets the name of the path variable.
        log-lib
        log)

target_link_libraries( # Specifies the target library.
        rtmp-lib
        ${log-lib})
