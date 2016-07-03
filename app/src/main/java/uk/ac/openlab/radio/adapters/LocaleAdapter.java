package uk.ac.openlab.radio.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.SpinnerAdapter;

import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

/**
 * Created by kylemontague on 12/04/16.
 */
public class LocaleAdapter implements SpinnerAdapter {

    Context context;
    ArrayList<Locale> items;
    ArrayList<Integer> codes;
    ArrayList<DataSetObserver> dataSetObservers;
    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    public LocaleAdapter(Context context){
        super();

        items = new ArrayList<>();
        codes = new ArrayList<>();
        this.context = context;
        for(Locale locale: Locale.getAvailableLocales()){
            if(locale.getCountry().length() > 0) {
                int code = phoneUtil.getCountryCodeForRegion(locale.getCountry());
//                if (!codes.contains(code)) {
                    items.add(locale);
                    codes.add(code);
//                }
            }
        }

        Collections.sort(items,new LocaleComparator());
        dataSetObservers = new ArrayList<>();

    }


    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        dataSetObservers.add(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        dataSetObservers.remove(observer);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        CheckedTextView textView = (CheckedTextView)rowView.findViewById(android.R.id.text1);
        Locale l= (Locale)getItem(position);
        int countryCode = phoneUtil.getCountryCodeForRegion(l.getCountry());
        textView.setText("+"+countryCode);

        return rowView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        CheckedTextView textView = (CheckedTextView)rowView.findViewById(android.R.id.text1);

        Locale l= (Locale)getItem(position);
        int countryCode = phoneUtil.getCountryCodeForRegion(l.getCountry());
        textView.setText(l.getDisplayName()+" ("+countryCode+")");

        return rowView;
    }


    public class LocaleComparator implements Comparator<Locale>
    {
        public int compare(Locale left, Locale right) {
            return left.getDisplayCountry().compareTo(right.getDisplayCountry());
        }
    }
}
