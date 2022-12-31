package com.ahmetardakavakci.turkeylandmarks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import com.ahmetardakavakci.turkeylandmarks.databinding.ActivityMainBinding;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Landmark> landmarkList;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        landmarkList = new ArrayList<>();

        Landmark kopru = new Landmark("Boğaz Köprüsü", "İstanbul'un en çok bilinen kent simgesi.", R.drawable.bogazkoprusu);
        Landmark dinocan = new Landmark("Dinocan", "Ankara'da bulunan tarihi bir eser.", R.drawable.dinocan);
        Landmark galata = new Landmark("Galata Kulesi", "İstanbul'un simgesi hâline gelmiş bir kent simgesi.", R.drawable.galata);
        Landmark kale = new Landmark("Kandiber Kalesi", "Türkiye'nin en güzel ilçesinin ortasında bulunan bir kale.", R.drawable.kandiberkalesi);

        landmarkList.add(kopru);
        landmarkList.add(dinocan);
        landmarkList.add(galata);
        landmarkList.add(kale);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(binding.recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        LandmarkAdapter landmarkAdapter = new LandmarkAdapter(landmarkList);
        binding.recyclerView.setAdapter(landmarkAdapter);

        /* This was for ListView

        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,
                landmarksList.stream().map(landmark -> landmark.name).collect(Collectors.toList())
        );

        binding.listView.setAdapter(arrayAdapter);
        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this,DetailsActivity.class);
                intent.putExtra("landmark", landmarksList.get(i));

                startActivity(intent);
            }
        });

        */

    }
}