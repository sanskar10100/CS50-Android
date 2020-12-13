# CS50-Android projects

This repo hosts the source code for Android apps that I wrote while following the Android track from CS50x. It contains the following apps:

1. Pokedex: Using Volley library and a RecyclerView, we fetch information from pokeapi.co and display a list of Pokemons. Clicking on a particular pokemon brings up a screen that displays more information about the pokemon, like its sprite (image), types and description. There's also a button to catch or release a pokemon. Stats is saved in a SharedPreferences key-value store.  
2. Fiftygram: Here we implement some Instragram like filters by using glide-transfomations library. Uses can choose an image from their storage, apply filter to the image and save it back to the device.  
3. Notes: Allows users to take notes. Clicking on '+' button brings up a new note, which can be edited or deleted in the next activity. Any changes are saved automatically upon going back from the editing activity. Uses Room library for persistance.