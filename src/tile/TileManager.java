package tile;

import javafx.scene.image.Image;

import tile.utils.TilePath;

import game.GamePanel;

// Implement checking for special properties if player isOnTile
// Implement checking for edge jump if floor - 1 exists

public class TileManager {

	GamePanel gp;
	
	// Shared tile set
	public Tile[] baseTile;
	public Tile[] terrainTile;
	public Tile[] objectTile;

	int debug = 0;

	private enum TileType { BASE, TERRAIN, OBJECT }
	
	// Special tiles number
	public static final int EMPTY = 0;
	public static final int STAIRS_UP1 = 55;
	public static final int STAIRS_UP2 = 56;
	public static final int STAIRS_UP3 = 57;
	public static final int STAIRS_DOWN1 = 174;
	public static final int STAIRS_DOWN2 = 175;
	public static final int STAIRS_DOWN3 = 176;

	public TileManager(GamePanel gp) {

		this.gp = gp;
		this.baseTile = new Tile[2];
		this.terrainTile = new Tile[30];
		this.objectTile = new Tile[200];

		initializeTiles();
	}	
	
	// Initialize tile set for terrain and objects
	private void initializeTiles() {
		
		loadTileSet(baseTile, TileType.BASE);
		loadTileSet(terrainTile, TileType.TERRAIN);
		loadTileSet(objectTile, TileType.OBJECT);
	}
	
	// Load tile set from file
	private void loadTileSet(Tile[] tileSet, TileType type) {
		
		// Initialize all tiles with default values
		for (int i = 0; i < tileSet.length; i++) {
			tileSet[i] = new Tile();
		}
		
		try {
			for (int i = 0; i < tileSet.length; i++) {
				TilePath tilePath = getTilePath(i , type);
				
				// Prioritize collision regardless of image
				tileSet[i].setCollision(tilePath.collision());

				// Handle empty invisible tiles
				// TODO: Remove null check as it should not be null for non 0
				if (i == 0 || tilePath.path() == null) {
					if ( i != 0) {
						gp.debugUtils.logTileInfo("No image for " + type + " tile " + i);
					}
                    continue;
                }

				// Load tile image for non-empty tiles
				Tile newTile = createTile(tilePath);
				if (newTile != null && newTile.image != null) {
					tileSet[i].image = newTile.image;
				}

			}
		} catch (IllegalArgumentException e) {
            gp.debugUtils.logError("Invalid image file path for " + type, e);
        }
	}
	
	// Create tile from file
	private Tile createTile(TilePath tilePath) {
		try {
			
			String imagePath = "/" + tilePath.path() + ".png";
			var stream = getClass().getResourceAsStream(imagePath);
			
			if (stream == null) {
                throw new IllegalArgumentException("Cannot find file: " + imagePath);
            }

			Tile tile = new Tile();
			tile.setCollision(tilePath.collision());
			tile.image = new Image(stream);
			
			return tile;
		} catch (IllegalArgumentException e) {
			System.err.println("Invalid image file path: " + e.getMessage());
		} catch (RuntimeException e) {
            System.err.println("Error loading image: " + e.getMessage());
		}

		return null;
	}
	
	// Get tile path from tile type and index
	private TilePath getTilePath(int index, TileType type) {
		return switch (type) {
			case BASE -> getBaseTilePath(index);
			case TERRAIN -> getTerrainTilePath(index);
			case OBJECT -> getObjectTilePath(index);
		};
	}
	
	private TilePath getBaseTilePath(int index) {
		
		String path = "tiles/base/";
        return switch(index) {
            case EMPTY -> TilePath.of(null, true);
            case 1 -> buildTilePath(path, "water/00");
            default -> throw new IllegalArgumentException("Invalid base tile index: " + index);
        };
	}
	
