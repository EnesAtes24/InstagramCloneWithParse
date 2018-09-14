package enesates.com.instagramcloneparseexample;

import android.app.Application;

import com.parse.Parse;

public class ParseStarterClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG); // Logcat'te ne yazdıracağımızı level'ını belirler.Değiştirebilirsin.

        //Asıl işlem aşağıdakii gibi inialize etmek
        // Tabi ki aşağıda yazdığımız kod Gradle da yazdığımız versiyona uygun olan kod başka versiyonlara nasıl kullanılacağı parse4app'in github'ında anlatılıyor.
        // Aşağıda yazdığımız kodu Manifestte tanımlamazsak hiç bir anlaöı olmaz. Manifestte android:name=".ParseStarterClass" olarak tanımladık.
        Parse.initialize(new Parse.Configuration.Builder(this)
        .applicationId("RdrStv4lkoqbxCHOg8tTJE0utjAn7bT6Tujb9S88")
        .clientKey("QKnUTapYxIFElmyQJKMmofGCrJ6cSN33HxpVIepl")
        .server("https://parseapi.back4app.com/")
        .build()
        );


    }
}
