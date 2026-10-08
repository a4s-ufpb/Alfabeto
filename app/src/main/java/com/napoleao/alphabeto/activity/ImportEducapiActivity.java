package com.napoleao.alphabeto.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.napoleao.alphabeto.R;
import com.napoleao.alphabeto.adapter.ThemeImportAdapter;
import com.napoleao.alphabeto.api.response.ContextPageResponse;
import com.napoleao.alphabeto.helper.RetrofitConfig;
import com.napoleao.alphabeto.helper.ThemeSyncManager;
import com.napoleao.alphabeto.helper.dao.ChallengesDAO;
import com.napoleao.alphabeto.helper.dao.TemasDAO;
import com.napoleao.alphabeto.model.Challenge;
import com.napoleao.alphabeto.model.Tema;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Screen for importing themes from EducAPI, with 3 tabs that share the same list:
 * <ul>
 * <li>Todos os temas: every theme, with a filter by name;</li>
 * <li>Por ID: one theme, shown before saving (no filter);</li>
 * <li>Por e-mail: the themes of one creator, with a filter by name.</li>
 * </ul>
 * Each tab keeps its own results, but the ticked themes are shared, so they are kept when the
 * user changes tabs. Results are paginated: the next page is loaded while the user scrolls.
 */
public class ImportEducapiActivity extends AppCompatActivity {

    private static final int PAGE_SIZE = 20;
    private static final long FILTER_DELAY_MS = 400;

    private static final int MODE_ALL = 0;
    private static final int MODE_ID = 1;
    private static final int MODE_EMAIL = 2;

    /**
     * Everything one tab needs to remember while the user is on another tab.
     */
    private class TabState {
        final int mode;
        final int introRes;
        final List<Tema> themes = new ArrayList<>();
        ThemeImportAdapter adapter;

        String name;                // applied name filter (null = none)
        String query;               // applied ID or e-mail (null = nothing searched yet)
        String nameText = "";       // what is typed in the fields, restored when coming back
        String queryText = "";
        boolean active;             // false while the tab waits for the first search

        int nextPage = 0;
        boolean isLast = false;
        boolean loading = false;
        boolean loadError = false;
        boolean notFound = false;   // ID tab: the server does not know this ID
        long totalElements = 0;
        // incremented on each new search, so late answers of an old search are ignored
        int searchId = 0;
        Parcelable layoutState;     // scroll position

        TabState(int mode, int introRes) {
            this.mode = mode;
            this.introRes = introRes;
        }
    }

    private TabLayout tabLayout;
    private TextView txtIntro;
    private TextView txtCounter;
    private View layoutQuery;
    private EditText editQuery;
    private EditText editFilter;
    private ProgressBar progress;
    private RecyclerView recycler;
    private LinearLayoutManager layoutManager;

    private TabState[] states;
    private TabState current;
    // true while the code (not the user) is filling the text fields
    private boolean restoringFields = false;

    // themes ticked by the user, shared by all tabs
    private final List<Tema> selectedThemes = new ArrayList<>();

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable filterRunnable = new Runnable() {
        @Override
        public void run() {
            applyFilter();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_educapi);

        tabLayout = findViewById(R.id.tabsImport);
        txtIntro = findViewById(R.id.txtIntroImport);
        txtCounter = findViewById(R.id.txtCounter);
        layoutQuery = findViewById(R.id.layoutQuery);
        editQuery = findViewById(R.id.editQuery);
        editFilter = findViewById(R.id.editFilterName);
        progress = findViewById(R.id.progressImport);
        recycler = findViewById(R.id.recyclerThemes);

        states = new TabState[]{
                new TabState(MODE_ALL, R.string.import_intro_todos),
                new TabState(MODE_ID, R.string.import_intro_id),
                new TabState(MODE_EMAIL, R.string.import_intro_email)
        };
        for (TabState st : states) {
            st.adapter = new ThemeImportAdapter(this, st.themes, selectedThemes);
        }
        states[MODE_ALL].active = true;

        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recycler.setLayoutManager(layoutManager);

        setupTabs();
        setupScroll();
        setupFields();

        showTab(MODE_ALL);
        startNewSearch(states[MODE_ALL]);
    }

