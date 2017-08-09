package com.app.qootho;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.qootho.Adapters.HelpDetailAdapter;
import com.app.qootho.Model.HelpModel;
import com.app.qootho.Model.TokenObject;
import com.app.qootho.Model.UserAccount;
import com.app.qootho.Utilities.QoothoDB;
import com.app.qootho.Utilities.SessionManager;

import java.util.ArrayList;
import java.util.Collections;

public class HelpDetailActivity extends AppCompatActivity {


    QoothoDB db = null;
    SessionManager session = null;
    SharedPreferences installPref;
    String username;
    UserAccount user;
    TokenObject token;

    TextView txtBack;
    TextView txtNext;
    TextView title;
    Toolbar toolbar;

    LinearLayout wait_icon;
    ListView listHelp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        ArrayList<HelpModel.HelpSubTitle> object = (ArrayList<HelpModel.HelpSubTitle>) args.getSerializable("help");

        Toast.makeText(HelpDetailActivity.this, object.size() + " ", Toast.LENGTH_LONG).show();


        db = new QoothoDB(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        installPref = getSharedPreferences(SessionManager.PREF_NAME, SessionManager.PRIVATE_MODE);
        username = installPref.getString(SessionManager.KEY_USERNAME, null);
        user = new UserAccount();
        token = new TokenObject();
        user = db.getUserProfile(username);
        token = db.getTokenByUsername(username);


        title = (TextView) toolbar.findViewById(R.id.txtTitle);
        title.setTextSize((float) 16.0);
        title.setText("Help");
        txtBack = (TextView) toolbar.findViewById(R.id.txtBack);
        txtNext = (TextView) toolbar.findViewById(R.id.txtNext);
        txtNext.setVisibility(View.INVISIBLE);
        listHelp = (ListView) findViewById(R.id.listHelpDetail);

        object = sortAndAddSections(object);
        HelpDetailAdapter adapter = new HelpDetailAdapter(HelpDetailActivity.this, object);

        listHelp.setAdapter(adapter);

        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private ArrayList<HelpModel.HelpSubTitle> sortAndAddSections(ArrayList<HelpModel.HelpSubTitle> itemList) {

        ArrayList<HelpModel.HelpSubTitle> tempList = new ArrayList<HelpModel.HelpSubTitle>();
        //First we sort the array
        Collections.sort(itemList);

        //Loops thorugh the list and add a section before each sectioncell start
        String header = "";
        for (int i = 0; i < itemList.size(); i++) {
            for (int j = 0; j < itemList.get(i).getHelpContent().size(); j++) {
                //If it is the start of a new section we create a new listcell and add it to our array
                //getHelpContent().get(position).getDisplayContent()
                if (header != itemList.get(i).getHelpContent().get(j).getDisplayContent()) {
                    HelpModel.HelpSubTitle sectionCell = new HelpModel.HelpSubTitle();
                    sectionCell.setSubTitleContent(itemList.get(i).getHelpContent().get(j).getDisplayContent());
                    tempList.add(sectionCell);
                    header = itemList.get(i).getHelpContent().get(j).getDisplayContent();
                }
                tempList.add(itemList.get(j));
            }
        }

        return tempList;
    }

}
