package com.openclassrooms.realestatemanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.modele.RealEstate;
import com.openclassrooms.realestatemanager.utils.Adaptateur;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResultsActivity extends AppCompatActivity {
    private static List<RealEstate> resultResearchRealEstate = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        resultResearchRealEstate = grabEstatFromSearchActivity();
        deployRecyclerView();
    }

    @SuppressWarnings("unchecked")
    private List<RealEstate> grabEstatFromSearchActivity() {
        List<RealEstate> estates = null;
        Intent intent = ResultsActivity.this.getIntent();
        Bundle extra = intent.getExtras();
        if (extra != null) {
            estates = (List<RealEstate>) extra.getSerializable("RealEstate");
        }
        return estates;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void deployRecyclerView() {
        Adaptateur adapter = new Adaptateur(resultResearchRealEstate, false, null,this);
        RecyclerView recyclerView = findViewById(R.id.result_RecyclerSearch);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ResultsActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.iconeresult, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.icone_check:
                goToMainActivity();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
