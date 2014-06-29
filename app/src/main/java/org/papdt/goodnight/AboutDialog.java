package org.papdt.goodnight;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Xavier on 14-6-29.
 */
public class AboutDialog extends DialogFragment {
    private final static String CC_URL = "http://creativecommons.org/licenses/sampling+/1.0/";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View v = inflater.inflate(R.layout.about_dialog, null);
        TextView tv = (TextView) v.findViewById(R.id.tv_about);

        // Format text
        SpannableStringBuilder sb = new SpannableStringBuilder();
        SpannableString quote = new SpannableString(getString(R.string.the_great_gatsby));
        quote.setSpan(new StyleSpan(Typeface.ITALIC), 0,
                quote.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.append(quote);
        sb.append(getString(R.string.app_name));
        sb.append(String.format("\nver.%s\n\n", getString(R.string.version_name)));
        sb.append(getString(R.string.copyright));
        SpannableString cc_url = new SpannableString(getString(R.string.audio_license));
        cc_url.setSpan(new URLSpan(CC_URL),0,cc_url.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.append(cc_url);

        tv.setText(sb);
        builder.setView(v);

        builder.setTitle(R.string.about);
        builder.setIcon(android.R.drawable.ic_dialog_info);

        return builder.create();
    }
}
