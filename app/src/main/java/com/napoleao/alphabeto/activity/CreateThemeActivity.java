package com.napoleao.alphabeto.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputLayout;
import com.napoleao.alphabeto.R;
import com.napoleao.alphabeto.activity.util.ImageLoadUtil;
import com.napoleao.alphabeto.adapter.CreateWordAdapter;
import com.napoleao.alphabeto.fragment.SearchFragment;
import com.napoleao.alphabeto.helper.dao.ChallengesDAO;
import com.napoleao.alphabeto.helper.dao.TemasDAO;
import com.napoleao.alphabeto.model.Challenge;
import com.napoleao.alphabeto.model.Tema;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class responsible for managing the theme creation screen.
 */
public class CreateThemeActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private GridLayoutManager layManager;

    public Tema theme;

    public TemasDAO themeDAO;
    public ChallengesDAO challengesDAO;

    private TextInputLayout tlIdTheme;
    private String urlImageTheme;
    private ImageView imageTheme;

    private boolean editMode = false;
    private Long editThemeID;

    private TextInputLayout tlIdWord;
    private String urlImageWord;
    private ImageView imageWord;

    public boolean editWordMode;
    public Challenge editWord;
    public List<Long> wordsID_Rem;
    public List<Challenge> words_Add;

    public HashMap<Object, String> imagePathToObjectMap;
    public String imageSavePath;

    /**
     * Method responsible for initializing the screen components.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.wordsID_Rem = new ArrayList<Long>();
        this.words_Add = new ArrayList<Challenge>();
        this.imagePathToObjectMap = new HashMap<Object, String>();

        this.imageSavePath = getFilesDir().getAbsolutePath() + File.separator + "images";

        this.theme = new Tema();
        this.editWord = new Challenge();

        this.themeDAO = TemasDAO.getInstance(getApplicationContext());
        this.challengesDAO = ChallengesDAO.getInstance(getApplicationContext());

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            Object editVal = intent.getSerializableExtra("editMode");
            if (editVal != null) {
                editMode = (boolean) editVal;
            }
            Object themeIDVal = intent.getSerializableExtra("themeID");
            if (themeIDVal != null) {
                editThemeID = (Long) themeIDVal;
            }
        }

        setContentView(R.layout.activity_create);

        tlIdWord = findViewById(R.id.tlIdPalavra);
        imageWord = findViewById(R.id.imageView7);

        tlIdTheme = findViewById(R.id.tlIdTheme2);
        imageTheme = findViewById(R.id.imageView72);

        recyclerView = findViewById(R.id.palavrasRows);
        layManager = new GridLayoutManager(getApplicationContext(), 2);

        findViewById(R.id.areaImagemTema).setOnClickListener(v -> {
            showOptionsForSelectImage(1);
        });

        findViewById(R.id.areaImagemPalavra).setOnClickListener(v -> {
            showOptionsForSelectImage(2);
        });

        findViewById(R.id.buttonAddPalavra).setOnClickListener(v -> {
            String palavra = tlIdWord.getEditText().getText().toString();

            if (palavra == null || palavra.length() == 0) {
                Toast.makeText(getApplicationContext(), "Insira uma Palavra", Toast.LENGTH_LONG).show();
                return;
            }
            if (urlImageWord == null || urlImageWord.length() == 0) {
                Toast.makeText(getApplicationContext(), "Insira uma Imagem para a palavra", Toast.LENGTH_LONG).show();
                return;
            }

            if (this.editWordMode) {
                editWord.setImageUrl(urlImageWord);
                editWord.setWord(palavra.trim());
                updateEditModeWord(false);
                recyclerView.getAdapter().notifyItemChanged(theme.getChallenges().indexOf(editWord));
                Toast.makeText(getApplicationContext(), "Palavra \"" + editWord.getWord() + "\" foi atualizada.", Toast.LENGTH_LONG).show();
            } else {
                Challenge palavaNova = new Challenge();
                palavaNova.setWord(palavra.trim());
                palavaNova.setImageUrl(urlImageWord);

                theme.getChallenges().add(palavaNova);
                this.words_Add.add(palavaNova);
                recyclerView.getAdapter().notifyItemInserted(theme.getChallenges().indexOf(palavaNova));
                Toast.makeText(getApplicationContext(), "Palavra \"" + palavaNova.getWord() + "\" foi adicionada.", Toast.LENGTH_LONG).show();
            }
            tlIdWord.getEditText().setText(null);
            urlImageWord = null;
            updateWordInfo();
        });

        findViewById(R.id.buttonAddPalavra2).setOnClickListener(v -> {
            updateEditModeWord(false);
        });

        if (this.editMode) {
            // Apply the title for edit mode
            TextView title = findViewById(R.id.textView);
            title.setText("Editar Tema");

            // recover theme from database
            this.theme = this.themeDAO.get(editThemeID);
            List<Challenge> storedChallenges = this.challengesDAO.findById(this.theme.getId());
            if (storedChallenges != null) {
                this.theme.setChallenges(storedChallenges);
            }

            //Log.i("", "Edit tema: "+ this.tema.toString());
        }

        updateThemeInfo();

        fillRecycleView();
    }

    /**
     * Action for Show alert option of select image for theme or word
     *
     * @param TAG
     */
    public void showOptionsForSelectImage(int TAG) {

        AlertDialog alertDialog = new AlertDialog.Builder(CreateThemeActivity.this).create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.show();

        Window win = alertDialog.getWindow();
        win.setContentView(R.layout.alerta_imagem_options);

        win.setBackgroundDrawableResource(R.drawable.background_gradient);

        View web_btn = (View) win.findViewById(R.id.areaWeb);
        web_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSearchForImage(TAG);
                alertDialog.dismiss();
            }
        });

        View cam_btn = (View) win.findViewById(R.id.areaCamera);
        cam_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(CreateThemeActivity.this)
                        .cameraOnly()
                        .crop()
                        .compress(420)
                        .start(TAG);
                alertDialog.dismiss();
            }
        });

        View biblioteca_btn = (View) win.findViewById(R.id.areaBiblioteca);
        biblioteca_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(CreateThemeActivity.this)
                        .galleryOnly()
                        .crop()
                        .compress(420)
                        .start(TAG);
                alertDialog.dismiss();
            }
        });

        View close_btn = (View) win.findViewById(R.id.areaClose);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    /**
     * Load name and open fragment for search image result
     *
     * @param TAG
     */
    public void loadSearchForImage(int TAG) {
        String nomeParaBuscar = null;

        TextInputLayout textIn = TAG == 1 ? this.tlIdTheme : this.tlIdWord;
        if (textIn.getEditText().getText() != null && textIn.getEditText().getText().toString().length() > 0) {
            nomeParaBuscar = textIn.getEditText().getText().toString();
        }

        if (nomeParaBuscar != null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment searchF = new SearchFragment(nomeParaBuscar, TAG);
            ft.replace(R.id.frameAuxPhotoFragment, searchF);
            ft.addToBackStack(null);
            ft.commit();
        } else {
            Toast.makeText(getApplicationContext(), "Preencha o campo para pesquisar imagens.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Action for update current theme info into views
     */
    public void updateThemeInfo() {
        if (this.theme != null) {
            tlIdTheme.getEditText().setText(this.theme.getNomeImagem());
            urlImageTheme = this.theme.getImageUrl();
            if (urlImageTheme == null) {
                imageTheme.setImageResource(android.R.drawable.ic_menu_gallery);
            } else {
                updateImageTheme();
            }
        }
    }

    /**
     * Action for update current word info into views
     */
    public void updateWordInfo() {
        if (urlImageWord == null) {
            imageWord.setImageResource(android.R.drawable.ic_menu_gallery);
        } else {
            updateImageWord();
        }
    }

    /**
     * Action for setup the current edited word
     *
     * @param challenge
     */
    public void editWord(Challenge challenge) {
        tlIdWord.requestFocus();

        editWord = challenge;
        tlIdWord.getEditText().setText(challenge.getWord());
        urlImageWord = challenge.getImageUrl();
        updateEditModeWord(true);
    }

    /**
     * Update the current mode of edition of word between edit or create
     *
     * @param edit
     */
    public void updateEditModeWord(boolean edit) {
        this.editWordMode = edit;

        if (this.editWordMode) {
            Button addBt2 = findViewById(R.id.buttonAddPalavra2);
            addBt2.setVisibility(View.GONE);
            addBt2.setVisibility(View.VISIBLE);
            Button addBt = findViewById(R.id.buttonAddPalavra);
            addBt.setText("OK");
            addBt.setVisibility(View.GONE);
            addBt.setVisibility(View.VISIBLE);
        } else {
            Button addBt2 = findViewById(R.id.buttonAddPalavra2);
            addBt2.setVisibility(View.GONE);
            Button addBt = findViewById(R.id.buttonAddPalavra);
            addBt.setText("Adicionar Palavra");
            addBt.setVisibility(View.GONE);
            addBt.setVisibility(View.VISIBLE);

            tlIdWord.getEditText().setText(null);
            urlImageWord = null;
        }
        updateWordInfo();
    }

    /**
     * Action for remove an word
     *
     * @param challenge
     */
    public void removeWord(Challenge challenge) {
        this.theme.getChallenges().remove(challenge);
        this.words_Add.remove(challenge);
        if (challenge.getId() != null) {
            wordsID_Rem.add(challenge.getId());
        }
        if (this.editWordMode && challenge == this.editWord) {
            updateEditModeWord(false);
        }
        Toast.makeText(getApplicationContext(), "Palavra \"" + challenge.getWord() + "\" foi removida", Toast.LENGTH_LONG).show();
    }

    /**
     * Function for copy file
     *
     * @param src
     * @param dst
     * @throws IOException
     */
    public static void copyFile(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    /**
     * Get image of an object at runtime
     *
     * @param object
     * @return
     */
    public String getImagePathFromObject(Object object) {
        String path = imagePathToObjectMap.get(object);
        return path;
    }

    /**
     * Save temp image to cache dir
     *
     * @param image
     * @param object
     */
    public void setImageToObject(File image, Object object) {
        if (object == null || image == null) {
            return;
        }
        String imagePathTemp = null;
        try {
            File outputDir = getApplicationContext().getCacheDir();
            File outputFile = File.createTempFile("image", ".jpg", outputDir);
            imagePathTemp = outputFile.getPath();
            copyFile(image, outputFile);
            // delete image in old app file
            image.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (imagePathTemp != null) {
            imagePathToObjectMap.put(object, imagePathTemp);
        }
    }

    /**
     * Save image to app files
     *
     * @param pathTempImage
     * @return
     */
    public String saveImageToFiles(String pathTempImage) {
        String pathRet = null;
        if (pathTempImage != null) {
            if (pathTempImage.startsWith(this.imageSavePath)) {
                return pathTempImage;
            }
            try {
                File dirPath = new File(this.imageSavePath);
                if (!dirPath.exists()) {
                    dirPath.mkdirs();
                }
                File imagePath = File.createTempFile("image", ".jpg", dirPath);
                File imageFileTemp = new File(pathTempImage);
                copyFile(imageFileTemp, imagePath);
                // delete temp image cache dir
                imageFileTemp.delete();
                pathRet = imagePath.getPath();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return pathRet;
    }

    /**
     * Save button action
     *
     * @param v
     */
    public void saveChanges(View v) {
        String temaNome = tlIdTheme.getEditText().getText().toString();

        if (temaNome == null || temaNome.length() == 0) {
            Toast.makeText(getApplicationContext(), "Insira um nome para o Tema", Toast.LENGTH_LONG).show();
            return;
        }

        if (urlImageTheme == null || urlImageTheme.length() == 0) {
            Toast.makeText(getApplicationContext(), "Insira uma Imagem para o Tema", Toast.LENGTH_LONG).show();
            return;
        }

        if (theme.getChallenges().size() == 0) {
            Toast.makeText(getApplicationContext(), "Adicione algumas palavras ao Tema", Toast.LENGTH_LONG).show();
            return;
        }

        v.setEnabled(false);

        saveActualTheme();

        backToHome();
    }

    /**
     * Save image at runtime
     *
     * @param object
     */
    public void saveImageToObject(Object object) {
        String origImagePath = null;
        String newImagePath = null;
        if (object != null) {
            if (object instanceof Tema) {
                Long id = ((Tema) object).getId();
                if (id != null) {
                    Tema temaGet = themeDAO.get(id);
                    origImagePath = temaGet.getImageUrl();
                }
                String ImagePath = ((Tema) object).getImageUrl();
                if (ImagePath != null && ImagePath.startsWith("/")) {
                    newImagePath = saveImageToFiles(ImagePath);
                    ((Tema) object).setImageUrl(newImagePath);
                }
            } else if (object instanceof Challenge) {
                Long id = ((Challenge) object).getId();
                if (id != null) {
                    Challenge palavraGet = challengesDAO.get(id);
                    origImagePath = palavraGet.getImageUrl();
                }
                String ImagePath = ((Challenge) object).getImageUrl();
                if (ImagePath != null && ImagePath.startsWith("/")) {
                    newImagePath = saveImageToFiles(ImagePath);
                    ((Challenge) object).setImageUrl(newImagePath);
                }
            }
        }
        if (origImagePath != null && newImagePath != null && !origImagePath.equals(newImagePath)) {
            File oldImage = new File(origImagePath);
            oldImage.delete();
        }
    }

    /**
     * Action for proceed to save the actual changes of theme in local database
     */
    public void saveActualTheme() {
        this.theme.setNomeImagem(tlIdTheme.getEditText().getText().toString().trim());
        this.theme.setImageUrl(urlImageTheme);

        if (this.editMode) {

            // atualizar theme
            saveImageToObject(this.theme);
            this.themeDAO.update(this.theme);

            // adicionar palavras
            for (Challenge palavraNow : this.words_Add) {
                saveImageToObject(palavraNow);
            }
            for (Challenge c : this.words_Add) {
                c.setIdTema(theme.getId());
                challengesDAO.save(c);
            }

            //atualizar palavras
            for (Challenge palavraNow : this.theme.getChallenges()) {
                saveImageToObject(palavraNow);
                if (palavraNow.getId() == null) {
                    continue;
                }
                challengesDAO.update(palavraNow);
            }

            //remover palavras
            for (Long palavraIDNow : this.wordsID_Rem) {
                challengesDAO.deleteById(palavraIDNow);
            }

            Log.i("", "Alterações do Tema Salva com sucesso!");
            Toast.makeText(CreateThemeActivity.this, String.format("Alterações do Tema '%s' Salva com Sucesso!", this.theme.getNomeImagem()), Toast.LENGTH_SHORT).show();
        } else {
            // save image to app files
            saveImageToObject(this.theme);
            for (Challenge palavraNow : this.theme.getChallenges()) {
                saveImageToObject(palavraNow);
            }
            if (themeDAO.save(theme)) {
                for (Challenge c : theme.getChallenges()) {
                    c.setIdTema(theme.getId());
                    challengesDAO.save(c);
                }
                Toast.makeText(CreateThemeActivity.this, String.format("Tema '%s' Salvo com Sucesso!", this.theme.getNomeImagem()), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Ops! Esse tema já foi salvo anteriormente!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Listen screen touches for auto close the keyboard when user touch or fade up/down the screen
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);
        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];
            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom())) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

    /**
     * Activity result callback from user selected image in album from the picker
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri dataUri = data.getData();
            File dataFile = null;
            try {
                dataFile = new File(new URI(dataUri.toString()));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            Log.w("", "Image path file: " + dataFile.toString());

            sendImage(dataFile, requestCode);

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Load image icon from the current word link
     */
    public void updateImageWord() {
        ImageLoadUtil.getInstance().loadImage(urlImageWord, imageWord, getApplicationContext());
    }

    /**
     * Load image icon from the current theme link
     */
    public void updateImageTheme() {
        ImageLoadUtil.getInstance().loadImage(urlImageTheme, imageTheme, getApplicationContext());
    }

    /**
     * store the image at runtime
     *
     * @param fileImage
     * @param requestCode
     */
    public void sendImage(File fileImage, int requestCode) {
        if (requestCode == 1) {
            setImageToObject(fileImage, theme);
            urlImageTheme = getImagePathFromObject(theme);
            updateImageTheme();
        } else if (requestCode == 2) {
            setImageToObject(fileImage, editWord);
            urlImageWord = getImagePathFromObject(editWord);
            updateImageWord();
        }
    }

    /**
     * Populate View with list of words
     */
    public void fillRecycleView() {
        recyclerView.setLayoutManager(layManager);
        recyclerView.setAdapter(new CreateWordAdapter(this, getApplicationContext(), true, true));
    }

    /**
     * Action call for Back button press
     */
    public void backAction(View v) {
        onBackPressed();
    }

    /**
     * Action call for Back to menu
     */
    public void backToHome() {
        Intent intent = new Intent(CreateThemeActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * Action call for Back button press
     */
    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            if (this.editMode) {
                backToHome();
            } else {
                Intent it = new Intent(CreateThemeActivity.this, ImportOptionsActivity.class);
                startActivity(it);
                finish();
            }
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }

    /**
     * Listen to detect click on any view in the activity
     *
     * @param view
     */
    @Override
    public void onClick(View view) {

    }
}