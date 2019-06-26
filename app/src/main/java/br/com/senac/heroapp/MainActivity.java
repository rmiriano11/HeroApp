package br.com.senac.heroapp;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.senac.heroapp.modelo.HeroApp;
import br.com.senac.heroapp.webservice.Api;
import br.com.senac.heroapp.webservice.RequestHandler;

public class MainActivity extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1020;
    private static final int CODE_POST_REQUEST = 1025;

    EditText editTextIdHero;
    EditText editTextHero;
    EditText editTextClasse;
    EditText editTextRanking;
    Button buttonSalvar;
    ProgressBar progressBar;
    ListView listView;
    List<HeroApp> heroappList;

    Boolean isUpdating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.barraProgresso);
        listView = findViewById(R.id.listViewHerois);
        editTextIdHero = findViewById(R.id.editTextIdHero);
        editTextHero = findViewById(R.id.editTextHero);
        editTextClasse = findViewById(R.id.editTextClasse);
        editTextRanking = findViewById(R.id.editTextRaking);

        buttonSalvar = findViewById(R.id.buttonSalvar);

        heroappList = new ArrayList<>();

        buttonSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUpdating){
                    updateHeroApp();
                }else{
                    createHeroApp();
                }
            }
        });
        readHeroApp();
    }

    private void createHeroApp(){
        String hero = editTextHero.getText().toString().trim();
        String classe = editTextClasse.getText().toString().trim();
        String ranking = editTextRanking.getText().toString().trim();

        if (TextUtils.isEmpty(hero)){
            editTextHero.setError("Digite seu nome de heroi");
            editTextHero.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(classe)){
            editTextClasse.setError("Digite sua classe");
            editTextClasse.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(ranking)){
            editTextRanking.setError("Digite seu raking");
            editTextRanking.requestFocus();
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("nome", hero);
        params.put("classe", classe);
        params.put("ranking", ranking);

        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_HEROAPP, params, CODE_POST_REQUEST);
        request.execute();
    }

    private void updateHeroApp(){
        String id = editTextIdHero.getText().toString();
        String hero = editTextHero.getText().toString().trim();
        String classe = editTextClasse.getText().toString().trim();
        String ranking = editTextRanking.getText().toString().trim();

        HashMap<String, String> params = new HashMap<>();
        params.put("id_nome", id);
        params.put("nome", hero);
        params.put("classe", classe);
        params.put("ranking", ranking);

        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_UPDATE_HEROAPP, params, CODE_POST_REQUEST);
        request.execute();

        buttonSalvar.setText("Salvar");
        editTextHero.setText("");
        editTextRanking.setText("");
        editTextClasse.setText("");

        isUpdating = false;
    }

    private void readHeroApp(){
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_HEROAPP, null, CODE_GET_REQUEST);
        request.execute();
    }

    private void deleteHeroApp(int id){
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_DELETE_HEROAPP + id, null, CODE_POST_REQUEST);
        request.execute();
    }

    private void refreshHeroAppList(JSONArray heroapp) throws JSONException{
        heroappList.clear();

        for (int i = 0; i < heroapp.length(); i++){
            JSONObject obj = heroapp.getJSONObject(i);

            heroappList.add(new HeroApp(
                    obj.getInt("id_nome"),
                    obj.getString("nome"),
                    obj.getString("classe"),
                    obj.getString("ranking")
            ));
        }

        HeroAppAdapter adapter = new HeroAppAdapter(heroappList);
        listView.setAdapter(adapter);
    }

    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;

        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    refreshHeroAppList(object.getJSONArray("heroapp"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);


            if (requestCode == CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }

    class HeroAppAdapter extends ArrayAdapter<HeroApp>{

        List<HeroApp> heroAppList;

        public HeroAppAdapter(List<HeroApp> heroAppList){
            super(MainActivity.this, R.layout.layout_heroapp, heroAppList);
            this.heroAppList = heroAppList;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.layout_heroapp, null, true);

            TextView textViewHerois = listViewItem.findViewById(R.id.textViewHerois);
            TextView textViewDelete = listViewItem.findViewById(R.id.textViewDelete);
            TextView textViewAlterar = listViewItem.findViewById(R.id.textViewAlterar);

            final HeroApp heroApp = heroAppList.get(position);

            textViewHerois.setText(heroApp.getHero());
            textViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setTitle("Delete" + heroApp.getHero())
                            .setMessage("Você quer realmente deletear?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteHeroApp(heroApp.getId());
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });

            textViewAlterar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isUpdating = true;

                    editTextIdHero.setText(String.valueOf(heroApp.getId()));
                    editTextHero.setText(heroApp.getHero());
                    editTextClasse.setText(heroApp.getClasse());
                    editTextRanking.setText(heroApp.getRaking());

                    buttonSalvar.setText("Alterar");
                }
            });
            return listViewItem;
        }
    }
}
