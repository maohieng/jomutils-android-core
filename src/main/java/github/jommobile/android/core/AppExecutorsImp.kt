/*
 * Copyright (C) 2017 The Android Open Source Project
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
package github.jommobile.android.core

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Global executor pools for the whole application.
 *
 *
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 */
internal class AppExecutorsImp private constructor(
    // It should be singleton to not depends on the Application context.
    //    private static class LazyHolder {
    //        static final AppExecutorsImp INSTANCE = new AppExecutorsImp();
    //    }
    //
    //    public static AppExecutorsImp getInstance() {
    //        return LazyHolder.INSTANCE;
    //    }
    private val mDiskIO: Executor,
    private val mNetworkIO: Executor,
    private val mMainThread: Executor
) : AppExecutors {

    constructor() : this(
        Executors.newSingleThreadExecutor(),
        Executors.newFixedThreadPool(3),
        MainThreadExecutor()
    ) {
    }

    override fun diskIO(): Executor {
        return mDiskIO
    }

    override fun networkIO(): Executor {
        return mNetworkIO
    }

    override fun mainThread(): Executor {
        return mMainThread
    }

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler =
            Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }

}