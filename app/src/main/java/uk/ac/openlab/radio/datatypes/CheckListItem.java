/*
 * Copyright (c) 2016. Kyle Montague
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uk.ac.openlab.radio.datatypes;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

/**
 * Created by Kyle Montague on 25/02/16.
 */
public class CheckListItem implements Parcelable {

    private boolean isEnabled;
    private String title;
    private int icon;
    private boolean isComplete;

    public CheckListItem(String title, @DrawableRes int icon) {
        this.title = title;
        this.icon = icon;
        this.isEnabled = true;
        this.isComplete = false;
    }

    public CheckListItem(String title, @DrawableRes int icon, boolean isEnabled) {
        this.title = title;
        this.icon = icon;
        this.isEnabled = isEnabled;
        this.isComplete = false;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public String getTitle() {
        return title;
    }

    public @DrawableRes int getIcon() {
        return icon;
    }


    protected CheckListItem(Parcel in) {
        isEnabled = in.readByte() != 0x00;
        title = in.readString();
        icon = in.readInt();
        isComplete = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isEnabled ? 0x01 : 0x00));
        dest.writeString(title);
        dest.writeInt(icon);
        dest.writeByte((byte) (isComplete ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CheckListItem> CREATOR = new Parcelable.Creator<CheckListItem>() {
        @Override
        public CheckListItem createFromParcel(Parcel in) {
            return new CheckListItem(in);
        }

        @Override
        public CheckListItem[] newArray(int size) {
            return new CheckListItem[size];
        }
    };
}
