package edu.harvard.cs50.pokedex;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
	private PokedexAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		RecyclerView recyclerView = findViewById(R.id.recycler_view);
		adapter = new PokedexAdapter(getApplicationContext());
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(layoutManager);

		// Restore SharedPreferences
		SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
		for (int i = 0; i < preferences.getInt("size", 0); i++) {
			Pokemon.caught.add(preferences.getInt("val_" + i, -1));
		}
		preferences.edit().clear().apply();
	}

	@Override
	protected void onPause() {
		super.onPause();
		SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();

		for (int i = 0; i < Pokemon.caught.size(); i++) {
			editor.putInt("val_" + i, Pokemon.caught.get(i));
		}
		editor.putInt("size", Pokemon.caught.size());
		editor.apply();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.

		// Associate searchable configuration with the SearchView
		getMenuInflater().inflate(R.menu.main_menu, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

		SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				adapter.getFilter().filter(newText);
				return true;
			}
		});

		return true;
	}
}
