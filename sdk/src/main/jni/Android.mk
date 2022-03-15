# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# 
#ADD THIS FOR DEBUG STATE ON BUILDERS: -B V=1 APP_OPTIM=debug NDK_DEBUG=1
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
APP_PLATFORM := android-<minSdkVersion>
LOCAL_CFLAGS := -Og
NDK_DEBUG	:= 1
LOCAL_MODULE    := MlinsLocationFinderUtils  
LOCAL_CFLAGS    := -Werror -fexceptions 
LOCAL_SRC_FILES :=  kdtree.cpp LevelIndexObj.cpp MathUtils.cpp Location.cpp AssociativeData.cpp  AssociativeDataSorter.cpp  MatrixBinRep.cpp WlBlip.cpp OptLocFinder.cpp OptFloorSelectorFinder.cpp LocationFinderUtils.cpp FloorSelectorUtils.cpp GisPoint.cpp GisSegment.cpp InstructionBuilder.cpp Instructionobject.cpp Log.cpp NavigationPath.cpp PoiData.cpp PropertyHolder.cpp SwitchFloorObj.cpp GisData.cpp GisLine.cpp aStarPoint.cpp FloorNavigationPath.cpp aStarMath.cpp aStarData.cpp aStarAlgorithm.cpp aStarUtils.cpp Chromosome.cpp GeneticPathFinder.cpp JNIGeneticPathOrderUtils.cpp Beacon.cpp HalfNavLocFinder.cpp ShortPathCalculator.cpp JNIShortPathCalculatorUtils.cpp ConversionUtils.cpp JNIConversionUtils.cpp GeneticOrderBuilder.cpp
LOCAL_LDLIBS    := -llog 
 # commnet above the following for release
# LOCAL_CFLAGS := -Og
# NDK_DEBUG	:= 1  		   
# LOCAL_CFLAGS    := -Werror
include $(BUILD_SHARED_LIBRARY)