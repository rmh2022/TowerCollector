/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package info.zamojski.soft.towercollector.providers.preferences;

import info.zamojski.soft.towercollector.MyApplication;
import timber.log.Timber;

import org.acra.ACRA;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;

abstract class PreferenceProviderBase<T> {

    protected Context context;

    public PreferenceProviderBase(Context context) {
        this.context = context;
    }

    public T getPreference(@StringRes int valueKey, int defaultValueKey) {
        T value;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        T defaultValue = getPreferenceDefaultValue(defaultValueKey);
        try {
            value = getPreferenceValue(prefs, valueKey, defaultValue);
            Timber.d("getPreference(): Preference `%s` loaded with value `%s`", context.getString(valueKey), value);
        } catch (ClassCastException ex) {
            Timber.e(ex, "getPreference(): Error while loading preference `%s`, restoring default", context.getString(valueKey));
            MyApplication.getAnalytics().sendException(ex, Boolean.FALSE);
            ACRA.getErrorReporter().handleSilentException(ex);
            value = defaultValue;
            SharedPreferences.Editor editor = prefs.edit();
            setPreferenceValue(editor, valueKey, defaultValue);
            editor.commit();
        }
        return value;
    }

    public void setPreference(@StringRes int valueKey, T value) {
        Timber.d("setPreference(): Preference `%s` value set to `%s`", context.getString(valueKey), value);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        setPreferenceValue(editor, valueKey, value);
        editor.commit();
    }

    abstract T getPreferenceDefaultValue(int defaultValueKey);

    abstract T getPreferenceValue(SharedPreferences prefs, @StringRes int valueKey, T defaultValue);

    abstract void setPreferenceValue(SharedPreferences.Editor editor, @StringRes int valueKey, T value);
}