	private TilePath getTerrainTilePath(int index) {

		String path = "tiles/terrain/";
		return switch(index) {
			case EMPTY -> TilePath.of(null, true);
			case 1 -> buildTilePath(path, "grass");

			/* Road */
			case 2 -> buildTilePath(path, "road/00");
			case 3 -> buildTilePath(path, "road/01");
			case 4 -> buildTilePath(path, "road/02");
			case 5 -> buildTilePath(path, "road/03");
			case 6 -> buildTilePath(path, "road/04");
			case 7 -> buildTilePath(path, "road/05");
			case 8 -> buildTilePath(path, "road/06");
			case 9 -> buildTilePath(path, "road/07");
			case 10 -> buildTilePath(path, "road/08");
			// Intersection tiles top left to bottom right
			case 11 -> buildTilePath(path, "road/09");
			case 12 -> buildTilePath(path, "road/10");
			case 13 -> buildTilePath(path, "road/11");
			case 14 -> buildTilePath(path, "road/12");
			// Water
			case 15 -> buildTilePath(path, "water", true);

            /* Edge */
			case 16 -> buildTilePath(path, "edge/01", true);
			case 17 -> buildTilePath(path, "edge/02", true);
			case 18 -> buildTilePath(path, "edge/03", true);
			case 19 -> buildTilePath(path, "edge/04", true);
			case 20 -> buildTilePath(path, "edge/05", true);
			case 21 -> buildTilePath(path, "edge/06");
			case 22 -> buildTilePath(path, "edge/07", true);
			case 23 -> buildTilePath(path, "edge/08", true);
			// Edge Intersection tiles
			case 24 -> buildTilePath(path, "edge/09");
			case 25 -> buildTilePath(path, "edge/10");
			case 26 -> buildTilePath(path, "edge/11", true);
			case 27 -> buildTilePath(path, "edge/12", true);
			default -> throw new IllegalArgumentException("Invalid terrain tile index: " + index);
		};
    }
	
