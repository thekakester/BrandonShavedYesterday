/**An explicit definition of properties of the game object**/
game.entities = [];		//Usage: game.entities[entityID]
game.player = null;		//Usage: game.player  (reference to player entity)
game.map = {
	delta: {},
	tile: [],
	set: function(row,col,value) {
		this[row][col] = value;
		this.delta[row + "|" + col + "|" + value] = null;
	}
};
game.collidableTiles = [];	//An array of unpassable tiles
game.collidableEntities = [];//Array of entities that can't be walked on
game.hiddenEntities = [];	//Array of things that should be hidden to the player (unless debug mode)
game.killableEntities = [];
game.overlayEntities = [];	//Entities that appear above everything
game.underlayEntities = [];	//Entities that appear under everything else
game.warps = [];					//A set of warps.  These also exist in spawnerEntities and entities 
game.waitingForServerResponse = false;				//When true, disables warps until server responds
game.movementQueue = new Queue();	//Where the player has moved since we last told the server
game.type = "menu";
game.ping = 0;
game.message = [];			//If game.message.length() > 0, this will display a message to the user
game.appendMessage = "";	//DEBUG ONLY: Append this to the end of the message sent to the server
game.debug = {};
game.debug.enabled = false;
game.debug.selected = 0;
game.debug.entities = []	//Index entities by x and y position for easier level design
game.debug.tiles = []		//Index tiles by x and y position for easier level design
game.debug.entities.maxX = 0;
game.debug.entities.maxY = 0;
game.debug.tiles.maxX = 0;
game.debug.tiles.maxY = 0;
game.debug.savableEntitites = [];
game.debug.selectedX = 0;
game.debug.selectedY = 0;
game.debug.speedBoost = 0;
game.debug.row = 0;	//Row 0 is tiles, 1 is entities
game.debug.brushSize = 1;
game.debug.circleFill = false;
game.debug.lastFPSPrint = 0;
game.debug.frameCount = 0;
game.debug.lastFPS = 0;
playerPath = null;

/*******************************************************************************
* INITIALIZATION                                                               *
*******************************************************************************/
game.init = function () {
	engine.preloadImage("assets/tiles.png","tiles");
	engine.preloadImage("assets/characters.png","characters");
	engine.preloadImage("assets/objects.png","objects");
	engine.preloadImage("assets/darkerTiles.png","darkerTiles");
	engine.preloadImage("assets/darkerCharacters.png","darkerCharacters");
	engine.preloadImage("assets/objectFull.png","everything")
	engine.preloadImage("assets/items.png","items");
	engine.onImagesLoaded(function() { begin_loadSprites(); });

}

