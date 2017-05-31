package com.example.tryston.runwithfriends;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Tryston on 5/24/2017.
 */

public class SavedRoutesFragment extends ListFragment {

    public ArrayList<String> routes;
    private RouteSelection callback;

    public SavedRoutesFragment()
    {
        routes = new ArrayList<>();
    }

    public void SetRouteNames(ArrayList<String> routeNames)
    {
        routes = new ArrayList<>();
        CustomAdapter adapter = new CustomAdapter(getActivity(),R.layout.array_adapter_custom, routes);
        setListAdapter(adapter);
        for(int i = 0; i < routeNames.size(); ++i)
        {
            routes.add(routeNames.get(i));
        }
    }

    public void SetRouteSelection(RouteSelection callback)
    {
        this.callback = callback;
    }

    public void Selected(int i)
    {
        callback.Selected(i);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.selected_routes_list_fragment, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CustomAdapter adapter = new CustomAdapter(getActivity(),R.layout.array_adapter_custom, routes);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Selected(position);
            }
        });
    }
}
