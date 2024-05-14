package com.skloch.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import screens.GameScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapManager {

    private final TmxMapLoader mapLoader;
    private final HashMap<String, TiledMap> loadedMaps;
    private TiledMap currentMap;
    private MapProperties mapProperties;
    private OrthogonalTiledMapRenderer mapRenderer;
    public int[] backgroundLayers;
    public int[] foregroundLayers;
    public int collisionLayer;
    public int interactLayer;
    private Float viewportScalar;
    private GameScreen game;


    public MapManager() {
        mapLoader = new TmxMapLoader();
        loadedMaps = new HashMap<>();
    }

    public MapManager(GameScreen game) {
        mapLoader = new TmxMapLoader();
        loadedMaps = new HashMap<>();
        this.game = game;
    }

    public TiledMap loadMap(String mapPath) {
        TiledMap map = null;
        try {
            if (loadedMaps.containsKey(mapPath)) {
                map =  loadedMaps.get(mapPath);
            } else {
                map = mapLoader.load(mapPath);
                loadedMaps.put(mapPath, map);
            }
        } catch (Exception e) {
            // Throw exception
            throw new RuntimeException("Failed to load the map");
        }

        mapProperties = map.getProperties();
        currentMap = map;
        if (mapRenderer != null) {
            mapRenderer.dispose();
        }
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        getLayers();
        viewportScalar = mapProperties.get("viewportScalar", Float.class);
        if (game != null) {
            game.teleported();
        }
        return map;
    }

    public TiledMap getCurrentMap() {
        if (currentMap == null) {
            throw new RuntimeException("No map loaded yet");
        }
        return currentMap;
    }

    public Vector2 getMapDimensions() {
        float width = mapProperties.get("width", Integer.class);
        float height = mapProperties.get("height", Integer.class);
        return new Vector2(width, height);
    }

    public Vector2 getMapPixelDimensions() {
        Vector2 mapUnitDimensions = getMapDimensions();
        float tileWidth = mapProperties.get("tilewidth", Integer.class);
        return new Vector2(
                mapUnitDimensions.x * tileWidth,
                mapUnitDimensions.y * tileWidth
        );
    }

    public List<GameObject> getCollisionObjects() {
        int[] layers = new int[]{collisionLayer};
        List<GameObject> collisionObjects = getObjectsFromLayers(layers);
        return collisionObjects;
    }

    public List<GameObject> getInteractObjects() {
        int[] layers = new int[]{interactLayer};
        List<GameObject> collisionObjects = getObjectsFromLayers(layers);
        return collisionObjects;
    }

    public Vector2 getSpawn() {
        int[] layers = new int[]{interactLayer};
        List<GameObject> collisionObjects = getObjectsFromLayers(layers);
        for (GameObject object : collisionObjects) {
            MapProperties properties = object.properties;
            if (properties.get("spawn") != null) {
                float x = (float)properties.get("x");
                float y = (float)properties.get("y");
                return new Vector2(x, y);
            }
        }
        throw new RuntimeException("Spawn not set");
    }

    public void setCamera(OrthographicCamera camera) {
        mapRenderer.setView(camera);
    }

    public void renderForeground() {
        mapRenderer.render(foregroundLayers);
    }

    public void renderBackground() {
        mapRenderer.render(backgroundLayers);
    }

    public float getViewportScalar() {
        if (viewportScalar == null) {
            return 1;
        }
        return viewportScalar;
    }

    public void dispose() {
        // Iterate through all the maps and dispose of them
        for (TiledMap map : loadedMaps.values()) {
            map.dispose();
        }
        mapRenderer.dispose();
    }

    private void getLayers() {
        try {
            backgroundLayers = getLayerArrayFromMapProperties("backgroundLayers");
            foregroundLayers = getLayerArrayFromMapProperties("foregroundLayers");
            collisionLayer = mapProperties.get("collisionLayer", Integer.class);
            interactLayer = mapProperties.get("interactLayer", Integer.class);
        } catch (Exception e) {
            throw new RuntimeException("Error loading layers");
        }
    }

    private int[] getLayerArrayFromMapProperties(String key) {
        // The map should have a property called for example "backgroundLayers" which is a comma separated list of integers.
        // Put these integers into an int array
        try {
            String[] layersString = mapProperties.get(key, String.class).split(",");
            if (layersString.length == 0 || layersString[0].isEmpty()) {
                return new int[0];
            } else {
                int[] layers = new int[layersString.length];
                for (int i = 0; i < layersString.length; i++) {
                    if (layersString[i].isEmpty()) {
                        continue;
                    }
                    layers[i] = Integer.parseInt(layersString[i]);
                }
                return layers;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading layer: "+key);
        }
    }

    private List<GameObject> getObjectsFromLayers(int[] layers) {
        List<GameObject> allObjects = new ArrayList<>();
        for (int layer : layers) {
            // Get all objects on the layer
            MapObjects layerObjects = currentMap.getLayers().get(layer).getObjects();
            for (int i = 0; i < layerObjects.getCount(); i++) {
                // Get the properties of each object
                MapProperties properties = layerObjects.get(i).getProperties();
                allObjects.add(new GameObject(properties));
            }
        }
        return allObjects;
    }
}
