package edu.harvard.cs50.pokedex;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class PokemonActivity extends AppCompatActivity {
	private TextView nameTextView;
	private TextView numberTextView;
	private TextView type1TextView;
	private TextView type2TextView;
	private ImageView pokemonImageView;
	private TextView pokemonDescriptionTextView;
	private String url;
	private String imageUrl;
	private RequestQueue requestQueue;
	private Integer currentId;
	private String pokemonDescription;
	private Button catchPokemon;
	private static final String TAG = "PokemonActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pokemon);

		requestQueue = Volley.newRequestQueue(getApplicationContext());
		url = getIntent().getStringExtra("url");
		nameTextView = findViewById(R.id.pokemon_name);
		numberTextView = findViewById(R.id.pokemon_number);
		type1TextView = findViewById(R.id.pokemon_type1);
		type2TextView = findViewById(R.id.pokemon_type2);
		pokemonDescriptionTextView = findViewById(R.id.pokemon_description);
		catchPokemon = findViewById(R.id.button_catchPokemon);
		pokemonImageView = findViewById(R.id.pokemon_image);
		catchPokemon.setVisibility(View.INVISIBLE); // The button should be invisible until ID is loaded

		load();
	}

	public void load() {
		type1TextView.setText("");
		type2TextView.setText("");

		JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
			@SuppressLint({"DefaultLocale", "SetTextI18n"})
			@Override
			public void onResponse(JSONObject response) {
				try {
					currentId = response.getInt("id");
					nameTextView.setText(response.getString("name"));
					numberTextView.setText(String.format("#%03d", currentId));

					imageUrl = response.getJSONObject("sprites").getString("front_default");

					getDescription(currentId);

					catchPokemon.setVisibility(View.VISIBLE); // ID is loaded, button can be shown now
					if (Pokemon.caught.contains(currentId)) {
						catchPokemon.setText("Release");
					}

                    new DownloadSpriteTask().execute(imageUrl);
					JSONArray typeEntries = response.getJSONArray("types");
					for (int i = 0; i < typeEntries.length(); i++) {
						JSONObject typeEntry = typeEntries.getJSONObject(i);
						int slot = typeEntry.getInt("slot");
						String type = typeEntry.getJSONObject("type").getString("name");

						if (slot == 1) {
							type1TextView.setText(type);
						} else if (slot == 2) {
							type2TextView.setText(type);
						}
					}
				} catch (JSONException e) {
					Log.e("cs50", "Pokemon json error", e);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e("cs50", "Pokemon details error", error);
			}
		});

		requestQueue.add(request);
	}

	public void getDescription(int pokemonId) {

		String url = "https://pokeapi.co/api/v2/pokemon-species/" + pokemonId;
		JsonObjectRequest descriptionRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					boolean isEnglish = false;
					int index = 0;
					JSONArray descriptionEntries = response.getJSONArray("flavor_text_entries");
					JSONObject entry, languageEntry;

					while (!isEnglish) {
						entry = descriptionEntries.getJSONObject(index);
						languageEntry = entry.getJSONObject("language");
						if (languageEntry.getString("name").equals("en")) {
							isEnglish = true;
							pokemonDescription = entry.getString("flavor_text");
							pokemonDescriptionTextView.setText(pokemonDescription);
						} else {
							index++;
						}
					}

				} catch (JSONException e) {
					Log.e(TAG, "onResponse: Could not load pokemon description due to faulty json");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "onErrorResponse: Pokemon description error");
			}
		});

		requestQueue.add(descriptionRequest);
	}

	/**
	 * Toggles Pokemon's caught status and button text
	 *
	 * @param view Catch/Release Pokemon Button
	 */
	@SuppressLint("SetTextI18n")
	public void toggleCatch(View view) {

		if (Pokemon.caught.contains(currentId)) {
			catchPokemon.setText("Catch");
			Pokemon.caught.remove(currentId);
		} else {
			catchPokemon.setText("Release");
			Pokemon.caught.add(currentId);
		}
	}

    private class DownloadSpriteTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                return BitmapFactory.decodeStream(url.openStream());
            }
            catch (IOException e) {
                Log.e("cs50", "Download sprite error", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            pokemonImageView.setImageBitmap(bitmap);
        }
    }
}
