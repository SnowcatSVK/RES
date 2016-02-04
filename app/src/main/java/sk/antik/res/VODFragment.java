package sk.antik.res;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lucasr.twowayview.TwoWayView;

import java.io.IOException;
import java.util.ArrayList;

import sk.antik.res.io.RequestHandler;
import sk.antik.res.logic.VOD;
import sk.antik.res.logic.VODAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class VODFragment extends Fragment {

    private TwoWayView vod;

    private ArrayList<VOD> vods;

    public VODFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vod, container, false);

        vod = (TwoWayView) view.findViewById(R.id.vod_twoWayView);



        /*ArrayList<VOD> vods = new ArrayList<>();
        vods.add(new VOD("Twilight", "2008", R.drawable.vod1));
        vods.add(new VOD("AVATAR", "2009", R.drawable.vod2));
        vods.add(new VOD("TITANIC", "1997", R.drawable.vod3));
        vods.add(new VOD("Star Wars", "2011", R.drawable.vod4));
        vods.add(new VOD("Friends with Benefits", "2004", R.drawable.vod5));
        vods.add(new VOD("Rush", "2013", R.drawable.vod6));
        vods.add(new VOD("The Green Mile", "1999", R.drawable.vod7));


        VODAdapter adapter = new VODAdapter(getActivity(), vods);*/

        ImageView leftArrowImageView = (ImageView) view.findViewById(R.id.left_imageButton);
        ImageView rightArrowImageView = (ImageView) view.findViewById(R.id.right_imageButton);

        leftArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vod.scrollBy(100);
            }
        });

        rightArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vod.scrollBy(-100);
            }
        });

        /*ArrayList<String> items = new ArrayList<String>();
        items.add("Item 1");
        items.add("Item 2");
        items.add("Item 3");
        items.add("Item 4");
        items.add("Item 5");


        ArrayAdapter<String> aItems = new ArrayAdapter<String>(getActivity(), R.layout.simple_list_item_1, items);*/
        loadVOD();

        return view;
    }

    public void setVods(ArrayList<VOD> vods) {
        this.vods = vods;
        VODAdapter adapter = new VODAdapter(getActivity(), vods);
        vod.setAdapter(adapter);
    }

    public void loadVOD() {
        final RequestHandler handler = new RequestHandler("http://posa.res_api.dev3.antik.sk");

        new AsyncTask<Void, Void, Void>() {
            JSONObject responseJson;

            @Override
            protected Void doInBackground(Void... params) {
                JSONObject json = new JSONObject();
                try {
                    json.put("function", "GetContent");
                    json.put("content_type_id", 4);
                    responseJson = handler.handleRequest(json);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if (responseJson != null) {
                    try {
                        JSONArray list = responseJson.getJSONArray("Content list");
                        JSONObject json = list.getJSONObject(0);
                        JSONArray vodJsonArray = json.getJSONArray("content");
                        ArrayList<VOD> vods = new ArrayList<>();
                        for (int i = 0; i < vodJsonArray.length(); i++) {
                            JSONObject vodJson = vodJsonArray.getJSONObject(i);
                            VOD vod = new VOD(vodJson.getInt("id"),
                                    vodJson.getString("name"),
                                    vodJson.getString("source"));
                            vods.add(vod);
                        }
                        setVods(vods);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }
}