function begin_loadSprites() {
	
	//ARRAY DECLARATIONS  -- to make math easier
	var grid = [0,320,640,960,1280,1600,1920,2240,2560,2880];
	var cell = [0,32,64,96,128,160,192,224,256,288];
	
	//The tag for tile sprites is "tile" followed by the tile number.
	//Example: "tile0" is grass, "tile1" is flowers, etc.
	game.uniqueTileIDs = 0;
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"tiles",32,32);	//Sprite: Plain grass
	tmp.addFrame(32,0,15);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"tiles",32,32);	//Sprite: Flowers
	tmp.addFrame(64,0,10);
	tmp.addFrame(96,0,10);
	tmp.addFrame(128,0,10);
	tmp.addFrame(160,0,10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"tiles",32,32);	//Sprite: Med Grass
	tmp.addFrame(0,32,15);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"tiles",32,32);	//Sprite: Long Grass
	tmp.addFrame(32,32,15);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"tiles",32,32);	//Sprite: Sandstone
	tmp.addFrame(64,32,15);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"tiles",32,32);	//Sprite: Water
	tmp.addFrame(96,32,15);
	tmp.addFrame(128,32,15);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"tiles",32,32);	//Sprite: Vert Bridge
	tmp.addFrame(160,32,15);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"tiles",32,32);	//Sprite: Horiz Bridge
	tmp.addFrame(0,64,15);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"tiles",32,32);	//Sprite: Stone
	tmp.addFrame(32,64,15);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"tiles",32,32);	//Sprite: Lava
	tmp.addFrame(64,64,10);
	tmp.addFrame(96,64,10);
	tmp.addFrame(128,64,10);
	tmp.addFrame(160,64,10);
	tmp.addFrame(128,64,10);
	tmp.addFrame(96,64,10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"darkerTiles",32,32);	//Sprite: Darker Plain grass
	tmp.addFrame(32,0,15);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"darkerTiles",32,32);	//Sprite: Darker Flowers
	tmp.addFrame(64,0,10);
	tmp.addFrame(96,0,10);
	tmp.addFrame(128,0,10);
	tmp.addFrame(160,0,10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"darkerTiles",32,32);	//Sprite: Darker Med Grass
	tmp.addFrame(0,32,15);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"darkerTiles",32,32);	//Sprite: Darker Long Grass
	tmp.addFrame(32,32,15);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"darkerTiles",32,32);	//Sprite: Darker Sandstone
	tmp.addFrame(64,32,15);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"darkerTiles",32,32);	//Sprite: Darker Water
	tmp.addFrame(96,32,15);
	tmp.addFrame(128,32,15);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"darkerTiles",32,32);	//Sprite: Darker Vert Bridge
	tmp.addFrame(160,32,15);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"darkerTiles",32,32);	//Sprite: Darker Horiz Bridge
	tmp.addFrame(0,64,15);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"darkerTiles",32,32);	//Sprite: Darker Stone
	tmp.addFrame(32,64,15);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"darkerTiles",32,32);	//Sprite: Miasma
	tmp.addFrame(64,64,10);
	tmp.addFrame(96,64,10);
	tmp.addFrame(128,64,10);
	tmp.addFrame(160,64,10);
	tmp.addFrame(128,64,10);
	tmp.addFrame(96,64,10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"darkerTiles",32,32);	//Sprite: Black Void
	tmp.addFrame(0,0,10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Indoor wood
	tmp.addFrame(2752,320,10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Dark Path Vertical 1
	tmp.addFrame(grid[1]+cell[6],grid[0]+cell[0],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Dark Path Vertical 2
	tmp.addFrame(grid[1]+cell[6],grid[0]+cell[1],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Dark Path Vertical 3
	tmp.addFrame(grid[1]+cell[6],grid[0]+cell[2],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Dark Path Horizontal 1
	tmp.addFrame(grid[1]+cell[6],grid[0]+cell[3],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Dark Path Horizontal 2
	tmp.addFrame(grid[1]+cell[6],grid[0]+cell[4],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Dark Path End Top
	tmp.addFrame(grid[1]+cell[6],grid[0]+cell[5],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Dark Path Corner 1
	tmp.addFrame(grid[1]+cell[7],grid[0]+cell[0],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Dark Path Corner 2
	tmp.addFrame(grid[1]+cell[7],grid[0]+cell[1],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Dark Path Corner 3
	tmp.addFrame(grid[1]+cell[7],grid[0]+cell[2],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Dark Path Corner 4
	tmp.addFrame(grid[1]+cell[7],grid[0]+cell[3],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Dark Path Horizontal 3
	tmp.addFrame(grid[1]+cell[7],grid[0]+cell[4],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Dark Path End Down
	tmp.addFrame(grid[1]+cell[7],grid[0]+cell[5],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Dark Path Corner 5
	tmp.addFrame(grid[1]+cell[8],grid[0]+cell[0],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Dark Path Corner 6
	tmp.addFrame(grid[1]+cell[8],grid[0]+cell[1],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Dark Path Corner 7
	tmp.addFrame(grid[1]+cell[8],grid[0]+cell[2],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Dark Path Corner 8
	tmp.addFrame(grid[1]+cell[8],grid[0]+cell[3],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Dark Path End Right
	tmp.addFrame(grid[1]+cell[8],grid[0]+cell[4],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Dark Path End Left
	tmp.addFrame(grid[1]+cell[8],grid[0]+cell[5],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Path Vertical 1
	tmp.addFrame(grid[2]+cell[6],grid[0]+cell[0],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Path Vertical 2
	tmp.addFrame(grid[2]+cell[6],grid[0]+cell[1],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Path Vertical 3
	tmp.addFrame(grid[2]+cell[6],grid[0]+cell[2],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Path Horizontal 1
	tmp.addFrame(grid[2]+cell[6],grid[0]+cell[3],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Path Horizontal 2
	tmp.addFrame(grid[2]+cell[6],grid[0]+cell[4],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Path End Top
	tmp.addFrame(grid[2]+cell[6],grid[0]+cell[5],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Path Corner 1
	tmp.addFrame(grid[2]+cell[7],grid[0]+cell[0],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Path Corner 2
	tmp.addFrame(grid[2]+cell[7],grid[0]+cell[1],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Path Corner 3
	tmp.addFrame(grid[2]+cell[7],grid[0]+cell[2],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Path Corner 4
	tmp.addFrame(grid[2]+cell[7],grid[0]+cell[3],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Path Horizontal 3
	tmp.addFrame(grid[2]+cell[7],grid[0]+cell[4],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Path End Down
	tmp.addFrame(grid[2]+cell[7],grid[0]+cell[5],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Path Corner 5
	tmp.addFrame(grid[2]+cell[8],grid[0]+cell[0],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Path Corner 6
	tmp.addFrame(grid[2]+cell[8],grid[0]+cell[1],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Path Corner 7
	tmp.addFrame(grid[2]+cell[8],grid[0]+cell[2],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Path Corner 8
	tmp.addFrame(grid[2]+cell[8],grid[0]+cell[3],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Path End Right
	tmp.addFrame(grid[2]+cell[8],grid[0]+cell[4],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Path End Left
	tmp.addFrame(grid[2]+cell[8],grid[0]+cell[5],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Path End Left
	tmp.addFrame(grid[2]+cell[8],grid[0]+cell[5],10);
	
	//The water update
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Dark Water 1
	tmp.addFrame(grid[1]+cell[8],grid[3]+cell[9],10);
	tmp.addFrame(grid[1]+cell[9],grid[3]+cell[9],10);
	tmp.addFrame(grid[2]+cell[0],grid[3]+cell[9],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Light Water 1
	tmp.addFrame(grid[2]+cell[1],grid[3]+cell[9],10);
	tmp.addFrame(grid[2]+cell[2],grid[3]+cell[9],10);
	tmp.addFrame(grid[2]+cell[3],grid[3]+cell[9],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Dark Water 2
	tmp.addFrame(grid[1]+cell[8],grid[4]+cell[0],10);
	tmp.addFrame(grid[1]+cell[9],grid[4]+cell[0],10);
	tmp.addFrame(grid[2]+cell[0],grid[4]+cell[0],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Light Water 2
	tmp.addFrame(grid[2]+cell[1],grid[4]+cell[0],10);
	tmp.addFrame(grid[2]+cell[2],grid[4]+cell[0],10);
	tmp.addFrame(grid[2]+cell[3],grid[4]+cell[0],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Dark Water 3
	tmp.addFrame(grid[1]+cell[8],grid[4]+cell[1],10);
	tmp.addFrame(grid[1]+cell[9],grid[4]+cell[1],10);
	tmp.addFrame(grid[2]+cell[0],grid[4]+cell[1],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Light Water 3
	tmp.addFrame(grid[2]+cell[1],grid[4]+cell[1],10);
	tmp.addFrame(grid[2]+cell[2],grid[4]+cell[1],10);
	tmp.addFrame(grid[2]+cell[3],grid[4]+cell[1],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Dark Water 4
	tmp.addFrame(grid[1]+cell[8],grid[4]+cell[2],10);
	tmp.addFrame(grid[1]+cell[9],grid[4]+cell[2],10);
	tmp.addFrame(grid[2]+cell[0],grid[4]+cell[2],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Light Water 4
	tmp.addFrame(grid[2]+cell[1],grid[4]+cell[2],10);
	tmp.addFrame(grid[2]+cell[2],grid[4]+cell[2],10);
	tmp.addFrame(grid[2]+cell[3],grid[4]+cell[2],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Dark Water 5
	tmp.addFrame(grid[1]+cell[8],grid[4]+cell[3],10);
	tmp.addFrame(grid[1]+cell[9],grid[4]+cell[3],10);
	tmp.addFrame(grid[2]+cell[0],grid[4]+cell[3],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Light Water 5
	tmp.addFrame(grid[2]+cell[1],grid[4]+cell[3],10);
	tmp.addFrame(grid[2]+cell[2],grid[4]+cell[3],10);
	tmp.addFrame(grid[2]+cell[3],grid[4]+cell[3],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Dark Water 6
	tmp.addFrame(grid[1]+cell[8],grid[4]+cell[4],10);
	tmp.addFrame(grid[1]+cell[9],grid[4]+cell[4],10);
	tmp.addFrame(grid[2]+cell[0],grid[4]+cell[4],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Light Water 6
	tmp.addFrame(grid[2]+cell[1],grid[4]+cell[4],10);
	tmp.addFrame(grid[2]+cell[2],grid[4]+cell[4],10);
	tmp.addFrame(grid[2]+cell[3],grid[4]+cell[4],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Dark Water 7
	tmp.addFrame(grid[1]+cell[8],grid[4]+cell[5],10);
	tmp.addFrame(grid[1]+cell[9],grid[4]+cell[5],10);
	tmp.addFrame(grid[2]+cell[0],grid[4]+cell[5],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Light Water 7
	tmp.addFrame(grid[2]+cell[1],grid[4]+cell[5],10);
	tmp.addFrame(grid[2]+cell[2],grid[4]+cell[5],10);
	tmp.addFrame(grid[2]+cell[3],grid[4]+cell[5],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Dark Water Rocks 1
	tmp.addFrame(grid[1]+cell[8],grid[4]+cell[6],10);
	tmp.addFrame(grid[1]+cell[9],grid[4]+cell[6],10);
	tmp.addFrame(grid[2]+cell[0],grid[4]+cell[6],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Light Water Rocks 1
	tmp.addFrame(grid[2]+cell[1],grid[4]+cell[6],10);
	tmp.addFrame(grid[2]+cell[2],grid[4]+cell[6],10);
	tmp.addFrame(grid[2]+cell[3],grid[4]+cell[6],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Dark Water Rocks 2
	tmp.addFrame(grid[1]+cell[8],grid[4]+cell[7],10);
	tmp.addFrame(grid[1]+cell[9],grid[4]+cell[7],10);
	tmp.addFrame(grid[2]+cell[0],grid[4]+cell[7],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Light Water Rocks 2
	tmp.addFrame(grid[2]+cell[1],grid[4]+cell[7],10);
	tmp.addFrame(grid[2]+cell[2],grid[4]+cell[7],10);
	tmp.addFrame(grid[2]+cell[3],grid[4]+cell[7],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Dark Water Rocks 3
	tmp.addFrame(grid[1]+cell[8],grid[4]+cell[8],10);
	tmp.addFrame(grid[1]+cell[9],grid[4]+cell[8],10);
	tmp.addFrame(grid[2]+cell[0],grid[4]+cell[8],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Light Water Rocks 3
	tmp.addFrame(grid[2]+cell[1],grid[4]+cell[9],10);
	tmp.addFrame(grid[2]+cell[2],grid[4]+cell[9],10);
	tmp.addFrame(grid[2]+cell[3],grid[4]+cell[9],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Grass Tile Alt
	tmp.addFrame(grid[0]+cell[1],grid[4]+cell[9],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Sand Tile Alt
	tmp.addFrame(grid[0]+cell[1],grid[5]+cell[5],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Snow Tile Alt
	tmp.addFrame(grid[0]+cell[1],grid[6]+cell[1],10);

	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Ice Tile 1
	tmp.addFrame(grid[0]+cell[6],grid[6]+cell[3],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Ice Tile 2
	tmp.addFrame(grid[0]+cell[6],grid[6]+cell[4],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Ice Tile 3
	tmp.addFrame(grid[0]+cell[6],grid[6]+cell[5],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Ice Tile 4
	tmp.addFrame(grid[0]+cell[6],grid[6]+cell[6],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Ice Tile 5
	tmp.addFrame(grid[0]+cell[7],grid[6]+cell[3],10);

	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Ice Tile 6
	tmp.addFrame(grid[0]+cell[7],grid[6]+cell[4],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Ice Tile 7
	tmp.addFrame(grid[0]+cell[7],grid[6]+cell[5],10);
	
	var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Sprite: Ice Tile 8
	tmp.addFrame(grid[0]+cell[7],grid[6]+cell[6],10);

	
	var yOffset = grid[4]+cell[5];
	for(var i = 0; i < 6; i++){//Sprite: 
		//0 - Green, 1 - Green Inner, 2 - Sand, 3 - Sand Inner, 4 - Snow, 5 - Snow Inner
		for(var j = 0; j < 2; j++){//0 - Dark, 1 - Light
			for(var k = 0; k < 3;k++){ //0 - Upper, 1 - Center, 2 - Lower
				for(var l = 0; l < 3; l++){ //0 - Left, 1 - Center, 3 - Right
					var tmp = engine.createSprite("tile" + game.uniqueTileIDs++,"everything",32,32);	//Add all the things for sprite name
					tmp.addFrame(0+288*j+l*32,yOffset+i*96+k*32,10);
					tmp.addFrame(96+288*j+l*32,yOffset+i*96+k*32,10);
					tmp.addFrame(192+288*j+l*32,yOffset+i*96+k*32,10);
				}
			}
		}
	}
	
	
	//This is a bad hacky way to populate our debug menu
	for (var spriteTag in engine.__sprites) {
		//Get the sprite
		var sprite = engine.__sprites[spriteTag];
		
		//Get the x and y position of the first frame
		var x = sprite.__frames[0].x/32;
		var y = sprite.__frames[0].y/32;
		if (!game.debug.tiles[y]) {
			game.debug.tiles[y] = [];
		}
		game.debug.tiles[y][x] = spriteTag;
		game.debug.tiles.maxX = x+1 > game.debug.tiles.maxX ? x+1 : game.debug.tiles.maxX;
		game.debug.tiles.maxY = y+1 > game.debug.tiles.maxY ? y+1 : game.debug.tiles.maxY;
	}
	
	
	/**Entity sprites loaded on server and transmitted during init()**/
	
	//ATTACK SPRITES
	var tmp = engine.createSprite("attack0","items",32,32);	//Sprite: Default Attack
	for (var atFrame = 0; atFrame < 6; atFrame++) {
		tmp.addFrame(atFrame*32,0,1);
	}
	
	begin_serverInit();
}

/**For every movable character, there is 8 sprites.
Up walking, down walking, left walking, right walking, up idle, down idle, left idle, right idle.
This is just a faster way of creating them.
Arguments:
	baseTag: The name of the sprite idle.  Eg "entity1"
	imageTag: the source of this sprite.  Eg "characters"
	width,height: Width and height of each frame
	startX,startY: coordinates of top left of image.
Image must be in form: 3 frames wide, 4 tall
Row1: down(step1), down(idle), down(step2)
Row2: left(step1), left(idle), left(step2)
Row3: rigt(step1), rigt(idle), rigt(step2)
Row4: up  (step1), up  (idle), up  (step2)
Creates tags in format: <baseTag>_<direction>[_w]
Example: entity1_2_w is entity 1's left walking animation
Example: entity2_0 is entity 2's up idle
*/
function createWalkingAnimSprites(baseTag,imageTag,width,height,startX,startY,optionalXOffset,optionalYOffset) {
	if (!optionalXOffset) { optionalXOffset = 0;}
	if (!optionalYOffset) { optionalYOffset = 0;}
	
	var duration = 3;
	//Row order = down, left, right, up

	//WALKING & IDLE ANIMATIONS
	for (var row = 0; row < 4; row++) {
		var direction = (row + 1) % 4;	//Converts from order of spritesheet to our order (u/d/l/r);
		var yOff = row * height;
		
		//WALKING
		var tmp = engine.createSprite(baseTag+"_" + direction + "_w",imageTag,width,height,optionalXOffset,optionalYOffset);//Down
		for (var i = 0; i < 4; i++) {
			var xOff = i;
			if (xOff == 3) { xOff = 1; }	//Back to inbetween
			xOff*=width;
			tmp.addFrame(startX+xOff,startY+yOff,duration)
		}
		
		//IDLE
		var tmp = engine.createSprite(baseTag+"_" + direction,imageTag,width,height,optionalXOffset,optionalYOffset);//Down
		tmp.addFrame(startX+width,startY+yOff,duration);
	}
	
	//DEFAULT ANIMATION
	var tmp = engine.createSprite(baseTag,imageTag,width,height,optionalXOffset,optionalYOffset);//Down
	tmp.addFrame(startX+width,startY,duration);
}

function begin_serverInit() {
	//Async Load map
	console.log("Getting player ID and map");
	engine.sendMessage("init=1",function(response) {
		var buffer = ByteBuffer.wrap(response);
		
		/* GET PLAYER ID */
		var pid = buffer.getInt();
		
		//Set the player entity
		game.player = new Entity(pid,1,0,0);	//Type 1: player
		game.entities[pid] = game.player;
		console.log("Player " + game.player.id + ": " + game.player.x + " " + game.player.y);
		
		/* GET UNPASSABLE TILES */
		var count = buffer.getInt();
		for (var i = 0; i < count; i++) {
			game.collidableTiles[buffer.getInt()] = true;
		}
		
		/* Get info about entities */
		game.uniqueEntityIDs = buffer.getInt();
		console.log("Loading definitions for " + game.uniqueEntityIDs + " entities");
		for (var type = 0; type < game.uniqueEntityIDs; type++) {
			var collidableOrSpawner = buffer.getByte();
			if (collidableOrSpawner & 0x1) {game.collidableEntities[type] = true;}	//Bit 1
			if (collidableOrSpawner & 0x2) {game.hiddenEntities[type] = true;}		//Bit 2
			if (collidableOrSpawner & 0x4) {game.hiddenEntities[type] = true;}		//Bit 3 //Trigger
			if (collidableOrSpawner & 0x8) {game.underlayEntities[type] = true;}		//Bit 4 //isunderlay
			if (collidableOrSpawner & 0x16) {game.overlayEntities[type] = true;}	//Bit 5 //isoverlay

			var hp = buffer.getInt();
			if (hp > 0) { game.killableEntities[type] = true;}
			var startX = buffer.getInt()/32;
			var startY = buffer.getInt()/32;
			var savable = buffer.getByte();
			var taglen = buffer.getInt();
			var srcImageTag = "";
			for (var i = 0; i < taglen; i++) {
				srcImageTag += buffer.getChar();
			}
			
			//For debug mode, store this indexed by the x and y position
			if (!game.debug.entities[startY]) {
				game.debug.entities[startY] = [];
			}
			game.debug.entities[startY][startX] = "entity" + type;
			//+1 because we want x % maxX to be x, but x+1 should loop back to 0
			game.debug.entities.maxX = startX+1 > game.debug.entities.maxX ? startX+1 : game.debug.entities.maxX;
			game.debug.entities.maxY = startY+1 > game.debug.entities.maxY ? startY+1 : game.debug.entities.maxY;
			
			if (savable==1) {game.debug.savableEntitites[type] = true;}
			
			//Load the sprite info
			var spriteCount = buffer.getInt();
			
			for (var i = 0; i < spriteCount; i++) {
				var width = buffer.getInt();
				var height = buffer.getInt();
				var xOffset = buffer.getInt();
				var yOffset = buffer.getInt();
				var taglen = buffer.getInt();
				var spriteTag = "";
				for (var j = 0; j < taglen; j++) {
					spriteTag += buffer.getChar();
				}
				var duration = buffer.getInt();
				var sprite = engine.createSprite(spriteTag,srcImageTag,width,height,xOffset,yOffset);
				//console.log("Adding sprite: " + spriteTag);
				//Load the frames
				var framecount = buffer.getInt();
				for (var j = 0; j < framecount; j++) {
					sprite.addFrame(buffer.getInt(),buffer.getInt(),duration);
				}
			}
		}
		
		
		engine.enterGameLoop();	//Next step
	});
}

function begin_getPid() {
	//Async Get unique player ID
	console.log("Getting player ID");
	engine.sendMessage("getpid=1",function(response) {
		var buffer = ByteBuffer.wrap(response);
		
		
		
		engine.enterGameLoop();	//Next step
	});
}

/*******************************************************************************
* Server Communication                                                         *
*******************************************************************************/

//This method gets called every 100ms or so.  It will send the following message
//To the server.  Be sure to escape any sensitive data
game.sendMessage = function() {
	/*----------PLAYER-----------*/
	//Return the message to send to the server
	var message = "entity=" + game.player.id;
	//Send our movement queue (direction path.  Eg: up-up-left-down)
	while (!game.movementQueue.isEmpty()) {
		var direction = game.movementQueue.dequeue();
		message += "|" + direction;
	}
	
	
	/*----------MAP-------------*/
	//If something changed in the map, relay that information
	//Add this to the message
	var tmp = "&map=";
	
	//Key is in format row|col|tile
	for (var key in game.map.delta) {
		tmp += "|" + key;	//Note the extra "|" at the beginning!
	}
	
	//Make sure we actually have something to send
	if (tmp != "&map=") {
		message += tmp;
		game.map.delta = {};	//Clear our delta
	}
	
	//DEBUG STUFF (do we need to add anything else?)
	message += game.appendMessage;
	game.appendMessage = "";
	
	/*---------CHAT-----------*/
	//TODO put chat code here
	
	
	//Last call is always update.  This asks for what has changed
	message += "&update=" + game.player.id;
	
	game.pingStart = new Date().getTime();
	return message;
	
}

game.onServerRespond = function(response) {
	game.ping = new Date().getTime()-game.pingStart;
	game.waitingForServerResponse = false;	//Allow another warp request
	var buffer = ByteBuffer.wrap(response);
	while (true) {
		var responseType = buffer.getInt();
		if (responseType == 0) { break; }	//0 means end
		
		//Response 1: Entity Info Update
		if (responseType == 1) {
			var count = buffer.getInt();
			for (var i = 0; i < count; i++) {
				var eid = buffer.getInt();
				var type = buffer.getInt();
				var x = buffer.getInt();
				var y = buffer.getInt();
				var flags = buffer.getByte();
				
				var pathLen = buffer.getInt();
				var path = new Queue();
				for (var j = 0; j < pathLen; j++) {
					path.enqueue(buffer.getByte());
				}
				
				var timeElapsed = buffer.getInt();
				
				if (x == y && y == -1) {
					//He dead   TODO-----------------------------CHANGE THIS!  IF SOMETHING GOES TO -1 -1, do they deserve to die??????/??
					delete game.entities[eid];
					continue;
				}
				
				var e = game.entities[eid];
				if (!e) {
					console.log("Creating new entity: " + type);
					game.entities[eid] = new Entity(eid,type,x,y);
					e = game.entities[eid];
					e.isUnderlay = game.underlayEntities[type];
					e.isOverlay = game.overlayEntities[type];
				}
				
				//If we just got a path and didn't previously have one, tween to it
				if (e.path.isEmpty()) {
					e.tween = 0;
				}
				e.path = path;
				e.timeElapsedOnServer = timeElapsed + game.ping / 2;
				e.lastUpdate = new Date().getTime();
				e.x = x;
				e.y = y;
				e.type = type;
				e.dead = false;	//Just in case a player was marked as dead. Make sure they respawn
				
				if (e.type == 189 /**warp**/) {game.warps[e.id] = e;}
				e.hostile = flags & 0x1 == 1;	//Set hostile to true if the first bit of flags is "1"
				//If this was a forced update about ourself, clear our path
				if (e.id == game.player.id) { game.player.path = new Queue(); }
			}
		}
		
		//Response 2: Map update
		if (responseType == 2) {
			var count = buffer.getInt();
			for (var i = 0; i < count; i++) {
				var row = buffer.getInt();
				var col = buffer.getInt();
				var type = buffer.getInt();
				if (game.map.tile[row] == undefined) {
					game.map.tile[row] = [];
				}
				game.map.tile[row][col] = type;
			}
		}
		
		//Response 3: Chat
		if (responseType == 3) {
			var count = buffer.getInt();
			for (var i = 0; i < count; i++) {
				var length = buffer.getInt();
				var message = "";
				for(var j = 0 ; j < length; j++){
					message+= buffer.getChar();
				}
				appendMessage(message);
			}
		}
		
		//Response 4: Sign
		if (responseType == 4) {
			var lines = buffer.getInt();
			for (var i = 0; i < lines; i++) {
				var length = buffer.getInt();
				var message = "";
				for(var j = 0 ; j < length; j++){
					message+= buffer.getChar();
				}
				appendMessage(unescape(message));
			}
		}
		
		//Response 5: Attacking entities
		if (responseType == 5) {
			var entities = buffer.getInt();
			for (var i = 0; i < entities; i++) {
				var eid = buffer.getInt();
				startAttackSprite(eid);
			}
		}
		
		//Response 6: Dead entities
		if (responseType == 6) {
			var entities = buffer.getInt();
			for (var i = 0; i < entities; i++) {
				var eid = buffer.getInt();
				//This entitiy is dead
				delete game.entities[eid];
				console.log("Entity " + eid + " died");
			}
		}
		
		//Response 7: Notification
		if (responseType == 7) {
			var lines = buffer.getInt();
			for (var line = 0; line < lines; line++) {
				game.message[line] = "";
				var length = buffer.getInt();
				for (var c = 0; c < length; c++) {
					game.message[line] += buffer.getChar();
				}
				game.message[line] = unescape(game.message[line]);
			}
		}
		
		//Response 7: STALE MAP TILES
		if (responseType == 8) {
			var zones = buffer.getInt();
			for (var i = 0; i < zones; i++) {
				var startRow = buffer.getInt();
				var startCol = buffer.getInt();
				var endRow = buffer.getInt();
				var endCol = buffer.getInt();
				//Clear these from our map
				for (var r = startRow; r < endRow; r++) {
					for (var c = startCol; c < endCol; c++) {
						if (game.map.tile[r]) {
							delete game.map.tile[r][c];
						}
					}
				}
			}
		}
		
	}
}

/*******************************************************************************
* Update and Paint                                                             *
*******************************************************************************/

game.update = function() {
	if (game.type == "menu") {
		updateMenu();
	} else if (game.type=="terminal") {
		updateTerminal();
	} else {
		updateGame();
	}
}

game.paint = function () {
	
	if (game.type == "menu") {
		return paintMenu();
	} else if (game.type == "terminal") {
		return paintTerminal();
	} else {
		return paintGame();
	}
}

//******************
//* MENU           *
//******************
var selected = 0;
function updateMenu() {
	if (engine.isKeyPressed("ArrowDown") || engine.isKeyPressed("ArrowUp")) {
		selected=(selected+1)%2;
	}
	
	if (engine.isKeyPressed("Enter")) {
		game.type = selected==0 ? "virtual" : "terminal";
	}
}

function paintMenu() {
	paintGame();
	
	//Draw a blackish box
	engine.__context.fillStyle = "black";
	engine.__context.fillRect(250,250,300,100);
	
	/**TODO: Timing race between bitmap font and regular font**/
	engine.__context.font = "30px Arial";
	engine.__context.fillStyle = "white";
	
	var myText = "";
	
	myText = "virtual player";
	if (selected == 0) { myText = "> " + myText;}
	engine.__context.fillText(myText,260,290);
	
	myText = "terminal";
	if (selected == 1) { myText = "> " + myText;}
	engine.__context.fillText(myText,260,330);
}

//******************
//* TERMINAL       *
//******************
var terminalLineBuffer = [];
var terminalCommandBuffer = [];
var terminalCommandBufferMaxSize = 20;	//Arbitrary number
var terminalFontSize = 20;
var terminalLineBufferMaxSize = (engine.height / terminalFontSize) - 2;
var terminalHistoryIndex = 0;
function updateTerminal() {
	engine.__context.font = terminalFontSize + "px Consolas";
	engine.recordKeyboard(true);
	
	//USE UP/DOWN TO GO THROUGH HISTORY
	var getHistory = 0;
	if (engine.isKeyPressed("ArrowUp")) {
		getHistory = 1;	//Go back one history
	}
	if (engine.isKeyPressed("ArrowDown")) {
		getHistory = -1; //Go forward one history
	}
	if (getHistory != 0) {
		terminalHistoryIndex+=getHistory;
		if (terminalHistoryIndex < 0 || terminalHistoryIndex > terminalCommandBuffer.length) {
			terminalHistoryIndex-=getHistory
		} else {
			if (terminalHistoryIndex == 0) {engine.keyboardBuffer = "";}
			else {
				//Go back this many commands
				var command;
				var i = terminalCommandBuffer.length - terminalHistoryIndex + 1;
				if (i > 0) {
					for (command in terminalCommandBuffer) {
						i--;
						if (i == 0) {break;}
					}
					engine.keyboardBuffer = terminalCommandBuffer[command];
				}
			}
		}
	}
	
	if (engine.isKeyPressed("Enter")) {
		tPrint(engine.keyboardBuffer);
		queuelikeAdd(terminalCommandBuffer,terminalCommandBufferMaxSize,engine.keyboardBuffer);
		execute(engine.keyboardBuffer);
		engine.keyboardBuffer = "";
		terminalHistoryIndex = 0;
	}
}

function tPrint(line) {
	queuelikeAdd(terminalLineBuffer,terminalLineBufferMaxSize,line);
}

function execute(command) {
	var args = command.toLowerCase().split(" ");
	command = args[0];
	
	if (command == "help") {
		tPrint("List of commands:");
		tPrint("listentities:  Lists all the objects in the game");
		tPrint("settile <eid> <tilenum>:  Change the tile below entity \"eid\"");
		return;
	}
	
	if (command == "clear") {
		terminalHistoryIndex = 0;
		terminalLineBuffer = [];
		return;
	}
	
	if (command == "settile") {
		//Get the tile at eid's position
		var eid = args[1];
		if (game.entities[eid]) {
			if (args[2] && Number.isInteger(args[2])) {
				var x = game.entities[eid].x;
				var y = game.entities[eid].y;
				game.map.set(y,x,args[2])
			} else {
				
			}
		} else {
			tPrint("EID: " + eid + " doesn't exist");
		}
		return;
	}
	
	if (command == "listentities") {
		for (var eid in game.entities) {
			var e = game.entities[eid];
			tPrint(eid + ": (" + e.x + "," + e.y + ") " + e.name);
		}
		return;
	}
	
	//If we made it this far, the player needs help
	tPrint("ERROR: Unrecognized command.  Try typing help");
}

function paintTerminal() {
	var txt = "> " + engine.keyboardBuffer;
	
	
	//First line should be at the top
	var y = terminalFontSize;
	//Draw the buffer
	for (var line in terminalLineBuffer) {
		engine.__context.fillText(terminalLineBuffer[line],10,y);
		y+=terminalFontSize;
	}
	engine.__context.fillText(txt,10,y);
}

function queuelikeAdd(array,maxSize,line) {
	array.push(line);
	if (array.length > maxSize) {
		var index;
		//Get the first element
		for (index in array) {
			break;
		}
		array.splice(index, 1);
	}
}

//******************
//* GAME           *
//******************

function updateGame() {
	//Handle movement
	var dX = 0;
	var dY = 0;
	if (engine.isKeyDown("ArrowUp")) {dY--;}
	if (engine.isKeyDown("ArrowDown")) {dY++;}
	if (engine.isKeyDown("ArrowLeft")) {dX--;}
	if (engine.isKeyDown("ArrowRight")) {dX++;}
	move(dX,dY);
	
	if (engine.isKeyPressed("Escape")) {
		game.debug.enabled = !game.debug.enabled;
		game.debug.selected = 0;
		game.debug.row = 0;
		game.debug.brushSize = 0;
		console.log("Set debug mode to " + game.debug.enabled);
	}
	
	if (engine.isKeyPressed("Space")) {
		if(game.message.length > 0) {
			game.message = [];
		} else {
			//Get the position we're facing
			var x = game.player.x;
			var y = game.player.y;
		
			//Get the direction we're facing 
			if(game.player.direction == 0) { y--; }
			else if (game.player.direction == 1) { y++; }
			else if (game.player.direction == 2) { x--; }
			else if (game.player.direction == 3) { x++; }
			
			//Search for an entity here
			for (var key in game.entities) {
				var e = game.entities[key];
				if (e.type == 2 && e.x == x && e.y == y) {	//EntityType.Sign is #2
					game.appendMessage += "&sign=" + e.id;
				}
			}
		}
	}
	
	//If the player isn't attackng (no attack sprite running)
	//allow them to attack
	if (!engine.containsSprite("inst_att_"+game.player.id)) {
		if (engine.isKeyDown("Space")) {
			startAttackSprite(game.player.id);
			game.appendMessage+="&attack="+game.player.id;
			
			//Check if they're hitting anything
			var attackX = game.player.tweenX;
			var attackY = game.player.tweenY;
			if (game.player.direction == 0) { attackY--; }
			else if (game.player.direction == 1) { attackY++; }
			else if (game.player.direction == 2) { attackX--; }
			else if (game.player.direction == 3) { attackX++; }
			
			//Colliding if x and y are within 1 of eachother
			for (var eid in game.entities) {
				if (eid == game.player.id) { continue; }
				var e = game.entities[eid];
				if (game.killableEntities[e.type] && Math.abs(e.tweenX-attackX) < 1 && Math.abs(e.tweenY-attackY) < 1) {
					//He ded
					e.dead = true;	//Temporarily doesn't draw.  If player, server will re-aliveafy
					game.appendMessage="&d=" + eid;	//Tell server
				}
			}
		}
	}
	
	//If we're on a warp tile, warp
	if (!game.waitingForServerResponse && !game.debug.enabled) {
		for (var key in game.warps) {
			var warp = game.warps[key];
			if (warp.x == game.player.x && warp.y == game.player.y) {
				game.appendMessage="&warp=" + game.player.id + "|" + warp.id;
				game.waitingForServerResponse = true;	//Set to false when the server responds
				break;
			}
		}
	} //I like to live...dangerously. 
	
	//If getting attacked by an entity
	if(!game.waitingForServerResponse && !game.debug.enabled){
		for(var eid in game.entities){
			
			var e = game.entities[eid];
			if(!e.hostile){continue;} //carry on sir.
			var x = e.x;
			var y = e.y;
			if(e.direction == 0){y--;}
			else if(e.direction == 1){y++;}
			else if(e.direction == 2){x--;}
			else if(e.direction == 3){x++;}
			if(x == game.player.x && y == game.player.y){
				game.appendMessage+="&d="+game.player.id;
				game.waitingForServerResponse = true;
				break;
			}
		}
	}
	
	
	//Update the positions of all the entities based on elapsed time
	//WARNING: tilesPerSecond MUST BE EXACTLY THE SAME ON THE SERVER
	var defaultTPS = 6;	//Speed of entities
	for (var eid in game.entities) {
		var e = game.entities[eid];
		if (e.path.isEmpty()) { 
			tweenEntityToPosition(e,e.x,e.y);
			continue;
		}
		
		var tilesPerSecond = defaultTPS;
		if (game.debug.enabled && game.player.id == e.id) {tilesPerSecond += game.debug.speedBoost; }
		var millisecondsPerTile = 1000.0/tilesPerSecond;
		

		//What is the total elapsed time since this path was started
		//(server offset + time since it told us that)
		var timeElapsed = e.timeElapsedOnServer + (new Date().getTime()-e.lastUpdate);
		
	
		//How many tiles have we traveled since the beginning of the path
		var tilesTraveled = timeElapsed / millisecondsPerTile;
		
		//If we've traveled past our last tile, truncate it
		if (tilesTraveled > e.path.getLength()) { tilesTraveled = e.path.getLength(); }
		
		var mostRecentTile = Math.floor(tilesTraveled);	//Most recent tile fully reached
		var tween = tilesTraveled - mostRecentTile;	//time since this tile (note that last tile is too far back)
		
		//Calculate where we're supposed to be by looping through the path
		var dX = 0; var dY = 0;
		
		var endX = 0; var endY = 0;
		var direction = 0;
		
		//Update tiles that we've completely moved over
		for (var i = 0; i < mostRecentTile; i++) {
			direction = e.path.dequeue();
			
			if (direction == 0) { dY--; }
			else if (direction == 1) { dY++; }
			else if (direction == 2) { dX--; }
			else if (direction == 3) { dX++; }
			
			//If we're the player, log this to tell the server
			if (e.id == game.player.id) {
				game.movementQueue.enqueue(direction);
			}
		}
		
		//We've COMPLETELY moved dX and dY
		e.x += dX;
		e.y += dY;
		
		//Init end to start in case this is the last node
		var endX = e.x; var endY = e.y;
		
		//Whats the next tile
		if (!e.path.isEmpty()) {
			e.idle = false;
			direction = e.path.peek();
			if (direction == 0) { endY--; }
			else if (direction == 1) { endY++; }
			else if (direction == 2) { endX--; }
			else if (direction == 3) { endX++; }
		} else {
			e.idle = true;
		}
		
		//Tween to get our in-between movement to the next tile
		var destTweenX = (endX * tween) + (e.x*(1-tween));
		var destTweenY = (endY * tween) + (e.y*(1-tween));
		
		tweenEntityToPosition(e,destTweenX,destTweenY);

		e.direction = direction;	//The last direction we tried
		
		//Update the lastUpdated time
		e.lastUpdate += mostRecentTile * millisecondsPerTile;
		
	}
	
	////////////////
	////DEBUG STUFF
	////////////////
	if (game.debug.enabled) {
		//Count fps
		game.debug.frameCount++;
		if (new Date().getTime() - game.debug.lastFPSPrint > 1000) {
			game.debug.lastFPSPrint = new Date().getTime();
			game.debug.lastFPS = game.debug.frameCount;
			game.debug.frameCount = 0;
		}
		
		if (engine.isKeyPressed("Minus")) {
			if (game.debug.brushSize > 0) {
				game.debug.brushSize--;
			}
		}
		if (engine.isKeyPressed("Equal")) {
			if (game.debug.brushSize < 10) {
				game.debug.brushSize++;
			}
		}
		if (engine.isKeyPressed("Digit1")) {
			game.debug.row+=2;
			game.debug.row %= 4;
		}
		if (engine.isKeyPressed("Digit7")) {
			game.debug.speedBoost-=2;
			if (game.debug.speedBoost < 0) { game.debug.speedBoost = 0;}
		}
		if (engine.isKeyPressed("Digit8")) {
			game.debug.speedBoost+=2;
			if (game.debug.speedBoost > 24) { game.debug.speedBoost = 24;}
		}
		if (engine.isKeyPressed("Digit9")) {
			game.debug.circleFill = !game.debug.circleFill;
		}
		
		//This is the index to use for grid view (ignored for other things)
		var index = game.debug.row == 2 ? game.debug.tiles : game.debug.entities;
		
		if (engine.isKeyPressed("KeyW")) {
			if (game.debug.row >= 2) {
				if (engine.isKeyDown("ShiftLeft")||engine.isKeyDown("ShiftRight")) { game.debug.selectedY -= 4; }	//When combined below, this becomes 5 (1/2 grid size)
				game.debug.selectedY--;
				while (game.debug.selectedY < 0) { game.debug.selectedY += index.maxY;}
			} else {
				game.debug.row--;
				if (game.debug.row < 0) { game.debug.row == 1; }
			}
		}
		if (engine.isKeyPressed("KeyS")) {
			if (game.debug.row >= 2) {
				if (engine.isKeyDown("ShiftLeft")||engine.isKeyDown("ShiftRight")) { game.debug.selectedY += 4; }	//When combined below, this becomes 5 (1/2 grid size)
				game.debug.selectedY++;
				while (game.debug.selectedY >= index.maxY) { game.debug.selectedY -= index.maxY;}
			} else {
				game.debug.row++;
				if (game.debug.row > 1) { game.debug.row = 0; }
			}
		}
		if (engine.isKeyPressed("KeyA")) {
			if (game.debug.row >= 2) {
				if (engine.isKeyDown("ShiftLeft")||engine.isKeyDown("ShiftRight")) { game.debug.selectedX -= 4; }	//When combined below, this becomes 5 (1/2 grid size)
				game.debug.selectedX--;
				while (game.debug.selectedX < 0) { game.debug.selectedX += index.maxX;}
			} else {
				game.debug.selected--;
			}
		}
		if (engine.isKeyPressed("KeyD")) {
			if (game.debug.row >= 2) {
				if (engine.isKeyDown("ShiftLeft")||engine.isKeyDown("ShiftRight")) { game.debug.selectedX += 4; }	//When combined below, this becomes 5 (1/2 grid size)
				game.debug.selectedX++;
				while (game.debug.selectedX >= index.maxX) { game.debug.selectedX -= index.maxX;}
			} else {
				game.debug.selected++;
			}
		}
		
		if (game.debug.row >= 2) {
			if (index[game.debug.selectedY] && index[game.debug.selectedY][game.debug.selectedX]) {
				game.debug.selected = index[game.debug.selectedY][game.debug.selectedX].replace("entity","").replace("tile","")-0;
			} else {
				game.debug.selected = 0;
			}
		}
		
		
		//Assume X tiles.  add by X then mod by X.  Solves + and - changes
		if (game.debug.row == 0) {
			game.debug.selected += game.uniqueTileIDs;
			game.debug.selected %= game.uniqueTileIDs;
		} else {
			game.debug.selected += game.uniqueEntityIDs;
			game.debug.selected %= game.uniqueEntityIDs;
		}
		
		
		var selectedID = game.debug.selected;	//Might be a tileID or entityID
		
		//if its an entity, tell the server to make a new entity 
		if (game.debug.row == 1 || game.debug.row == 3) {
			if (engine.isKeyPressed("KeyE")) {	//Used pressed so we don't make extra entities
				//Tell server to make a new entity
				game.appendMessage += "&createEntity=" + selectedID + "|" + game.player.x + "|" + game.player.y;
			}
		} else {	
			if (engine.isKeyDown("KeyE")) {			
				for (var xOffset = -game.debug.brushSize; xOffset <= game.debug.brushSize; xOffset++) {
					for (var yOffset = -game.debug.brushSize; yOffset <= game.debug.brushSize; yOffset++) {
						var row = game.player.y + yOffset;
						var col = game.player.x + xOffset;
												
						//Distance calculation
						var distSqrd = (xOffset*xOffset)+(yOffset*yOffset);
						var radSqrd = game.debug.brushSize * game.debug.brushSize;
						if (game.debug.brushSize > 3) { radSqrd -= 0.1; }
						if (!game.debug.circleFill || distSqrd <= radSqrd) {
							game.map.tile[row][col] = selectedID;
							game.map.delta[row + "|" + col + "|" + selectedID] = true;
						}
					}
				}
			}
		}
	}
	
	if(engine.isKeyPressed("Enter")){
		game.appendMessage += "&chat=" + escape(engine.keyboardBuffer);
		engine.keyboardBuffer = "";
	}
	
	//Deprecated.  Handled on server now
	//playerPath = findPath(game.player.x,game.player.y,48,64);
}

function tweenEntityToPosition(e,endX,endY) {
		if (e.timeElapsedOnServer == 0) {
			e.tween = 1;	//No tween for things we didn't get from server
		}
		
		//If we're really far, don't tween
		if (Math.abs(endX-e.tweenX) > 6 || Math.abs(endY-e.tweenY) > 6) {
			e.tween = 1;
		}
		e.tween+=0.1;
		if (e.tween > 1) { e.tween = 1;}
		e.tweenX = (endX * e.tween) + (e.tweenX*(1-e.tween));
		e.tweenY = (endY * e.tween) + (e.tweenY*(1-e.tween));
}

function paintGame() {
	//Offset everything by the player's position
	var offsetXTile = Math.floor(game.player.tweenX - 12);
	var offsetYTile = Math.floor(game.player.tweenY - 9);
	var offsetX = Math.floor((game.player.tweenX - 12) * 32);
	var offsetY = Math.floor((game.player.tweenY - 9) * 32);
	
	//If debug mode, shift offsetY slightly less so everything is drawn lower
	if (game.debug.enabled && game.debug.row >= 2) {
		offsetYTile -= 4;
		offsetY -= 4*32;
	}
	
	//Draw only what we can see.
	var startRow = offsetYTile - 1;
	var endRow = offsetYTile + 20;
	var startCol = offsetXTile - 1;
	var endCol = offsetXTile + 26;
	
	for (var r = startRow; r <= endRow; r++) {
		for (var c = startCol; c <= endCol; c++) {
			//context.drawImage(images.tiles, 0,32*game.map.tile[r][c],32,32,32 * (c-offsetX), 32 * (r-offsetY),32,32);		
			//console.log(sprites.tiles[game.map.tile[r][c]].__frames[0].y);
			//engine.__sprites[game.map.tile[r][c]].draw(context,,);
			if (game.map.tile[r]!=undefined && game.map.tile[r][c] !=undefined) {
				engine.drawSprite("tile" + game.map.tile[r][c],(32 * c) - offsetX,(32 * r)-offsetY);
				if (game.debug.enabled && engine.isKeyDown("Digit2")) {
					//Draw collision box
					if (game.collidableTiles[game.map.tile[r][c]]) {
						engine.__context.fillStyle = "rgba(255,0,0,.7)";
					} else {
						engine.__context.fillStyle = "rgba(255,255,255,.7)";
					}
					engine.__context.fillRect((32*c)-offsetX,(32*r)-offsetY,32,32);
				}
			}
		}
	}
	
	//Sort entities based on their y position. (ties use HP);
	//Only add entities that are in viewing distance (x (-1:26) & y (-1:20)
	var tmpEntities = [];
	for (var id in game.entities) {
		var e = game.entities[id];
		var x = e.tweenX - offsetXTile;
		var y = e.tweenY - offsetYTile;
		if (x > -1 && x < 26 && y > -1 && y < 20) {
			//Spawners only render in debug mode
			if (game.debug.enabled || !game.hiddenEntities[e.type]) {
				tmpEntities.push(e);
			}
		}
	}
	tmpEntities.sort(entitySortFunc);
	
	for (var id in tmpEntities) {
		var e = tmpEntities[id];
		if (e.dead) { continue; }
		var x = (e.tweenX*32)-offsetX;
		var y = (e.tweenY*32)-offsetY;
		
		engine.drawSprite(getSpriteTag(e),x,y);	
		
		//DEBUG: Draw collision box
		if (game.debug.enabled && engine.isKeyDown("Digit3")) {
			//Draw collision box
			if (game.collidableEntities[e.type]) {
				engine.__context.fillStyle = "rgba(255,0,0,.7)";
			} else {
				engine.__context.fillStyle = "rgba(255,255,255,.7)";
			}
			engine.__context.fillRect(x,y,32,32);
		}
		
		if (e.direction == 0) {y-=32;}
		else if (e.direction == 1) { y += 32; }
		else if (e.direction == 2) { x -= 32; }
		else if (e.direction == 3) { x += 32; }
		
		//Draws the attack sprite if it exists
		engine.drawSprite("inst_att_" + e.id,x,y);
		
		
	}
	
	engine.recordKeyboard(true);
	engine.__context.fillStyle = "#FFF";
	engine.__context.fillText(engine.keyboardBuffer,10,engine.height - 30);
	
	
	if (game.message.length > 0) {
		var yStart = 600-(30*4)-10;
		engine.__context.fillStyle = "#000";
		engine.__context.fillRect(10,yStart,780,(30*4));
		
		//Actually draw the message
		engine.__context.fillStyle = "#fff";
		engine.__context.font = "24px celticFont";
		for (var i = 0; i < game.message.length;i++) {
			engine.__context.fillText(game.message[i],30,yStart + (28*(i+1)));
		}
	}
	
	/////////////////////
	//////////////////////
	//////////////////////
	//////////DEBUG MODE
	/////////////////////
	//////////////////////
	//////////////////////
	if (game.debug.enabled) {
		//Draw the entity ID above every entity (and paths)
		engine.__context.fillStyle = "#fff";
		engine.__context.font="12px Arial";
		for (var id in game.entities) {
			var e = game.entities[id];
			var x = e.x;
			var y = e.y
			
			engine.__context.fillText(id,(x*32)-offsetX,(y*32)-offsetY);	
			
			//Draw the path
			engine.__context.strokeStyle="#00f";
			engine.__context.beginPath();
			engine.__context.moveTo((x*32)-offsetX+16,(y*32)-offsetY+16);
			var newPath = new Queue();
			while (!e.path.isEmpty()) {
				var direction = e.path.dequeue();
				newPath.enqueue(direction);
				if (direction == 0) {y--;}
				if (direction == 1) {y++;}
				if (direction == 2) {x--;}
				if (direction == 3) {x++;}
				engine.__context.lineTo((x*32)-offsetX+16,(y*32)-offsetY+16);
			}
			e.path = newPath;
			engine.__context.stroke();
		}
		
		if (game.debug.row >= 2) {
			
			var index = game.debug.row == 2 ? game.debug.tiles : game.debug.entities;
			
			//Use multiples of 42 (32+10px padding)
			//8 rows, 19 cols
			var width = 42;
			var maxRow = 8;
			var maxCol = 19;
			var zoomOut = false;
			if (engine.isKeyDown("ShiftRight")) {
				zoomOut = true;
				engine.__context.save();
				engine.__context.scale(0.5,0.5);
				maxRow *= 2;
				maxCol *= 2;
			}
			var rowOffset = game.debug.selectedY-Math.floor(maxRow/2);
			var colOffset = game.debug.selectedX-Math.floor(maxCol/2);;
			
			for (var row = 0; row < maxRow; row++) {
				for (var col = 0; col < maxCol; col++) {
					var r = row + rowOffset;
					var c = col + colOffset;
					if (r < 0) { r += index.maxY; }
					if (c < 0) { c += index.maxX; }
					r %= index.maxY;
					c %= index.maxX;
					
					var opacity = 0.8;
					var majorColor = 150;
					var minorColor = 50;
					var noColor = 0;
					if (r == game.debug.selectedY && c == game.debug.selectedX) {
						opacity = 1;
						majorColor = 255;
						minorColor = 255;
						noColor = 255;
					}
					//Draw this entity if it exists
					if (index[r] && index[r][c]) {
						var tag = index[r][c];
						if (game.collidableEntities[tag.replace("entity","")]) {
							engine.__context.fillStyle = "rgba("+majorColor+",050,0," + opacity +")";
						} else {
							engine.__context.fillStyle  = "rgba(0, "+minorColor+", 0, " + opacity +")";
						}
						engine.__context.fillRect(col * width, row*width, width, width);
						
						if (game.debug.savableEntitites[tag.replace("entity","")]) {
							engine.__context.fillStyle = "rgba(0,"+minorColor+","+majorColor+"," + opacity +")";
							engine.__context.beginPath();
							engine.__context.moveTo(col*width+1,row*width+1);
							engine.__context.lineTo(col*width + 40,row*width+1);
							engine.__context.lineTo(col*width+21,row*width+41);
							engine.__context.fill();
						}
						engine.drawSprite(tag,(col * width)+5,(row * width)+5);
					} else {
						var gridlineColor = 0;
						var imagelineColor = 0;
						if (r % 10 == 0 || c % 10 == 0) { gridlineColor = 150;}
						if (r == 0 || c == 0) { imagelineColor = 150;}
						
						engine.__context.fillStyle  = "rgba("+noColor+", "+imagelineColor + ", "+gridlineColor+", 0.9)";
						engine.__context.fillRect(col * width, row*width, width, width);
					}
				}
			}
			
			if (zoomOut) {
				//Restore out graphics state
				engine.__context.restore();
			}
		} else if (game.debug.row < 2) {
			
			//IDK why i chose 42, but go with it
			engine.__context.fillStyle = "#000";
			engine.__context.fillRect(0,0,engine.width,42*2);
			engine.__context.fillStyle = "#fff";
			
			var xOffset = 0;
			while (game.debug.selected - xOffset > 20) {
				xOffset += 20;
			}
			xOffset *= 32+5;
			
			var selectRow = game.debug.row == 1 ? 42 : 0;
			engine.__context.fillRect(game.debug.selected * (32+5) - xOffset, selectRow,32+10,32+10);
			
			//If we go too far to the right, scroll
			//Draw percent of brushSize
			engine.__context.fillStyle = "#f00";
			var height = game.debug.brushSize * 4.2;	//0-10 * 42 is a size of 0 to 42
			engine.__context.fillRect(game.debug.selected * (32+5) - xOffset, 42-height,32+10,height);
			
			//Draw tiles at the top for level editor
			for (var i =0 ; i < game.uniqueTileIDs; i++) {
				engine.drawSprite("tile" + i,(i*(32+5) + 5)-xOffset,5);
			}
			
			
			//Draw what we're about to edit
			//NOTE:  THIS IS JUST DRAWING!  MAY DIFFER FROM ACTUAL
			engine.__context.fillStyle  = "rgba(255, 0, 0, 0.2)";
			for (var colOffset = -game.debug.brushSize; colOffset <= game.debug.brushSize; colOffset++) {
				for (var rowOffset = -game.debug.brushSize; rowOffset <= game.debug.brushSize; rowOffset++) {
					var row = game.player.y + rowOffset;
					var col = game.player.x + colOffset;
					var x = (col * 32) - offsetX;
					var y = (row * 32) - offsetY;
					
					//Distance calculation
					var distSqrd = (colOffset*colOffset)+(rowOffset*rowOffset);
					var radSqrd = game.debug.brushSize * game.debug.brushSize;
					if (game.debug.brushSize > 3) { radSqrd -= 0.1; }
					if (!game.debug.circleFill || distSqrd <= radSqrd) {
						engine.__context.fillRect(x,y,32,32);
					}
				}
			}
			
			
			//ENTITY STUFF
			//Draw tiles at the top for level editor
			for (var i =0 ; i < game.uniqueEntityIDs; i++) {
				engine.drawSprite("entity" + i,(i*(32+5) + 5)-xOffset,5+42);
			}
			
			//Draw ping time
			engine.__context.fillStyle  = "#000";
			engine.__context.fillRect(0,42*2,300,42);
			engine.__context.fillStyle  = "#fff";
			engine.__context.fillText("Ping: "+game.ping + "ms",10,42*3-10);
			engine.__context.fillText("FPS: " + game.debug.lastFPS, 100,42*3-10);
			engine.__context.fillText("Pos: (" + game.player.x + "," + game.player.y + ")", 200,42*3-10);
		}
		
		engine.__context.fillStyle  = "#000";
		engine.__context.fillRect(0,600-20,800,20);
		engine.__context.fillStyle  = "#fff";
		engine.__context.fillText("? key = help:",10,595);
		
		if (engine.isKeyDown("Slash")) {
			engine.__context.fillStyle  = "rgba(0,0,0,.8)";
			engine.__context.fillRect(0,0,800,600);
			engine.__context.fillStyle  = "#fff";
			engine.__context.font = "18px Arial";
			var y = 20;
			engine.__context.fillText("WASD: Select",10,y); y+=20
			engine.__context.fillText("E: Create object / Set tile",10,y); y+=20
			engine.__context.fillText("1: Toggle grid view",10,y); y+=20
			engine.__context.fillText("2/3: Show Tile/Entity Collision boxes",10,y); y+=20
			engine.__context.fillText("7/8: Fly slower/faster",10,y); y+=20
			engine.__context.fillText("+/-: Increase/Decrease edit size (tiles only)",10,y); y+=20
			engine.__context.fillText("9: Toggle between circle and square select zone (tiles only)",10,y); y+=20
			engine.__context.fillText("Left Shift (in grid mode): Move 5 tiles at a time",10,y); y+=20
			engine.__context.fillText("Right Shift (in grid mode): Zoom out",10,y); y+=20
		}
	}
}


function move(xMovement, yMovement){
	//Don't let the player move if we haven't spawned yet
	if (game.message.length > 0) { return; }//No doing stuff while there's a message
	
	var moved = false;
	
	var playerX = game.player.x;
	var playerY = game.player.y;
	
	//If they've already got a path of length >= 1, we will look to replace the second part
	//Pretend that we just finished walking and queue the next step
	if (game.player.path.getLength() > 0) {
		var direction = game.player.path.peek();
		if (direction == 0) {playerY--;}
		if (direction == 1) {playerY++;}
		if (direction == 2) {playerX--;}
		if (direction == 3) {playerX++;}
	}
	
	var prevX = playerX;
	var prevY = playerY;
	
	//If we're less than halfway there (distance < 0.5)
	//then quit. (distsqrd < 0.5*0.5)
	if (game.player.path.getLength() > 0) {
		var dX = (game.player.x-game.player.tweenX);
		var dY = (game.player.y-game.player.tweenY);
		if ((dX*dX)+(dY*dY) < 0.25) { return; }
	}
	
	//Player can walk anywhere in debug mode
	if (game.debug.enabled) {
		playerX += xMovement;
		playerY += yMovement;
		moved = xMovement != 0 || yMovement != 0;
	} else {
		//Try moving x first
		if (xMovement != 0 && isPassable(playerY, playerX + xMovement)) {
			playerX += xMovement; moved = true;
		} else if (yMovement != 0 && isPassable(playerY + yMovement,playerX)) {
			playerY += yMovement; moved = true;
		}
	}
	
	if (moved) {
		//Create a path
		var direction = 0;
		if (playerY < prevY) { direction = 0;}
		else if (playerY > prevY) { direction = 1;}
		else if (playerX < prevX) { direction = 2;}
		else if (playerX > prevX) { direction = 3;}
		
		//add this as our path
		var newPath = new Queue();
		if (game.player.path.getLength() > 0) {
			newPath.enqueue(game.player.path.peek());
		} else {
			game.player.lastUpdate = new Date().getTime();
		}
		newPath.enqueue(direction);
		game.player.path = newPath;
		game.player.timeElapsedOnServer = 0;
		
		//game.player.set("x",playerX);
		//game.player.set("y",playerY);
	}
}

/**Start the attack animation for this entitt*/
function startAttackSprite(eid) {
	engine.createSpriteInstance("inst_att_" + eid,"attack0");
}

/**Gets the sprite tag for the entity specified.
This handles rotation (eg, left sprite if walking left)
This also works for sprites that do not have direction.*/
function getSpriteTag(entity) {
	var baseTag = "entity" + entity.type;
	
	//Directions (walking)
	if (!entity.idle) {
		var directionTag = baseTag + "_" + entity.direction + "_w";
		if (engine.containsSprite(directionTag)) {
			return directionTag;
		}
	}
	
	//Directions (idle)
	directionTag = baseTag + "_" + entity.direction;
	if (engine.containsSprite(directionTag)) {
		return directionTag;
	}
	
	//There's a chance this entity won't have any special sprites
	//Just return the basic if we made it this far. (eg: "entity2")
	return baseTag;
}

//Returns true if there is a tile at position [row][col] and it is a passable tile
function isPassable(row,col) {
	
	//Loop over entities to see if there's anything on this tile
	for (var eid in game.entities) {
		var e = game.entities[eid];
		if (e.x == col && e.y == row && game.collidableEntities[e.type]) {
			return false;
		}
	}

	if (game.map.tile[row] == undefined || game.map.tile[row][col] == undefined) { 
		return false;
	}
	
	return !game.collidableTiles[game.map.tile[row][col]];
}

/*******************************************************************************
* Data Structures and Util                                                     *
*******************************************************************************/

function entitySortFunc(a,b) {
	if (a.isUnderlay && !b.isUnderlay) { return -1;}
	if (b.isUnderlay && !a.isUnderlay) { return 1;}
	
	if (a.isOverlay && !b.isOverlay) { return 1;}
	if (b.isOverlay && !a.isOverlay) { return -1;}
	
	if (a.y < b.y) { return -1; }
	if (a.y > b.y) { return 1; }
	
	//If they're the same, compare HP
	if (game.killableEntities[a.type] && !game.killableEntities[b.type]) { return 1;}
	if (!game.killableEntities[a.type] && game.killableEntities[b.type]) { return -1;}
	
	if (a.id < b.id) { return -1;}
	if (a.id > b.id) { return 1;}
	return 0;
}

//GAME STUFF
Entity.prototype = {
	x: 0,
	y: 0,
	id: 0,
	type: 0,
	tweenX: 0,
	tweenY: 0,		//Calculated by path
	direction: 1,	//0/1/2/3 = up/dn/lf/rt respectively (default down)
	dead: false,	//If true, treat it like its gone
	hostile: false,
	path: new Queue(),
	timeElapsedOnServer: 0,
	lastUpdate: 0,		//Time in javascript time since last server update
	idle: true,			//if false, animation starts
	name: "unnamed",
	delta: [],
	isOverlay: false,
	isUnderlay: false,
	sprite: null,
	set: function(key,value) { this[key] = value; this.delta[key] = value;}
}

function Entity(id,type,x,y) {
	this.id = id;
	this.type = type;
	this.x = x;
	this.y = y;
}

//Queue implementation, 3rd party minified
//code.stephenmorley.org
function Queue(){var a=[],b=0;this.getLength=function(){return a.length-b};this.isEmpty=function(){return 0==a.length};this.enqueue=function(b){a.push(b)};this.dequeue=function(){if(0!=a.length){var c=a[b];2*++b>=a.length&&(a=a.slice(b),b=0);return c}};this.peek=function(){return 0<a.length?a[b]:void 0}};

/**I don't see any reason to use this...
*/
function findPath(startX,startY,endX,endY,maxRadius) {
	var q = new Queue();
	var visited = [];	//A set of visited nodes
	
	//Add our end point
	q.enqueue({x:endX,y:endY,prev:null});
	visited[endX + "," + endY] = true;
	
	while (!q.isEmpty()) {
		var node = q.dequeue();
		var neighbors = [
			{x:node.x-1,y:node.y,  prev:node},
			{x:node.x+1,y:node.y,  prev:node},
			{x:node.x  ,y:node.y-1,prev:node},
			{x:node.x  ,y:node.y+1,prev:node}]
		//Add all unvisited tiles that are passable
		for(var neighbor in neighbors) {
			neighbor = neighbors[neighbor]
			if (visited[neighbor.x+","+neighbor.y] == true) { continue; }
			if (isPassable(neighbor.y,neighbor.x)) {
				//Is this our goal???
				if (neighbor.x == startX && neighbor.y == startY) {
					//We found it!  Build the path!
					return __buildPathFromGraph(neighbor);
				}
				
				//Add it to our queue
				q.enqueue(neighbor);
				visited[neighbor.x + "," + neighbor.y] = true;
			}
		}
	}
	return null;
}

function __buildPathFromGraph(node) {
	var path = [];
	
	//Fake node so we can start with node in our loop
	node = {x:-1,y:-1,prev:node};
	
	//Build our path in reverse order
	while (node.prev != null) {
		node = node.prev;
		path.push(node);
	}
	return path;
}