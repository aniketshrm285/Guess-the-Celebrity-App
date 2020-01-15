package com.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> urls;
    ArrayList<String> names;
    Button button1 ;
    Button button2 ;
    Button button3 ;
    Button button4 ;
    ImageView imageView;
    int ansButton;
    Random rand = new Random();
    int ansIndex = 0 ;
    int totalImages=43;
    public void downLoadImageFunc(){
        DownloadImage task = new DownloadImage();
        Bitmap image;
        ansIndex = rand.nextInt(totalImages);
        String newUrl = urls.get(ansIndex);
        try {
            image= task.execute(newUrl).get();
            imageView.setImageBitmap(image);
        } catch (Exception e) {
            Log.i("Info","SetImageFailed");
            e.printStackTrace();
        }
        HashMap<Integer, Integer> myMap= new HashMap<>();
        for(int i=0;i<43;i++){
            myMap.put(i,0);
        }
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        myMap.put(ansIndex,1);
        ansButton = rand.nextInt(4);
        for (int i=0;i < 4 ; i++){
            if(i==ansButton){
                indexes.add(ansIndex);
                continue;
            }
            int newIndex = rand.nextInt(totalImages);
            while(myMap.get(newIndex)==1){
                newIndex = rand.nextInt(totalImages);
            }
            indexes.add(newIndex);
            myMap.put(newIndex,1);
        }
        button1.setText(names.get(indexes.get(0)));
        button2.setText(names.get(indexes.get(1)));
        button3.setText(names.get(indexes.get(2)));
        button4.setText(names.get(indexes.get(3)));

    }

    public void selectFunc(View view){
        Button buttonPressed = (Button) view;
        int tag=Integer.parseInt(buttonPressed.getTag().toString());
        if(tag==ansButton){
            Toast.makeText(this, "Correct Answer!", Toast.LENGTH_SHORT).show();
            downLoadImageFunc();
        }
        else{
            Toast.makeText(this, "OOPS! Wrong Answer! Correct Answer is "+ names.get(ansIndex), Toast.LENGTH_SHORT).show();
        }
    }

    public class DownloadHtml extends AsyncTask <String , Void , String >{
        @Override
        protected String doInBackground(String... strings) {
            String result="";
            URL url ;
            HttpURLConnection urlConnection = null ;

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data!=-1){

                    char current = (char) data;

                    result += current;

                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed";
            }
        }
    }

    public class DownloadImage extends AsyncTask<String,Void, Bitmap>{
        @Override
        protected Bitmap doInBackground(String... urls) {
            HttpURLConnection connection = null ;
            try{
                URL url = new URL(urls[0]);

                connection =(HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream in = connection.getInputStream();

                Bitmap bitmap = BitmapFactory.decodeStream(in);

                return bitmap;
            }catch (Exception e){
                e.printStackTrace();
                Log.i("Info","Internet Connection Failed");
                return null;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        DownloadHtml task = new DownloadHtml();
        String result = null;
        try {
            result = task.execute("http://www.posh24.se/kandisar").get();
        }catch (Exception e){
            e.printStackTrace();
        }
        //Log.i("Result",result);
        urls = new ArrayList<String>();
        names = new ArrayList<String>();

        Pattern p = Pattern.compile("img src=\\\"(.*?)\\\" alt");
        Matcher m = p.matcher(result);
        int check=0;
        while (m.find()){
            check++;
            urls.add(m.group(1));
            //Log.i("URL",m.group(1)+ " "+Integer.toString(check));
        }

        p = Pattern.compile("alt=\\\"(.*?)\\\"");
        m= p.matcher(result);
        check=0;
        while (m.find()){
            check++;
            names.add(m.group(1));
            //Log.i("Name",m.group(1)+" "+Integer.toString(check));
        }
        button1 = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);

        downLoadImageFunc();



    }
}
