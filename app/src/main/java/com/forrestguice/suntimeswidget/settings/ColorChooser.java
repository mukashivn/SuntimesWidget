/**
    Copyright (C) 2017 Forrest Guice
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

package com.forrestguice.suntimeswidget.settings;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.forrestguice.suntimeswidget.themes.WidgetThemeConfigActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

public class ColorChooser implements TextWatcher, View.OnFocusChangeListener
{
    private static final String DIALOGTAG_COLOR = "colorchooser";

    private String chooserID = "0";
    protected ImageButton button;
    protected EditText edit;
    protected TextView label;

    private int color;
    private boolean isRunning = false, isRemoving = false;
    private boolean isCollapsed = false;

    public static final char[] alphabet = {'#', '0', '1', '2', '3', '4', '5', '6', '7','8', '9', 'a', 'A', 'b', 'B', 'c', 'C', 'd', 'D', 'e', 'E', 'f', 'F'};
    protected HashSet<Character> inputSet;

    public ColorChooser(final Context context, TextView txtLabel, EditText editField, ImageButton imgButton, String id)
    {
        init(context, txtLabel, editField, imgButton, id);
    }

    public ColorChooser(final View root, final Context context, int labelID, int editID, int buttonID, String id)
    {
        init( context, (TextView)root.findViewById(labelID), (EditText)root.findViewById(editID), (ImageButton)root.findViewById(buttonID), id );
    }

    private void init(final Context context, TextView txtLabel, EditText editField, ImageButton imgButton, String id)
    {
        chooserID = id;

        label = txtLabel;
        if (label != null)
        {
            label.setOnClickListener( new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onFocusGained(v);
                }
            });
        }

        edit = editField;
        if (edit != null)
        {
            edit.addTextChangedListener(this);
            edit.setOnFocusChangeListener(this);

            edit.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
                {
                    if (actionId == EditorInfo.IME_ACTION_DONE)
                    {
                        changeColor();
                        return true;
                    }
                    return false;
                }
            });

        }

        inputSet = new HashSet<>();
        for (char c : alphabet)
        {
            inputSet.add(c);
        }

        button = imgButton;
        if (button != null)
        {
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (label != null)
                    {
                        label.requestFocus();
                    }
                    showColorPicker(context);
                }
            });
        }
    }

    private final ArrayList<ColorChooser> linked = new ArrayList<ColorChooser>();
    public ArrayList<ColorChooser> getLinked()
    {
        return linked;
    }
    public void link(ColorChooser chooser)
    {
        if (!linked.contains(chooser))
        {
            linked.add(chooser);
        }
    }
    public void unlink(ColorChooser chooser)
    {
        if (linked.contains(chooser))
        {
            linked.remove(chooser);
        }
    }

    /**
     * @return a key that identifies this chooser's value
     */
    public String getID()
    {
        return chooserID;
    }

    /**
     * @return EditText wrapped by chooser
     */
    public EditText getField()
    {
        return edit;
    }

    /**
     * @return Button wrapped by chooser
     */
    public ImageButton getButton()
    {
        return button;
    }

    /**
     * @return TextView wrapped by chooser
     */
    public TextView getLabel()
    {
        return label;
    }

    /**
     * Set the color.
     * @param color color as integer
     */
    public void setColor(int color)
    {
        this.color = color;
        updateViews();
    }

    /**
     * Set the color.
     * @param hexCode hex color code #aarrggbb
     */
    public void setColor(String hexCode)
    {
        this.color = Color.parseColor(hexCode);
        updateViews();
    }

    /**
     * Set the color from provided bundle (using chooserID as a key).
     * @param savedState Bundle
     */
    public void setColor( Bundle savedState )
    {
        setColor(savedState.getInt(chooserID, getColor()));
    }

    /**
     * @return color value
     */
    public int getColor()
    {
        return color;
    }

    /**
     * @param value true expand edit field, false collapse edit field
     */
    public void setCollapsed( boolean value )
    {
        isCollapsed = value;
        updateViews();
    }

    public void setEnabled( boolean value )
    {
        if (label != null)
        {
            label.setEnabled(value);
        }
        if (edit != null)
        {
            edit.setEnabled(value);
        }
        if (button != null)
        {
            button.setEnabled(value);
        }
        if (!value)
        {
            setCollapsed(true);
        }
    }

    private void updateViews()
    {
        if (edit != null)
        {
            edit.setText( String.format("#%08X", color) );
            edit.setVisibility((isCollapsed ? View.GONE : View.VISIBLE));
        }

        if (button != null)
        {
            Drawable d = button.getDrawable();
            if (d != null)
            {
                GradientDrawable g = (GradientDrawable) d.mutate();
                g.setColor(color);
                g.invalidateSelf();
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after)
    {
        isRemoving = count > after;
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int after) {}

    @Override
    public void afterTextChanged(Editable editable)
    {
        if (isRunning || isRemoving)
            return;
        isRunning = true;

        String text = editable.toString();             // should consist of [#][0-9][a-f]
        for (int j=text.length()-1; j>=0; j--)
        {
            if (!inputSet.contains(text.charAt(j)))
            {
                editable.delete(j, j+1);
            }
        }

        text = editable.toString();                   // should start with a #
        int i = text.indexOf('#');
        if (i != -1)
        {
            editable.delete(i, i + 1);
        }
        editable.insert(0, "#");

        if (editable.length() > 8)                   // should be no longer than 8
        {
            editable.delete(9, editable.length());
        }

        text = editable.toString();
        String toCaps = text.toUpperCase(Locale.US);
        editable.clear();
        editable.append(toCaps);

        isRunning = false;
    }

    protected void onColorChanged( int newColor )
    {
        for (ColorChooser chooser : getLinked())
        {
            chooser.setColor(newColor);
        }
        signalOnColorChanged(newColor);
    }
    protected void onFocusGained(View view)
    {
        setCollapsed(false);
        if (edit != null)
        {
            edit.requestFocus();
        }
    }
    protected void onFocusLost(View view)
    {
        setCollapsed(true);
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus)
    {
        if (!hasFocus)
        {
            if (edit != null)
            {
                changeColor();
                onFocusLost(view);
            }
        }
    }

    private void changeColor()
    {
        Editable editable = edit.getText();
        int i = editable.toString().indexOf('#');
        if (i != -1)                    // should start with a #
        {
            editable.delete(i, i + 1);
        }
        editable.insert(0, "#");

        while (editable.length() < 3)   // supply an alpha value (FF)
        {
            editable.insert(1, "F");
        }
        if (editable.length() == 7)
        {
            editable.insert(1, "FF");
        }

        while (editable.length() < 9)   // fill rest with "0"
        {
            editable.append("0");
        }

        //Log.d("DEBUG", "color is " + editable.toString());
        edit.setText(editable);
        edit.setText(editable);
        setColor(editable.toString());
        onColorChanged(getColor());
    }

    private FragmentManager fragmentManager = null;
    public void setFragmentManager( FragmentManager manager )
    {
        fragmentManager = manager;
    }

    private void showColorPicker(Context context)
    {
        ColorDialog colorDialog = new ColorDialog();
        colorDialog.setColor(getColor());
        colorDialog.setColorChangeListener(colorDialogChangeListener);
        if (fragmentManager != null)
        {
            colorDialog.show(fragmentManager, DIALOGTAG_COLOR + "_" + chooserID);
        } else {
            Log.w("showColorPicker", "fragmentManager is null; showing fallback ...");
            Dialog dialog = colorDialog.getDialog();
            dialog.show();
        }
    }

    private final ColorDialog.ColorChangeListener colorDialogChangeListener = new ColorDialog.ColorChangeListener()
    {
        @Override
        public void onColorChanged(int color)
        {
            setColor(color);
            ColorChooser.this.onColorChanged(getColor());
        }
    };

    public void onResume()
    {
        if (fragmentManager != null)
        {
            ColorDialog colorDialog = (ColorDialog) fragmentManager.findFragmentByTag(DIALOGTAG_COLOR + "_" + chooserID);
            if (colorDialog != null)
            {
                colorDialog.setColorChangeListener(colorDialogChangeListener);
            }
        }
    }

    public static abstract class OnColorChangedListener
    {
        public void onColorChanged(int newColor ) {}
    }

    protected OnColorChangedListener listener = null;
    public void setOnColorChangedListener(OnColorChangedListener listener )
    {
        this.listener = listener;
    }
    public void clearOnColorChangedListener()
    {
        this.listener = null;
    }
    private void signalOnColorChanged(int newColor)
    {
        if (listener != null)
        {
            listener.onColorChanged(newColor);
        }
    }
}