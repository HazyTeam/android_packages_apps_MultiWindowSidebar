package com.zst.app.multiwindowsidebar;

import com.zst.app.multiwindowsidebar.adapter.AppListActivity;
import com.zst.app.multiwindowsidebar.sidebar.SidebarService;

import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;

public class MainActivity extends Activity {
	
	static Handler mHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new Handler();
		
		// Display the fragment as the main content.
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		MainPreference fragment = new MainPreference();
		transaction.replace(android.R.id.content, fragment);
		transaction.commit();
	}
	
	public static class MainPreference extends PreferenceFragment implements
			OnPreferenceClickListener, OnPreferenceChangeListener {
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			getPreferenceManager().setSharedPreferencesName(Common.KEY_PREFERENCE_MAIN);
			addPreferencesFromResource(R.xml.main_pref);
			findPreference(Common.PREF_KEY_TOGGLE_SERVICE).setOnPreferenceClickListener(this);
			findPreference(Common.PREF_KEY_SELECT_APPS).setOnPreferenceClickListener(this);
			findPreference(Common.PREF_KEY_LAUNCH_MODE).setOnPreferenceChangeListener(this);
			findPreference(Common.PREF_KEY_SIDEBAR_POSITION).setOnPreferenceChangeListener(this);
			findPreference(Common.PREF_KEY_TAB_SIZE).setOnPreferenceChangeListener(this);
			findPreference(Common.PREF_KEY_ANIM_TIME).setOnPreferenceChangeListener(this);
		}
		
		@Override
		public boolean onPreferenceClick(Preference p) {
			String k = p.getKey();
			if (k.equals(Common.PREF_KEY_SELECT_APPS)) {
				getActivity().startActivity(new Intent(getActivity(), AppListActivity.class));
				return true;
			} else if (k.equals(Common.PREF_KEY_TOGGLE_SERVICE)) {
				if (SidebarService.isRunning) {
					getActivity().stopService(new Intent(getActivity(), SidebarService.class));
				} else {
					getActivity().startService(new Intent(getActivity(), SidebarService.class));
				}
				return true;
			}
			return false;
		}
		
		@Override
		public boolean onPreferenceChange(Preference pref, Object newValue) {
			String k = pref.getKey();
			if (k.equals(Common.PREF_KEY_LAUNCH_MODE)) {
				Util.refreshService(getActivity());
				
				if (newValue instanceof String) {
					switch (Integer.parseInt((String) newValue)) {
					case IntentUtil.MODE_XHALO_FLOATINGWINDOW:
						if (!Util.isAppInstalled(getActivity().getPackageManager(),
								Common.PKG_XHALOFLOATINGWINDOW)) {
							Util.dialog(getActivity(), R.string.pref_launch_mode_error_xhfw);
						}
						break;
					case IntentUtil.MODE_XMULTI_WINDOW:
						if (!Util.isAppInstalled(getActivity().getPackageManager(),
								Common.PKG_XMULTIWINDOW)) {
							Util.dialog(getActivity(), R.string.pref_launch_mode_error_xmw);
						}
						break;
					}
				}
			} else if (k.equals(Common.PREF_KEY_TAB_SIZE)) {
				Util.refreshService(getActivity());
			} else if (k.equals(Common.PREF_KEY_SIDEBAR_POSITION) ||
					k.equals(Common.PREF_KEY_ANIM_TIME)) {
				Util.resetService(getActivity());
			}
			return true;
		}
	}
}
