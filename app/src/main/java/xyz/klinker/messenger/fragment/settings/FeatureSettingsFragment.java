package xyz.klinker.messenger.fragment.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import xyz.klinker.messenger.R;
import xyz.klinker.messenger.api.implementation.Account;
import xyz.klinker.messenger.api.implementation.ApiUtils;
import xyz.klinker.messenger.data.FeatureFlags;
import xyz.klinker.messenger.data.Settings;

public class FeatureSettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings_features);
        initSecurePrivateConversations();
        initQuickCompose();
        initDelayedSending();
        initCleanupOldMessages();
        initSignature();
    }

    @Override
    public void onStop() {
        super.onStop();
        Settings.get(getActivity()).forceUpdate();
    }

    private void initSecurePrivateConversations() {
        Preference preference = findPreference(getString(R.string.pref_secure_private_conversations));
        preference.setOnPreferenceChangeListener((p, o) -> {
                    boolean secure = (boolean) o;
                    new ApiUtils().updateSecurePrivateConversations(
                            Account.get(getActivity()).accountId, secure);
                    return true;
                });

        if (!FeatureFlags.get(getActivity()).SECURE_PRIVATE) {
            getPreferenceScreen().removePreference(preference);
        }
    }

    private void initQuickCompose() {
        Preference preference = findPreference(getString(R.string.pref_quick_compose));
        preference.setOnPreferenceChangeListener((p, o) -> {
                    boolean quickCompose = (boolean) o;
                    new ApiUtils().updateQuickCompose(
                            Account.get(getActivity()).accountId, quickCompose);
                    return true;
                });

        if (!FeatureFlags.get(getActivity()).QUICK_COMPOSE) {
            getPreferenceScreen().removePreference(preference);
        }
    }

    private void initDelayedSending() {
        Preference preference = findPreference(getString(R.string.pref_delayed_sending));
        preference.setOnPreferenceChangeListener((p, o) -> {
                    String delayedSending = (String) o;
                    new ApiUtils().updateDelayedSending(
                            Account.get(getActivity()).accountId, delayedSending);
                    return true;
                });
    }

    private void initCleanupOldMessages() {
        Preference preference = findPreference(getString(R.string.pref_cleanup_messages));
        preference.setOnPreferenceChangeListener((p, o) -> {
                    String cleanup = (String) o;
                    new ApiUtils().updateCleanupOldMessages(
                            Account.get(getActivity()).accountId, cleanup);
                    return true;
                });

        if (!FeatureFlags.get(getActivity()).CLEANUP_OLD_MESSAGES) {
            getPreferenceScreen().removePreference(preference);
        }
    }

    private void initSignature() {
        findPreference(getString(R.string.pref_signature)).setOnPreferenceClickListener(p -> {
            //noinspection AndroidLintInflateParams
            View layout = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edit_text,
                    null, false);
            final EditText editText = (EditText) layout.findViewById(R.id.edit_text);
            editText.setHint(R.string.signature);
            editText.setText(Settings.get(getActivity()).signature);
            editText.setSelection(editText.getText().length());

            new AlertDialog.Builder(getActivity())
                    .setView(layout)
                    .setPositiveButton(R.string.save, (dialogInterface, i) -> {
                        if (editText.getText().length() > 0) {
                            new ApiUtils().updateSignature(Account.get(getActivity()).accountId,
                                    editText.getText().toString());
                        } else {
                            new ApiUtils().updateSignature(Account.get(getActivity()).accountId, "");
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();

            return false;
        });
    }
}