package org.ccd.connection.callbacks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.ccd.model.Place;

public class CallbackListPlace implements Serializable {

    public String status = "";
    public int count = -1;
    public int count_total = -1;
    public int pages = -1;
    public List<Place> places = new ArrayList<>();

}
