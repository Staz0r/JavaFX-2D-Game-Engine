package tile.utils;

// Holds tile path and collision flag
// Allow null for invisible tiles, but also allow collision flag
public record TilePath(String path, boolean collision) {
	public static TilePath of(String path) {
		return new TilePath(path, false);
	}
	
	public static TilePath of(String path, boolean collision) {
		return new TilePath(path, collision);
	}
}
