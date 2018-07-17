/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.okhttp.internalandroidapi;

import com.squareup.okhttp.Cache;

import java.io.File;

/**
 * An interface used to indicate a class can return a {@link CacheHolder} object.
 */
public interface HasCacheHolder {

    /**
     * Returns the {@link CacheHolder} object.
     */
    CacheHolder getCacheHolder();

    /**
     * A holder for an OkHttp internal Cache object. This class exists as an opaque layer over
     * OkHttp internal classes.
     */
    final class CacheHolder {

        private final Cache okHttpCache;

        private CacheHolder(Cache okHttpCache) {
            this.okHttpCache = okHttpCache;
        }

        /**
         * Returns the underlying {@link Cache} object.
         * @hide
         */
        public Cache getCache() {
            return okHttpCache;
        }

        /**
         * Returns a new {@link CacheHolder} containing an OKHttp Cache with the specified settings.
         *
         * @param directory a writable directory
         * @param maxSizeBytes the maximum number of bytes this cache should use to store
         */
        public static CacheHolder create(File directory, long maxSizeBytes) {
            Cache cache = new Cache(directory, maxSizeBytes);
            return new CacheHolder(cache);
        }

        /**
         * Returns true if the arguments supplied would result in an equivalent cache to this one
         * being created if they were passed to {@link #create(File, long)}.
         */
        public boolean isEquivalent(File directory, long maxSizeBytes) {
            return (okHttpCache.getDirectory().equals(directory)
                    && okHttpCache.getMaxSize() == maxSizeBytes
                    && !okHttpCache.isClosed());
        }
    }
}
