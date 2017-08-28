package ru.startandroid.earthquakemonitor;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    Elements elements;
    private ArrayList<String> titleList=new ArrayList<String>();
    private ArrayList<LatLng> latLngs=new ArrayList<>();
    private ArrayList<String> magnitude=new ArrayList<>();
    private ArrayList<String> depth=new ArrayList<>();
    private ArrayList<String> time=new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter = null;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView=(ListView)findViewById(R.id.listView);

        Thread thread=new Thread();
        thread.execute();


        arrayAdapter = new ArrayAdapter<String>(this,
                R.layout.list_item,R.id.pro_item, titleList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("latitude",latLngs.get(position).latitude);
                intent.putExtra("longitude",latLngs.get(position).longitude);
                intent.putExtra("title",titleList.get(position));
                startActivity(intent);
            }
        });
    }

    public class Thread extends AsyncTask<String,Void, String>{

        @Override
        protected String doInBackground(String... arg) {

            try {
                Document document = Jsoup.connect("http://zeml.info/online/").get();

                elements= document.select(".table-place");

                for(Element element: elements){
                    titleList.add(element.text());
                }

                Element table = document.select("tbody").first();
                Elements rows = table.select("tr");

                for (int i = 0; i < rows.size(); i++) {
                    Element row = rows.get(i);
                    Elements cols = row.select("td");
                    for(int j=1;j<cols.size();j+=4){
                        Element colTime = cols.get(j);
                        j++;
                        Element colLatitude=cols.get(j);
                        j++;
                        Element colLongitude=cols.get(j);
                        LatLng latLng=new LatLng(Double.parseDouble(colLatitude.text().toString()),
                                Double.parseDouble(colLongitude.text().toString()));
                        time.add(colTime.text().toString());
                        latLngs.add(latLng);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            listView.setAdapter(arrayAdapter);
        }
    }
}
