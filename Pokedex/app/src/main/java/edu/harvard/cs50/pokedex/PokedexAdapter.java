package edu.harvard.cs50.pokedex;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PokedexAdapter extends RecyclerView.Adapter<PokedexAdapter.PokedexViewHolder> implements Filterable {
	/**
	 * <p>Returns a filter that can be used to constrain data with a filtering
	 * pattern.</p>
	 *
	 * <p>This method is usually implemented by {@link Adapter}
	 * classes.</p>
	 *
	 * @return a filter used to constrain data
	 */
	@Override
	public Filter getFilter() {
		return new PokemonFilter();
	}

	private class PokemonFilter extends Filter {

		/**
		 * <p>Invoked in a worker thread to filter the data according to the
		 * constraint. Subclasses must implement this method to perform the
		 * filtering operation. Results computed by the filtering operation
		 * must be returned as a {@link FilterResults} that
		 * will then be published in the UI thread through
		 * {@link #publishResults(CharSequence,
		 * FilterResults)}.</p>
		 *
		 * <p><strong>Contract:</strong> When the constraint is null, the original
		 * data must be restored.</p>
		 *
		 * @param constraint the constraint used to filter the data
		 * @return the results of the filtering operation
		 * @see #filter(CharSequence, FilterListener)
		 * @see #publishResults(CharSequence, FilterResults)
		 * @see FilterResults
		 */
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults filterResults = new FilterResults();
			List<Pokemon> matchingPokemons = new ArrayList<>();

			if (constraint.length() == 0) {
				matchingPokemons.addAll(originalPokemon);
			} else {
				for (Pokemon currentPokemon : originalPokemon) {
					if (currentPokemon.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
						matchingPokemons.add(currentPokemon);
					}
				}
			}

			filterResults.values = matchingPokemons;
			filterResults.count = matchingPokemons.size();
			return filterResults;
		}

		/**
		 * <p>Invoked in the UI thread to publish the filtering results in the
		 * user interface. Subclasses must implement this method to display the
		 * results computed in {@link #performFiltering}.</p>
		 *
		 * @param constraint the constraint used to filter the data
		 * @param results    the results of the filtering operation
		 * @see #filter(CharSequence, FilterListener)
		 * @see #performFiltering(CharSequence)
		 * @see FilterResults
		 */
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			filteredPokemon = (List<Pokemon>) results.values;
			notifyDataSetChanged();
		}
	}

	public static class PokedexViewHolder extends RecyclerView.ViewHolder {
		public LinearLayout containerView;
		public TextView textView;

		PokedexViewHolder(View view) {
			super(view);

			containerView = view.findViewById(R.id.pokedex_row);
			textView = view.findViewById(R.id.pokedex_row_text_view);

			containerView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Pokemon current = (Pokemon) containerView.getTag();
					Intent intent = new Intent(v.getContext(), PokemonActivity.class);
					intent.putExtra("url", current.getUrl());

					v.getContext().startActivity(intent);
				}
			});
		}
	}

	private final List<Pokemon> originalPokemon = new ArrayList<>();
	private List<Pokemon> filteredPokemon = new ArrayList<>();
	private final RequestQueue requestQueue;

	PokedexAdapter(Context context) {
		requestQueue = Volley.newRequestQueue(context);
		loadPokemon();
		filteredPokemon = originalPokemon;
	}

	public void loadPokemon() {
		String url = "https://pokeapi.co/api/v2/pokemon?limit=151";
		JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONArray results = response.getJSONArray("results");
					for (int i = 0; i < results.length(); i++) {
						JSONObject result = results.getJSONObject(i);
						String name = result.getString("name");
						originalPokemon.add(new Pokemon(
								name.substring(0, 1).toUpperCase() + name.substring(1),
								result.getString("url")
						));
					}

					notifyDataSetChanged();
				} catch (JSONException e) {
					Log.e("cs50", "Json error", e);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e("cs50", "Pokemon list error", error);
			}
		});

		requestQueue.add(request);
	}

	@NonNull
	@Override
	public PokedexViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.pokedex_row, parent, false);

		return new PokedexViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull PokedexViewHolder holder, int position) {
		Pokemon current = filteredPokemon.get(position);
		holder.textView.setText(current.getName());
		holder.containerView.setTag(current);
	}

	@Override
	public int getItemCount() {
		return filteredPokemon.size();
	}
}