	private TilePath getObjectTilePath(int index) {

		String path = "objects/";
		return switch(index) {
			case EMPTY -> TilePath.of(null);
			case 1 -> buildTilePath(path, "wall", true);
			case 2 -> buildTilePath(path, "pokeball", true);
			case 3 -> buildTilePath(path, "bush");

			/* Fences */
		    case 4 -> buildTilePath(path, "fence/00", true);
		    case 5 -> buildTilePath(path, "fence/01", true);
		    case 6 -> buildTilePath(path, "fence/02", true);
			
			/* Poke Center */
		    case 7 -> buildTilePath(path, "building/pokecenter/00", true);
		    case 8 -> buildTilePath(path, "building/pokecenter/01", true);
		    case 9 -> buildTilePath(path, "building/pokecenter/02", true);
		    case 10 -> buildTilePath(path, "building/pokecenter/03", true);
		    case 11 -> buildTilePath(path, "building/pokecenter/04", true);
		    case 12 -> buildTilePath(path, "building/pokecenter/05", true);
		    case 13 -> buildTilePath(path, "building/pokecenter/06", true);
		    case 14 -> buildTilePath(path, "building/pokecenter/07", true);
		    case 15 -> buildTilePath(path, "building/pokecenter/08", true);
		    case 16 -> buildTilePath(path, "building/pokecenter/09", true);
		    case 17 -> buildTilePath(path, "building/pokecenter/10", true);
		    case 18 -> buildTilePath(path, "building/pokecenter/11", true);
		    case 19 -> buildTilePath(path, "building/pokecenter/12", true);
		    case 20 -> buildTilePath(path, "building/pokecenter/13", true);
		    case 21 -> buildTilePath(path, "building/pokecenter/14", true);
		    case 22 -> buildTilePath(path, "building/pokecenter/15", true);
		    case 23 -> buildTilePath(path, "building/pokecenter/16", true);
		    case 24 -> buildTilePath(path, "building/pokecenter/17", true);
		    case 25 -> buildTilePath(path, "building/pokecenter/18", true);
		    case 26 -> buildTilePath(path, "building/pokecenter/19", true);
		    case 27 -> buildTilePath(path, "building/pokecenter/20");
		    case 28 -> buildTilePath(path, "building/pokecenter/21", true);
		    case 29 -> buildTilePath(path, "building/pokecenter/22", true);
		    case 30 -> buildTilePath(path, "building/pokecenter/23", true);
		    case 31 -> buildTilePath(path, "building/pokecenter/24", true);
			case 32 -> buildTilePath(path, "building/pokecenter/25", true);
			case 33 -> buildTilePath(path, "building/pokecenter/26");
			case 34 -> buildTilePath(path, "building/pokecenter/27");
			case 35 -> buildTilePath(path, "building/pokecenter/28", true);
			case 36 -> buildTilePath(path, "building/pokecenter/29", true);
			case 37 -> buildTilePath(path, "building/pokecenter/30", true);
			case 38 -> buildTilePath(path, "building/pokecenter/31");
			case 39 -> buildTilePath(path, "building/pokecenter/32");
			case 40 -> buildTilePath(path, "building/pokecenter/33");
			case 41 -> buildTilePath(path, "building/pokecenter/34");
			case 42 -> buildTilePath(path, "building/pokecenter/35");
			case 43 -> buildTilePath(path, "building/pokecenter/36");
			case 44 -> buildTilePath(path, "building/pokecenter/37");
			case 45 -> buildTilePath(path, "building/pokecenter/38");
			case 46 -> buildTilePath(path, "building/pokecenter/39");
			case 47 -> buildTilePath(path, "building/pokecenter/40");
			case 48 -> buildTilePath(path, "building/pokecenter/41");
			case 49 -> buildTilePath(path, "building/pokecenter/42");
			case 50 -> buildTilePath(path, "building/pokecenter/43");
			case 51 -> buildTilePath(path, "building/pokecenter/44");
			
			/* Lamp */
			case 52 -> buildTilePath(path, "lamp/00");
			case 53 -> buildTilePath(path, "lamp/01");
			case 54 -> buildTilePath(path, "lamp/02", true);

			/* STAIRS_UP */
			case 55 -> buildTilePath(path, "stair/00");
			case 56 -> buildTilePath(path, "stair/01");
			case 57 -> buildTilePath(path, "stair/02");

			/* Tree */
			case 58 -> buildTilePath(path, "tree/00");
			case 59 -> buildTilePath(path, "tree/01");
			case 60 -> buildTilePath(path, "tree/02");
			case 61 -> buildTilePath(path, "tree/03");
			case 62 -> buildTilePath(path, "tree/04");
			case 63 -> buildTilePath(path, "tree/05");
			case 64 -> buildTilePath(path, "tree/06");
			case 65 -> buildTilePath(path, "tree/07", true);
			case 66 -> buildTilePath(path, "tree/08", true);
			case 67 -> buildTilePath(path, "tree/09");
			case 68 -> buildTilePath(path, "tree/10");
			case 69 -> buildTilePath(path, "tree/11", true);
			case 70 -> buildTilePath(path, "tree/12", true);
			case 71 -> buildTilePath(path, "tree/13");
			case 72 -> buildTilePath(path, "tree/14");
			case 73 -> buildTilePath(path, "tree/15");
			case 74 -> buildTilePath(path, "tree/16");
			case 75 -> buildTilePath(path, "tree/17");

			/* Mailbox */
			case 76 -> buildTilePath(path, "mailbox/00");
			case 77 -> buildTilePath(path, "mailbox/01", true);
			
            /* Fence 2 */
			case 78 -> buildTilePath(path, "fence2/00", true);
			case 79 -> buildTilePath(path, "fence2/01", true);
			case 80 -> buildTilePath(path, "fence2/02", true);
			case 81 -> buildTilePath(path, "fence2/03", true);
			
			/* Wharf */
			case 82 -> buildTilePath(path, "wharf/00");
			case 83 -> buildTilePath(path, "wharf/01");
			case 84 -> buildTilePath(path, "wharf/02");
			case 85 -> buildTilePath(path, "wharf/03");
			case 86 -> buildTilePath(path, "wharf/04");
			case 87 -> buildTilePath(path, "wharf/05");
			case 88 -> buildTilePath(path, "wharf/06");
			case 89 -> buildTilePath(path, "wharf/07");
			case 90 -> buildTilePath(path, "wharf/08");
			case 91 -> buildTilePath(path, "wharf/09");
			case 92 -> buildTilePath(path, "wharf/10");
			case 93 -> buildTilePath(path, "wharf/11");
			
			/* Ship */
			case 94 -> buildTilePath(path, "ship/00");
			case 95 -> buildTilePath(path, "ship/01");
			case 96 -> buildTilePath(path, "ship/02");
			case 97 -> buildTilePath(path, "ship/03");
			case 98 -> buildTilePath(path, "ship/04");
			case 99 -> buildTilePath(path, "ship/05");
			case 100 -> buildTilePath(path, "ship/06");
			case 101 -> buildTilePath(path, "ship/07");
			case 102 -> buildTilePath(path, "ship/08");
			case 103 -> buildTilePath(path, "ship/09");
			case 104 -> buildTilePath(path, "ship/10");
			case 105 -> buildTilePath(path, "ship/11");
			case 106 -> buildTilePath(path, "ship/12");
			case 107 -> buildTilePath(path, "ship/13");
			case 108 -> buildTilePath(path, "ship/14");
			case 109 -> buildTilePath(path, "ship/15");
			case 110 -> buildTilePath(path, "ship/16");
			case 111 -> buildTilePath(path, "ship/17");
			case 112 -> buildTilePath(path, "ship/18");
			case 113 -> buildTilePath(path, "ship/19");
			
			/* hedge */
			case 114 -> buildTilePath(path, "hedge/00");
			case 115 -> buildTilePath(path, "hedge/01", true);
			case 116 -> buildTilePath(path, "hedge/02", true);
			case 117 -> buildTilePath(path, "hedge/03", true);
			case 118 -> buildTilePath(path, "hedge/04");
			case 119 -> buildTilePath(path, "hedge/05", true);
			
			/* white fence */
			case 120 -> buildTilePath(path, "fence3/00", true);
			case 121 -> buildTilePath(path, "fence3/01", true);
			case 122 -> buildTilePath(path, "fence3/02", true);
			case 123 -> buildTilePath(path, "fence3/03", true);
			case 124 -> buildTilePath(path, "fence3/04", true);
			case 125 -> buildTilePath(path, "fence3/05", true);
			case 126 -> buildTilePath(path, "fence3/06", true);
			
			/* host */
			case 127 -> buildTilePath(path, "host/00", true);
			case 128 -> buildTilePath(path, "host/01", true);
			case 129 -> buildTilePath(path, "host/02", true);
			case 130 -> buildTilePath(path, "host/03", true);
			case 131 -> buildTilePath(path, "host/04", true);
			case 132 -> buildTilePath(path, "host/05", true);
			case 133 -> buildTilePath(path, "host/06", true);
			case 134 -> buildTilePath(path, "host/07", true);
			case 135 -> buildTilePath(path, "host/08", true);
			case 136 -> buildTilePath(path, "host/09", true);
			case 137 -> buildTilePath(path, "host/10", true);
			case 138 -> buildTilePath(path, "host/11", true);
			case 139 -> buildTilePath(path, "host/12", true);
			case 140 -> buildTilePath(path, "host/13", true);
			case 141 -> buildTilePath(path, "host/14", true);
			case 142 -> buildTilePath(path, "host/15", true);
			case 143 -> buildTilePath(path, "host/16", true);
			case 144 -> buildTilePath(path, "host/17", true);
			case 145 -> buildTilePath(path, "host/18");
			case 146 -> buildTilePath(path, "host/19", true);
			case 147 -> buildTilePath(path, "host/20", true);
			case 148 -> buildTilePath(path, "host/21", true);
			case 149 -> buildTilePath(path, "host/22", true);
			case 150 -> buildTilePath(path, "host/23", true);
			case 151 -> buildTilePath(path, "host/24", true);
			case 152 -> buildTilePath(path, "host/25");
			case 153 -> buildTilePath(path, "host/26", true);
			case 154 -> buildTilePath(path, "host/27");
			case 155 -> buildTilePath(path, "host/28", true);
			case 156 -> buildTilePath(path, "host/29", true);
			case 157 -> buildTilePath(path, "host/30", true);
			case 158 -> buildTilePath(path, "host/31", true);
			case 159 -> buildTilePath(path, "host/32");
			case 160 -> buildTilePath(path, "host/33");
			case 161 -> buildTilePath(path, "host/34");
			case 162 -> buildTilePath(path, "host/35");
			case 163 -> buildTilePath(path, "host/36", true);
			case 164 -> buildTilePath(path, "host/37", true);
			case 165 -> buildTilePath(path, "host/38", true);
			case 166 -> buildTilePath(path, "host/39");
			case 167 -> buildTilePath(path, "host/40");
			case 168 -> buildTilePath(path, "host/41");
			case 169 -> buildTilePath(path, "host/42");
			case 170 -> buildTilePath(path, "host/43");
			case 171 -> buildTilePath(path, "host/44");
			case 172 -> buildTilePath(path, "host/45");
			case 173 -> buildTilePath(path, "host/46");
			
			/* STAIRS_DOWN*/
			case 174 -> buildTilePath(path, "stair/00");
			case 175 -> buildTilePath(path, "stair/01");
			case 176 -> buildTilePath(path, "stair/02");
			
			default -> throw new IllegalArgumentException("Invalid object tile index: " + index);
		};
	}
	
	private TilePath buildTilePath(String type, String path) {

	    return TilePath.of(type + path);
	}

	private TilePath buildTilePath(String type, String path, boolean collision) {

	    return TilePath.of(type + path, collision);
	}
}