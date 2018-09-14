package enesates.com.instagramcloneparseexample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UploadActivity extends AppCompatActivity {

    // Kullanıcının istediği resim ve yorum ekleyebileciği yer.

    EditText commentText;
    ImageView imageView;
    Bitmap chosenImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        commentText = findViewById(R.id.upload_activity_comment_text);
        imageView = findViewById(R.id.upload_activity_imageView);
    }

    public void upload (View view) {

        String comment = commentText.getText().toString();

        // Aşağıda ParseFile'ı kullanabilmek için BytrArrayoututStream dönüştürmesini ordanda byte[] array döşümünü yaptık.
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        chosenImage.compress(Bitmap.CompressFormat.PNG,50,byteArrayOutputStream);// compress yapmayı zip yapmak gibi düşünebilirsin.
        byte[] bytes = byteArrayOutputStream.toByteArray();

        // Imaj dosyası eklerken ParseFıle denen bir objeye dönüştürmeliyiz.
        ParseFile parseFile = new ParseFile("image.png", bytes);

        ParseObject object = new ParseObject("Posts"); // Parse server'ında bu isimde bir class oluşturduk.
        object.put("image", parseFile); // Bunu yapabilmek için  yukarıda dönüşüm yaptırdık
        object.put("comment", comment);
        object.put("username", ParseUser.getCurrentUser().getUsername());
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Post Uploaded !", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), FeedActivity.class); // FeedActivity'e giderek paylaştığımız doyayı göreceğiz.
                    startActivity(intent);
                }
            }
        });

    }

    public void chooseImage(View view) {
        // Burada kullanıcının galerisine girip foto alıcaz
        // Önce izin isteyeceğiz.

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            // Eğer izin yoksa burada isteyeceğiz.
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 2); // request koda ne yazdığımız önemli değil değil ileride kontrol yaparken hatırlasak yeter.
        } else {
            // Aşağıda ACTION_PICK almak demek MediaStore.Images.Media.EXTERNAL_CONTENT_URI da alacağımız yeri söyler.
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1); // Bir sonuç için aktiviteyi başlattık.
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Bu chooseImage metodundaki if  yani izin sonucunda yapacağımız işlemleri yazıcaz.

        if(requestCode == 2) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Eğer izin verildiyse demek
                // grantResults[0] ilk eleman, zaten tek şey istedik o da izin anlaına geliyor
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Bu da chooseImage metodunda else yani startActivityForResult yapıldığında olacak olanları yazıyoruz.
        //Kullanıcı resmi seçtiyse olacak olanları yazıyoruz.

        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            // resultCode da seçildiyse dedik çünkü kullanıcı iptalde yapabilir yani RESULT_OK yerine RESULT_CANCELED diye kod var
            Uri uri = data.getData();
            try {
                chosenImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                imageView.setImageBitmap(chosenImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
