package github.jommobile.android.repository

import github.jommobile.android.core.AppExecutors

/**
 * Base repository class.
 *
 * @author MAO Hieng on 1/9/19.
 */
abstract class JomRepository // mContext = application.getApplicationContext();
    (// final Context mContext;
    protected val appExecutors: AppExecutors
)