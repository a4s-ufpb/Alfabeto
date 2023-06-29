package com.napoleao.alphabeto.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.napoleao.alphabeto.R;
import com.napoleao.alphabeto.activity.CreateThemeActivity;
import com.napoleao.alphabeto.activity.util.ImageLoadUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;

/**
 * Class responsible for implementing the image search adapter.
 */
public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {
    private Context fragmentContext;
    private int TAG;
    private Fragment fragmento;
    private List<HashMap<String, Object>> resultado;

    /**
     * Constructor method.
     *
     * @param resultado
     * @param context
     * @param searchFragment
     * @param tag
     */
    public SearchListAdapter(List<HashMap<String, Object>> resultado, Context context, SearchFragment searchFragment, int tag) {
        this.fragmentContext = context;
        this.resultado = resultado;
        this.TAG = tag;
        this.fragmento = searchFragment;
    }

    /**
     * Class responsible for implementing the view holder.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_card_grid, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Class responsible for implementing the view holder.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageUrl = null;

        try {
            imageUrl = resultado.get(position).get("webformatURL").toString();
        } catch (Exception e) {
            Toast.makeText(fragmentContext, "Imagem Invalida, Tente escolher Outra", Toast.LENGTH_SHORT).show();
        }

        if (imageUrl != null) {
            loadImage(imageUrl, holder.img);

            String finalImageUrl = imageUrl;
            holder.itemView.setOnClickListener(v -> {
                fragmento.getView().findViewById(R.id.loadingframeLayout2).setVisibility(View.VISIBLE);

                GetImageExecutor executor = new GetImageExecutor(finalImageUrl, fragmento, TAG);
                executor.start();
            });
        }
    }

    /**
     * Count the number of items in the list.
     *
     * @return number of items in the list
     */
    @Override
    public int getItemCount() {
        return resultado.size();
    }

    /**
     * Class responsible for downloading the image.
     */
    public class GetImageExecutor extends Thread {

        private String imageUrl;
        private Fragment fragmento;
        private int TAG;

        public GetImageExecutor(String imageUrl, Fragment fragmento, int TAG) {
            this.imageUrl = imageUrl;
            this.fragmento = fragmento;
            this.TAG = TAG;
        }

        @Override
        public void run() {
            try {

                URL url = new URL(imageUrl);
                URLConnection connection = url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                InputStream input = connection.getInputStream();

                File outputDir = fragmento.getActivity().getCacheDir();
                File outputFile = File.createTempFile("image", ".jpg", outputDir);

                OutputStream outStream = new FileOutputStream(outputFile);

                byte[] buffer = new byte[2 * 1024];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
                input.close();

                fragmento.getActivity().runOnUiThread(new Thread(new Runnable() {
                    public void run() {
                        fragmento.getView().findViewById(R.id.loadingframeLayout2).setVisibility(View.GONE);

                        CreateThemeActivity actv = (CreateThemeActivity) fragmento.getActivity();

                        actv.sendImage(outputFile, TAG);

                        actv.onBackPressed();
                    }
                }));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Class responsible for implementing the view holder.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.grid_image);
        }
    }

    /**
     * Method responsible for loading the image.
     *
     * @param imageUrl
     * @param themeImageLeft
     */
    private void loadImage(String imageUrl, ImageView themeImageLeft) {
        ImageLoadUtil.getInstance().loadImage(imageUrl, themeImageLeft, fragmentContext);
    }
}
