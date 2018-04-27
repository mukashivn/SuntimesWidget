/**
    Copyright (C) 2018 Forrest Guice
    This file is part of SuntimesWidget.

    SuntimesWidget is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SuntimesWidget is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SuntimesWidget.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.forrestguice.suntimeswidget.calendar;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;

import android.content.Context;
import android.content.SyncResult;

import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;

public class SuntimesSyncAdapter extends AbstractThreadedSyncAdapter
{
    public static final String ACCOUNT_NAME = "Suntimes";

    public SuntimesSyncAdapter(Context context, boolean autoInitialize)
    {
        super(context, autoInitialize);
    }

    public SuntimesSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs)
    {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult)
    {
        // EMPTY
    }

    @TargetApi(15)
    public static Uri asSyncAdapter(Uri uri)
    {
        return uri.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL).build();
    }
}
