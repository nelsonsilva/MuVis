/*
* The GPLv3 licence :
* -----------------
* Copyright (c) 2009 Ricardo Dias
*
* This file is part of MuVis.
*
* MuVis is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* MuVis is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with MuVis.  If not, see <http://www.gnu.org/licenses/>.
 */
package muvis.view;

import java.util.ArrayList;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import muvis.util.Observable;
import muvis.util.Observer;

/**
 * Class that implements a listener for a JTextField
 * @author Ricardo
 */
public class TextFieldListener implements DocumentListener, Observable {

    private ArrayList<Observer> observers;
    private JTextField textField;

    public TextFieldListener(JTextField textField) {
        observers = new ArrayList<Observer>();
        this.textField = textField;
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        updateObservers();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        updateObservers();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updateObservers();
    }

    @Override
    public void registerObserver(Observer obs) {
        observers.add(obs);
    }

    @Override
    public void unregisterObserver(Observer obs) {
        observers.remove(obs);
    }

    @Override
    public void updateObservers() {
        for (Observer obs : observers) {
            obs.update(this, textField.getText());
        }
    }
}