    // ------------------------------------------------------------------ tabs

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_todos));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.import_id));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.import_mail));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // applies a name typed just before leaving the tab
                handler.removeCallbacks(filterRunnable);
                applyFilter();
                current.layoutState = layoutManager.onSaveInstanceState();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    /**
     * Shows the list, the fields and the texts of the chosen tab.
     */
    private void showTab(int position) {
        current = states[position];

        txtIntro.setText(current.introRes);
        layoutQuery.setVisibility(current.mode == MODE_ALL ? View.GONE : View.VISIBLE);
        editFilter.setVisibility(current.mode == MODE_ID ? View.GONE : View.VISIBLE);

        restoringFields = true;
        if (current.mode == MODE_ID) {
            editQuery.setHint(R.string.import_id_hint);
            editQuery.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (current.mode == MODE_EMAIL) {
            editQuery.setHint(R.string.import_email_hint);
            editQuery.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }
        editQuery.setText(current.queryText);
        editFilter.setText(current.nameText);
        restoringFields = false;

        recycler.setAdapter(current.adapter);
        if (current.layoutState != null) {
            layoutManager.onRestoreInstanceState(current.layoutState);
        }
        refreshUi();
    }

    // ------------------------------------------------------------------ fields

    private void setupScroll() {
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy <= 0) {
                    return;
                }
                if (layoutManager.findLastVisibleItemPosition() >= current.themes.size() - 3) {
                    request(current);
                }
            }
        });

        // after an error, the counter text works as a "try again" button
        txtCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current.loadError && !current.notFound) {
                    current.loadError = false;
                    request(current);
                }
            }
        });
    }

    private void setupFields() {
        editFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (restoringFields) {
                    return;
                }
                current.nameText = s.toString();
                // waits for the user to stop typing before asking the server
                handler.removeCallbacks(filterRunnable);
                handler.postDelayed(filterRunnable, FILTER_DELAY_MS);
            }
        });

        editFilter.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                    handler.removeCallbacks(filterRunnable);
                    applyFilter();
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });

        editQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!restoringFields) {
                    current.queryText = s.toString();
                }
            }
        });

        editQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                    searchByQuery(v);
                    return true;
                }
                return false;
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editFilter.getWindowToken(), 0);
        }
    }

    /**
     * "Buscar" button (tabs "Por ID" and "Por e-mail").
     *
     * @param view
     */
    public void searchByQuery(View view) {
        String text = editQuery.getText().toString().trim();
        TabState st = current;

        if (st.mode == MODE_ID) {
            if (text.isEmpty()) {
                Toast.makeText(this, R.string.import_msg_id_vazio, Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                text = String.valueOf(Long.parseLong(text));
            } catch (NumberFormatException e) {
                Toast.makeText(this, R.string.import_msg_id_invalido, Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (st.mode == MODE_EMAIL) {
            if (text.isEmpty()) {
                Toast.makeText(this, R.string.import_msg_email_vazio, Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                Toast.makeText(this, R.string.import_msg_email_invalido, Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            return;
        }

        hideKeyboard();
        st.query = text;
        st.active = true;
        startNewSearch(st);
    }

    /**
     * Starts a new search only if the typed name is different from the one already applied.
     * Only for the current tab ("Por ID" has no name filter).
     */
    private void applyFilter() {
        TabState st = current;
        if (st.mode == MODE_ID) {
            return;
        }
        String name = st.nameText.trim();
        if (name.isEmpty()) {
            name = null;
        }
        if ((name == null && st.name == null) || (name != null && name.equals(st.name))) {
            return;
        }
        st.name = name;
        if (st.mode == MODE_EMAIL && st.query == null) {
            return; // still waiting for an e-mail; the name will be used in the first search
        }
        startNewSearch(st);
    }

    // ------------------------------------------------------------------ loading

    /**
     * Clears the list of the tab and loads the first page again. The ticked themes are kept.
     */
    private void startNewSearch(TabState st) {
        st.searchId++;
        st.loading = false;
        st.loadError = false;
        st.notFound = false;
        st.isLast = false;
        st.nextPage = 0;
        st.totalElements = 0;
        st.themes.clear();
        st.adapter.notifyDataSetChanged();
        st.layoutState = null;
        request(st);
        if (st == current) {
            refreshUi();
        }
    }

    /**
     * Loads the next page of a tab (or, in the ID tab, the theme itself).
     */
    private void request(final TabState st) {
        if (st.loading || st.isLast || st.loadError || !st.active) {
            return;
        }
        if (st.mode == MODE_EMAIL && st.query == null) {
            return;
        }
        st.loading = true;
        if (st == current) {
            refreshUi();
        }
        final int thisSearch = st.searchId;

        if (st.mode == MODE_ID) {
            requestById(st, thisSearch);
        } else {
            requestPage(st, thisSearch);
        }
    }

    private void requestPage(final TabState st, final int thisSearch) {
        final int page = st.nextPage;
        String email = st.mode == MODE_EMAIL ? st.query : null;

        new RetrofitConfig().contextService()
                .getContextsFiltered(page, PAGE_SIZE, "id,asc", st.name, email)
                .enqueue(new Callback<ContextPageResponse>() {
                    @Override
                    public void onResponse(Call<ContextPageResponse> call, Response<ContextPageResponse> response) {
                        if (st.searchId != thisSearch || isFinishing()) {
                            return; // old search
                        }
                        st.loading = false;

                        ContextPageResponse body = response.body();
                        if (!response.isSuccessful() || body == null) {
                            onLoadFailed(st);
                            return;
                        }

                        List<Tema> content = body.getContent();
                        int start = st.themes.size();
                        st.themes.addAll(content);
                        st.adapter.notifyItemRangeInserted(start, content.size());
                        st.nextPage = page + 1;
                        st.isLast = body.isLast() || content.isEmpty();
                        st.totalElements = body.getTotalElements();
                        if (st == current) {
                            refreshUi();
                        }
                    }

                    @Override
                    public void onFailure(Call<ContextPageResponse> call, Throwable t) {
                        if (st.searchId != thisSearch || isFinishing()) {
                            return;
                        }
                        st.loading = false;
                        onLoadFailed(st);
                    }
                });
    }

    private void requestById(final TabState st, final int thisSearch) {
        new RetrofitConfig().contextService()
                .getContextById(Long.parseLong(st.query))
                .enqueue(new Callback<Tema>() {
                    @Override
                    public void onResponse(Call<Tema> call, Response<Tema> response) {
                        if (st.searchId != thisSearch || isFinishing()) {
                            return;
                        }
                        st.loading = false;

                        Tema body = response.body();
                        if (response.isSuccessful() && body != null && body.getId() != null) {
                            st.themes.add(body);
                            st.adapter.notifyDataSetChanged();
                            st.isLast = true;
                            st.totalElements = 1;
                            if (st == current) {
                                refreshUi();
                            }
                        } else if (response.isSuccessful() || (response.code() >= 400 && response.code() < 500)) {
                            // the server answered, but there is no theme with this ID
                            st.notFound = true;
                            st.loadError = true;
                            st.isLast = true;
                            if (st == current) {
                                refreshUi();
                            }
                        } else {
                            onLoadFailed(st);
                        }
                    }

                    @Override
                    public void onFailure(Call<Tema> call, Throwable t) {
                        if (st.searchId != thisSearch || isFinishing()) {
                            return;
                        }
                        st.loading = false;
                        onLoadFailed(st);
                    }
                });
    }

    private void onLoadFailed(TabState st) {
        st.loadError = true;
        if (st == current) {
            refreshUi();
            Toast.makeText(this, R.string.import_counter_error, Toast.LENGTH_SHORT).show();
        }
    }

    // ------------------------------------------------------------------ screen state

    /**
     * Updates the progress bar and the text under the list for the current tab.
     */
    private void refreshUi() {
        progress.setVisibility(current.loading && current.themes.isEmpty() ? View.VISIBLE : View.GONE);
        txtCounter.setText(counterText(current));
    }

    /**
     * Text under the list: "20 de 134 temas", "Nenhum tema encontrado." or the error.
     */
    private String counterText(TabState st) {
        if (st.notFound) {
            return getString(R.string.import_counter_not_found);
        }
        if (st.loadError) {
            return getString(R.string.import_counter_error);
        }
        if (!st.active) {
            return "";
        }
        int loaded = st.themes.size();
        if (loaded == 0) {
            return st.loading ? "" : getString(R.string.import_counter_empty);
        }
        if (st.totalElements > 0) {
            long total = Math.max(st.totalElements, loaded);
            return total == 1
                    ? getString(R.string.import_counter_one)
                    : getString(R.string.import_counter, loaded, total);
        }
        // the API did not send the total
        return loaded == 1
                ? getString(R.string.import_counter_one)
                : getString(R.string.import_counter_unknown, loaded);
    }

    // ------------------------------------------------------------------ save / back

    /**
     * Method responsible for saving the selected themes.
     *
     * @param view
     */
    public void saveSelectedThemes(View view) {
        if (selectedThemes.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Selecione ao menos um tema!", Toast.LENGTH_LONG).show();
            return;
        }

        TemasDAO temasDAO = TemasDAO.getInstance(getApplicationContext());
        ChallengesDAO challengesDAO = ChallengesDAO.getInstance(getApplicationContext());
        int contSucessos = 0;

        for (Tema t : selectedThemes) {
            if (!t.getChallenges().isEmpty()) {
                ThemeSyncManager.prepareForLocalSave(getApplicationContext(), t);

                if (temasDAO.save(t)) {
                    for (Challenge c : t.getChallenges()) {
                        c.setIdTema(t.getId());
                        challengesDAO.save(c);
                    }
                    contSucessos++;
                }
            }
        }
        Toast.makeText(this, contSucessos + " novo(s) tema(s) salvo(s)!", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(ImportEducapiActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * Method responsible for returning to the previous screen.
     *
     * @param view
     */
    public void backAction(View view) {
        onBackPressed();
    }

    /**
     * Method responsible for returning to the previous screen.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent it = new Intent(ImportEducapiActivity.this, ImportOptionsActivity.class);
        startActivity(it);
        finish();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(filterRunnable);
        super.onDestroy();
    }
}
