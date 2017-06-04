package se.mah.af6851.project2;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectFragment extends Fragment {
    private Button btnConnect, btnRefresh;
    private ListView lvGroups;
    private MainActivity main;
    private EditText etUserName, etGroupName;

    public ConnectFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_connect, container, false);
        main = ((MainActivity) getActivity());
        btnConnect = (Button) view.findViewById(R.id.btnConnect);
        btnRefresh = (Button) view.findViewById(R.id.btnRefresh);
        lvGroups = (ListView) view.findViewById(R.id.lvGroups);
        etUserName = (EditText) view.findViewById(R.id.etUserName);
        etGroupName = (EditText) view.findViewById(R.id.etGroupName);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main.registerGroup();
                main.swapToMap();
            }
        });
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main.requestGroups();
                main.requestMembers();
//                main.sendPosition();
            }
        });

        lvGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String value = "" + adapterView.getItemAtPosition(position);
                etGroupName.setText(value);
            }
        });
        return view;
    }

    public void refreshGroups(ArrayList<String> groups) {
        lvGroups.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, groups));
    }

    public String getUsername(){
        String userName = etUserName.getText().toString();
        return userName;
    }
    public String getGroupname(){
        String groupname = etGroupName.getText().toString();
        return groupname;
    }
}

