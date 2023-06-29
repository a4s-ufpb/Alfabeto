package com.napoleao.alphabeto.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.napoleao.alphabeto.R;
import com.napoleao.alphabeto.helper.SearchEngineClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class responsible for implementing the image search fragment.
 */
public class SearchFragment extends Fragment {

    private String query;
    private int TAG;

    private RecyclerView rv;
    private GridLayoutManager laymanager;
    private SearchEngineClient searchEngine;

    private ArrayList<HashMap<String, Object>> resultado = new ArrayList();

    /**
     * Constructor method.
     *
     * @param query
     * @param TAG
     */
    public SearchFragment(String query, int TAG) {
        this.query = query;
        this.TAG = TAG;
    }

    /**
     * Method responsible for creating the view.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return root
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_nav_search, container, false);

        searchEngine = new SearchEngineClient(getContext());
        rv = root.findViewById(R.id.search_rv);

        laymanager = new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false);

        try {
            getActivity().runOnUiThread(new Thread(new Runnable() {
                public void run() {
                    getActivity().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

                    searchEngine.search(query, new SearchEngineClient.SearchEngineClientBingCompletionHandler() {
                        @Override
                        public void success(List<HashMap<String, Object>> resultado) {
                            getActivity().runOnUiThread(new Thread(new Runnable() {
                                public void run() {
                                    getActivity().findViewById(R.id.progressBar).setVisibility(View.GONE);
                                    fillRecycleView(resultado);
                                }
                            }));
                        }

                        @Override
                        public void failed(String reason) {
                            getActivity().runOnUiThread(new Thread(new Runnable() {
                                public void run() {
                                    getActivity().findViewById(R.id.progressBar).setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), reason, Toast.LENGTH_SHORT).show();
                                }
                            }));
                        }
                    });
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return root;
    }

    /**
     * Method responsible for filling the recycle view.
     *
     * @param resultado
     */
    private void fillRecycleView(List<HashMap<String, Object>> resultado) {
        rv.setLayoutManager(laymanager);
        rv.setAdapter(new SearchListAdapter(resultado, getContext(), this, TAG));
    }

}
