#!/bin/bash

if [ -z "$ANDROID_BUILD_TOP" ]; then
    echo "Missing environment variables. Did you run build/envsetup.sh and lunch?" 1>&2
    exit 1
fi

CLASSPATH=${ANDROID_HOST_OUT}/framework/currysrc.jar
PROJECT_DIR=${ANDROID_BUILD_TOP}/external/okhttp

cd ${ANDROID_BUILD_TOP}
make -j15 currysrc

function do_transform() {
  local SRC_IN_DIR=$1
  local SRC_OUT_DIR=$2

  if [ ! -d $SRC_OUT_DIR ]; then
    echo ${SRC_OUT_DIR} does not exist >&2
    exit 1
  fi
  rm -rf ${SRC_OUT_DIR}
  mkdir -p ${SRC_OUT_DIR}

  java -cp ${CLASSPATH} com.google.currysrc.aosp.RepackagingTransform \
       --source-dir ${SRC_IN_DIR} \
       --target-dir ${SRC_OUT_DIR} \
       --package-transformation "com.squareup:com.android" \
       --package-transformation "okio:com.android.okhttp.okio" \
       --tab-size 2 \

}

REPACKAGED_DIR=${PROJECT_DIR}/repackaged
for i in android okhttp okhttp-urlconnection okhttp-android-support okio/okio
do
  for s in src/main/java
  do
    IN=${PROJECT_DIR}/$i/$s
    if [ -d $IN ]; then
      OUT=${REPACKAGED_DIR}/$i/$s
      do_transform ${IN} ${OUT}
    fi
  done
done

# Remove an unused source file:
rm ${REPACKAGED_DIR}/okhttp/src/main/java/com/android/okhttp/internal/Platform.java
