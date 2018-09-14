package enesates.com.instagramcloneparseexample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    ListView listView; // listView içinde ne görmek istiyorsak onu layout kısmındaki custom_view de yapıyoruz.
    ArrayList<String> userNamesFromParse;
    ArrayList<String> userCommentsFromParse;
    ArrayList<Bitmap> userImagesFromParse;
    PostClass postClass;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // manuel olarak ekledik.
        // Burada hangi menüyü buraya bağlıyacağız onu yazıcaz.

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu); // BUrada main_menu ile menumuzu bağladık.

        return super.onCreateOptionsMenu(menu); // Burda da menuyu döndürdüğünde bağlamış olduk
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Burada da menülerden bir seçenek tıklanırsa ne olacak onu yazıyoruz.

        if(item.getItemId() == R.id.add_post) {
            //intent
            Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.logout) {
        // ParseUser.logOut();// Çıkış yapmak için bunu yazmak yeterlidir ama genede bir hata olursa(internet kopması gibi) diye aşağıdaki şekilde yaptık.
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if (e!=null) {
                        Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(getApplicationContext(),SignUpActivity.class);// Hata yoksa çıkış yaparak singup'a git.
                        startActivity(intent);
                    }
                }
            });

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        listView = findViewById(R.id.listView);

        userNamesFromParse = new ArrayList<>();
        userCommentsFromParse = new ArrayList<>();
        userImagesFromParse = new ArrayList<>();

        postClass = new PostClass(userNamesFromParse, userCommentsFromParse, userImagesFromParse, this);

        listView.setAdapter(postClass); // Böyle yapabiliriz çünkü PostClass sınıfı bir arrayAdapterdan extend ediliyor.

        download();

    }

    public  void download() {
        // Download işlemini parseQUery kullanarak yaparız.
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Posts");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e != null) {
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                } else {

                    if(objects.size() > 0) {

                        for(final ParseObject object : objects) {
                            ParseFile parseFile = (ParseFile) object.get("image"); // Burada önce image' indiriyoruz.
                            parseFile.getDataInBackground(new GetDataCallback() {
                                // getDataInBackground demek arka planda indir anlamına gelir.
                                @Override
                                public void done(byte[] data, ParseException e) {

                                    if(e == null && data != null) {

                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length); //Bu kodda array dizisini decode ederek image'a çeviriyoruz.

                                        userImagesFromParse.add(bitmap);
                                        userNamesFromParse.add(object.getString("username"));
                                        userCommentsFromParse.add(object.getString("comment"));

                                        postClass.notifyDataSetChanged();

                                    }

                                }
                            });
                        }

                    }

                }
            }
        });
    }
}
