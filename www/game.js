/**An explicit definition of properties of the game object**/
game.entities = [];		//Usage: game.entities[entityID]
game.player = null;		//Usage: game.player  (reference to player entity)
game.map = {
	delta: {},
	set: function(row,col,value) {
		this[row][col] = value;
		this.delta[row + "|" + col + "|" + value] = null;
	}
};
game.collidableTiles = [];	//An array of unpassable tiles
game.collidableEntities = [];//Array of entities that can't be walked on
game.type = "menu";
game.ping = 0;
game.appendMessage = "";	//DEBUG ONLY: Append this to the end of the message sent to the server
game.debug = {};
game.debug.enabled = false;
game.debug.selected = 0;
game.debug.row = 0;	//Row 0 is tiles, 1 is entities
game.debug.brushSize = 1;
game.debug.circleFill = false;
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
	
	
	
	//ENTITY SPRITES
	game.uniqueEntityIDs = 0;
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"objects",32,32);	//Sprite: NULL
	tmp.addFrame(288,0,10);
	
	createWalkingAnimSprites("entity" + game.uniqueEntityIDs++,"characters",32,32,96,0);//Sprite(s): Player (up/dn/lf/rt, walking and idle)
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Sign
	tmp.addFrame(0,0,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Gravestone
	tmp.addFrame(32,0,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Gem
	var xTmp = 0;
	for (var i = 0; i < 8; i++) {	//Load the 8 frames, all to the right of eachother
		tmp.addFrame((xTmp++)*32,32,4);
	}
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Wall top back left
	tmp.addFrame(0,64,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Wall top back right
	tmp.addFrame(32,64,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Wall bot back left
	tmp.addFrame(0,96,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Wall bot back right
	tmp.addFrame(32,96,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Wall left
	tmp.addFrame(0,128,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Wall right
	tmp.addFrame(32,128,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Wall front top left
	tmp.addFrame(0,160,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Wall front top right
	tmp.addFrame(32,160,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Wall front bot left
	tmp.addFrame(0,192,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Wall front bot right
	tmp.addFrame(32,192,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Wall norm Top
	tmp.addFrame(64,64,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Wall norm
	tmp.addFrame(64,96,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Wall Pillar top left half
	tmp.addFrame(64,128,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Wall Pillar bottom left half
	tmp.addFrame(64,160,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Wall Pillar top right half
	tmp.addFrame(96,128,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Wall Pillar bottom right half
	tmp.addFrame(96,160,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Egg Yellow
	tmp.addFrame(256,32,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Egg Blue-Orange
	tmp.addFrame(288,32,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Mushroom Spawn
	tmp.addFrame(0,288,10);
	
	createWalkingAnimSprites("entity" + game.uniqueEntityIDs++,"characters",32,32,0,256);//Sprite(s): Mushroom Entity (up/dn/lf/rt, walking and idle)
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Chick Spawn
	tmp.addFrame(32,288,10);
	
	createWalkingAnimSprites("entity" + game.uniqueEntityIDs++,"characters",32,32,96,256);//Sprite(s): Chick Entity (up/dn/lf/rt, walking and idle)
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,64,0,32);	//Sprite: Tree (2blocks high)
	tmp.addFrame(96,64,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Fence back left
	tmp.addFrame(128,64,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Fence back
	tmp.addFrame(160,64,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Fence back right
	tmp.addFrame(192,64,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Fence left
	tmp.addFrame(128,96,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Fence center
	tmp.addFrame(160,96,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Fence right
	tmp.addFrame(192,96,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Fence front left
	tmp.addFrame(128,128,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Fence front
	tmp.addFrame(160,128,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Fence front right
	tmp.addFrame(192,128,10);
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Fence Post
	tmp.addFrame(224,64,10);
	
	createWalkingAnimSprites("entity" + game.uniqueEntityIDs++,"darkerCharacters",32,32,96,0);//Sprite(s): Zombie Entity (up/dn/lf/rt, walking and idle)
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Torch Yellow
	for (var i = 0; i < 4; i++) {
		var y = i < 3 ? i : 1;	//Frames 0-1-2-1
		tmp.addFrame(224+(y*32),96,3);
	}
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Torch Blue
	for (var i = 0; i < 4; i++) {
		var y = i < 3 ? i : 1;	//Frames 0-1-2-1
		tmp.addFrame(224+(y*32),128,3);
	}
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Torch Red
	for (var i = 0; i < 4; i++) {
		var y = i < 3 ? i : 1;	//Frames 0-1-2-1
		tmp.addFrame(224+(y*32),160,3);
	}
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Torch Green
	for (var i = 0; i < 4; i++) {
		var y = i < 3 ? i : 1;	//Frames 0-1-2-1
		tmp.addFrame(224+(y*32),192,3);
	}
	
	var tmp = engine.createSprite("entity" + game.uniqueEntityIDs++,"everything",32,32);	//Sprite: Robot Spawn
	tmp.addFrame(64,288,10);
	
	createWalkingAnimSprites("entity" + game.uniqueEntityIDs++,"characters",64,64,384,0,16,32);//Sprite(s): Robot Entity (up/dn/lf/rt, walking and idle)
	
	//ARRAY DECLARATIONS  -- to make math easier
	var grid = [0,320,640,960,1280,1600,1920,2240,2560,2880];
	var cell = [0,32,64,96,128,160,192,224,256,288];
	
	
	//ITEM SPRITES 
		//Block A 2
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Teal Potion
	tmp.addFrame(grid[0] +cell[0],grid[1]+cell[0],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Red Potion
	tmp.addFrame(grid[0] +cell[1],grid[1]+cell[0],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Blue Potion
	tmp.addFrame(grid[0] +cell[2],grid[1]+cell[0],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Yellow Potion
	tmp.addFrame(grid[0] +cell[3],grid[1]+cell[0],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Green Potion
	tmp.addFrame(grid[0] +cell[4],grid[1]+cell[0],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: White Potion
	tmp.addFrame(grid[0] +cell[5],grid[1]+cell[0],10);
	
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Black Potion
	tmp.addFrame(grid[0] +cell[6],grid[1]+cell[0],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Purple Potion
	tmp.addFrame(grid[0] +cell[7],grid[1]+cell[0],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Cyan Potion
	tmp.addFrame(grid[0] +cell[8],grid[1]+cell[0],10);

	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Meat
	tmp.addFrame(grid[0] +cell[0],grid[1]+cell[1],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Wheat
	tmp.addFrame(grid[0] +cell[1],grid[1]+cell[1],10);

	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Apple
	tmp.addFrame(grid[0] +cell[2],grid[1]+cell[1],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Bread
	tmp.addFrame(grid[0] +cell[3],grid[1]+cell[1],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Potato
	tmp.addFrame(grid[0] +cell[4],grid[1]+cell[1],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: White Flower
	tmp.addFrame(grid[0] +cell[0],grid[1]+cell[2],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Onion
	tmp.addFrame(grid[0] +cell[1],grid[1]+cell[2],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Purple Flower
	tmp.addFrame(grid[0] +cell[2],grid[1]+cell[2],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Leeks
	tmp.addFrame(grid[0] +cell[3],grid[1]+cell[2],10);

	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Pink Flower
	tmp.addFrame(grid[0] +cell[4],grid[1]+cell[2],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Beats
	tmp.addFrame(grid[0] +cell[5],grid[1]+cell[2],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Blue Flower
	tmp.addFrame(grid[0] +cell[6],grid[1]+cell[2],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Carrot
	tmp.addFrame(grid[0] +cell[7],grid[1]+cell[2],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Sword
	tmp.addFrame(grid[0] +cell[0],grid[1]+cell[3],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Battle Axe
	tmp.addFrame(grid[0] +cell[1],grid[1]+cell[3],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Dagger
	tmp.addFrame(grid[0] +cell[2],grid[1]+cell[3],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Arrow
	tmp.addFrame(grid[0] +cell[3],grid[1]+cell[3],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Cane
	tmp.addFrame(grid[0] +cell[4],grid[1]+cell[3],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Pole
	tmp.addFrame(grid[0] +cell[5],grid[1]+cell[3],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Bow
	tmp.addFrame(grid[0] +cell[6],grid[1]+cell[3],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Spear
	tmp.addFrame(grid[0] +cell[7],grid[1]+cell[3],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Pick Axe
	tmp.addFrame(grid[0] +cell[8],grid[1]+cell[3],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: SledgeHammer
	tmp.addFrame(grid[0] +cell[9],grid[1]+cell[3],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Bludgeon
	tmp.addFrame(grid[0] +cell[0],grid[1]+cell[4],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Spiked Bludgeon
	tmp.addFrame(grid[0] +cell[1],grid[1]+cell[4],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Flail
	tmp.addFrame(grid[0] +cell[2],grid[1]+cell[4],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Sceptre
	tmp.addFrame(grid[0] +cell[3],grid[1]+cell[4],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Axe
	tmp.addFrame(grid[0] +cell[4],grid[1]+cell[4],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: War Hammer
	tmp.addFrame(grid[0] +cell[5],grid[1]+cell[4],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Royal Sceptre
	tmp.addFrame(grid[0] +cell[6],grid[1]+cell[4],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Helmet
	tmp.addFrame(grid[0] +cell[0],grid[1]+cell[5],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Horny Helmet
	tmp.addFrame(grid[0] +cell[1],grid[1]+cell[5],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Mohawk Helmet
	tmp.addFrame(grid[0] +cell[2],grid[1]+cell[5],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Blue Mage Hat
	tmp.addFrame(grid[0] +cell[3],grid[1]+cell[5],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Grey Mage Hat
	tmp.addFrame(grid[0] +cell[4],grid[1]+cell[5],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Red Mage Hat
	tmp.addFrame(grid[0] +cell[5],grid[1]+cell[5],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Apprentice Hat
	tmp.addFrame(grid[0] +cell[6],grid[1]+cell[5],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: ChestPlate
	tmp.addFrame(grid[0] +cell[0],grid[1]+cell[6],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Gilded ChestPlate
	tmp.addFrame(grid[0] +cell[1],grid[1]+cell[6],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Chainmail
	tmp.addFrame(grid[0] +cell[2],grid[1]+cell[6],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Red Armor
	tmp.addFrame(grid[0] +cell[3],grid[1]+cell[6],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Grey Mage Robe
	tmp.addFrame(grid[0] +cell[4],grid[1]+cell[6],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Blue Mage Robe
	tmp.addFrame(grid[0] +cell[5],grid[1]+cell[6],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Red Mage Robe
	tmp.addFrame(grid[0] +cell[6],grid[1]+cell[6],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Leather Body
	tmp.addFrame(grid[0] +cell[7],grid[1]+cell[6],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Studded Body
	tmp.addFrame(grid[0] +cell[8],grid[1]+cell[6],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: PlateLegs
	tmp.addFrame(grid[0] +cell[0],grid[1]+cell[7],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Gilded PlateLegs
	tmp.addFrame(grid[0] +cell[1],grid[1]+cell[7],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Sexy Legs
	tmp.addFrame(grid[0] +cell[2],grid[1]+cell[7],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Red Boots
	tmp.addFrame(grid[0] +cell[3],grid[1]+cell[7],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Boots
	tmp.addFrame(grid[0] +cell[4],grid[1]+cell[7],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Red Shield
	tmp.addFrame(grid[0] +cell[0],grid[1]+cell[8],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Good Shield
	tmp.addFrame(grid[0] +cell[1],grid[1]+cell[8],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Blood Shield
	tmp.addFrame(grid[0] +cell[2],grid[1]+cell[8],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Gray Shield
	tmp.addFrame(grid[0] +cell[3],grid[1]+cell[8],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Wooden Shield
	tmp.addFrame(grid[0] +cell[4],grid[1]+cell[8],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: KiteShield
	tmp.addFrame(grid[0] +cell[5],grid[1]+cell[8],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Round Shield
	tmp.addFrame(grid[0] +cell[6],grid[1]+cell[8],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Gold Amulet
	tmp.addFrame(grid[0] +cell[0],grid[1]+cell[9],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Amulet
	tmp.addFrame(grid[0] +cell[1],grid[1]+cell[9],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Gold Unstrung Amulet
	tmp.addFrame(grid[0] +cell[2],grid[1]+cell[9],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Unstrung Amulet
	tmp.addFrame(grid[0] +cell[3],grid[1]+cell[9],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Red Key
	tmp.addFrame(grid[0] +cell[8],grid[1]+cell[9],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Grey Key
	tmp.addFrame(grid[0] +cell[9],grid[1]+cell[9],10);
	
	//Block B 2
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Urn
	tmp.addFrame(grid[1] +cell[2],grid[1]+cell[0],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Vase
	tmp.addFrame(grid[1] +cell[3],grid[1]+cell[0],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Mineral Water
	tmp.addFrame(grid[1] +cell[4],grid[1]+cell[0],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Water
	tmp.addFrame(grid[1] +cell[5],grid[1]+cell[0],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Big Fish
	tmp.addFrame(grid[1] +cell[1],grid[1]+cell[1],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Salmon
	tmp.addFrame(grid[1] +cell[2],grid[1]+cell[1],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Blue Fish
	tmp.addFrame(grid[1] +cell[3],grid[1]+cell[1],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Lobster
	tmp.addFrame(grid[1] +cell[4],grid[1]+cell[1],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Dead Salmon
	tmp.addFrame(grid[1] +cell[5],grid[1]+cell[1],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Short Sword
	tmp.addFrame(grid[1] +cell[0],grid[1]+cell[3],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Short Flail
	tmp.addFrame(grid[1] +cell[1],grid[1]+cell[3],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Fishing Pole
	tmp.addFrame(grid[1] +cell[2],grid[1]+cell[3],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Short BattleAxe
	tmp.addFrame(grid[1] +cell[3],grid[1]+cell[3],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Halberd
	tmp.addFrame(grid[1] +cell[4],grid[1]+cell[3],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Scimitar
	tmp.addFrame(grid[1] +cell[5],grid[1]+cell[3],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Blue Book
	tmp.addFrame(grid[1] +cell[3],grid[1]+cell[8],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Red Book
	tmp.addFrame(grid[1] +cell[4],grid[1]+cell[8],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Black Book
	tmp.addFrame(grid[1] +cell[5],grid[1]+cell[8],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Gold Key
	tmp.addFrame(grid[1] +cell[0],grid[1]+cell[9],10);
	
	
	//BLOCK A3
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: One Die
	tmp.addFrame(grid[0] +cell[0],grid[2]+cell[1],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Two Die
	tmp.addFrame(grid[0] +cell[1],grid[2]+cell[1],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Three Die
	tmp.addFrame(grid[0] +cell[2],grid[2]+cell[1],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Four Die
	tmp.addFrame(grid[0] +cell[3],grid[2]+cell[1],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Five Die
	tmp.addFrame(grid[0] +cell[4],grid[2]+cell[1],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Six Die
	tmp.addFrame(grid[0] +cell[5],grid[2]+cell[1],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Small Die
	tmp.addFrame(grid[0] +cell[6],grid[2]+cell[1],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Blank Die
	tmp.addFrame(grid[0] +cell[7],grid[2]+cell[1],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Unlit Candle
	tmp.addFrame(grid[0] +cell[0],grid[2]+cell[2],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Candle Flicker
	tmp.addFrame(grid[0] +cell[1],grid[2]+cell[2],10);
	tmp.addFrame(grid[0] +cell[2],grid[2]+cell[2],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Fireball
	tmp.addFrame(grid[0] +cell[3],grid[2]+cell[2],10);
	tmp.addFrame(grid[0] +cell[3],grid[2]+cell[3],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",64,64); //Sprite: Explosion
	tmp.addFrame(grid[0] +cell[4],grid[2]+cell[2],10);
	tmp.addFrame(grid[0] +cell[6],grid[2]+cell[2],10);
	tmp.addFrame(grid[0] +cell[8],grid[2]+cell[2],10);
	tmp.addFrame(grid[0] +cell[4],grid[2]+cell[4],10);
	tmp.addFrame(grid[0] +cell[6],grid[2]+cell[4],10);
	tmp.addFrame(grid[0] +cell[8],grid[2]+cell[4],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Unlit Wall Candle
	tmp.addFrame(grid[0] +cell[0],grid[2]+cell[3],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Wall Candle
	tmp.addFrame(grid[0] +cell[1],grid[2]+cell[3],10);
	tmp.addFrame(grid[0] +cell[2],grid[2]+cell[3],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Unlit Wall Torch
	tmp.addFrame(grid[0] +cell[0],grid[2]+cell[4],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Wall Torch
	tmp.addFrame(grid[0] +cell[1],grid[2]+cell[4],10);
	tmp.addFrame(grid[0] +cell[2],grid[2]+cell[4],10);

	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Bubble Ray
	tmp.addFrame(grid[0] +cell[3],grid[2]+cell[4],10);
	tmp.addFrame(grid[0] +cell[3],grid[2]+cell[5],10);
	

	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Unlit Lantern
	tmp.addFrame(grid[0] +cell[0],grid[2]+cell[5],10);
	

	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Lantern
	tmp.addFrame(grid[0] +cell[1],grid[2]+cell[5],10);
	tmp.addFrame(grid[0] +cell[2],grid[2]+cell[5],10);
	
	//BLOCK A4 - All Crystals Plus Eggs
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Yellow Gem
	tmp.addFrame(grid[0] +cell[0],grid[3]+cell[1],10);
	tmp.addFrame(grid[0] +cell[1],grid[3]+cell[1],10);
	tmp.addFrame(grid[0] +cell[2],grid[3]+cell[1],10);
	tmp.addFrame(grid[0] +cell[3],grid[3]+cell[1],10);
	tmp.addFrame(grid[0] +cell[4],grid[3]+cell[1],10);
	tmp.addFrame(grid[0] +cell[5],grid[3]+cell[1],10);
	tmp.addFrame(grid[0] +cell[6],grid[3]+cell[1],10);
	tmp.addFrame(grid[0] +cell[7],grid[3]+cell[1],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Pink Gem
	tmp.addFrame(grid[0] +cell[0],grid[3]+cell[2],10);
	tmp.addFrame(grid[0] +cell[1],grid[3]+cell[2],10);
	tmp.addFrame(grid[0] +cell[2],grid[3]+cell[2],10);
	tmp.addFrame(grid[0] +cell[3],grid[3]+cell[2],10);
	tmp.addFrame(grid[0] +cell[4],grid[3]+cell[2],10);
	tmp.addFrame(grid[0] +cell[5],grid[3]+cell[2],10);
	tmp.addFrame(grid[0] +cell[6],grid[3]+cell[2],10);
	tmp.addFrame(grid[0] +cell[7],grid[3]+cell[2],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Green Gem
	tmp.addFrame(grid[0] +cell[0],grid[3]+cell[3],10);
	tmp.addFrame(grid[0] +cell[1],grid[3]+cell[3],10);
	tmp.addFrame(grid[0] +cell[2],grid[3]+cell[3],10);
	tmp.addFrame(grid[0] +cell[3],grid[3]+cell[3],10);
	tmp.addFrame(grid[0] +cell[4],grid[3]+cell[3],10);
	tmp.addFrame(grid[0] +cell[5],grid[3]+cell[3],10);
	tmp.addFrame(grid[0] +cell[6],grid[3]+cell[3],10);
	tmp.addFrame(grid[0] +cell[7],grid[3]+cell[3],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Gray Gem
	tmp.addFrame(grid[0] +cell[0],grid[3]+cell[4],10);
	tmp.addFrame(grid[0] +cell[1],grid[3]+cell[4],10);
	tmp.addFrame(grid[0] +cell[2],grid[3]+cell[4],10);
	tmp.addFrame(grid[0] +cell[3],grid[3]+cell[4],10);
	tmp.addFrame(grid[0] +cell[4],grid[3]+cell[4],10);
	tmp.addFrame(grid[0] +cell[5],grid[3]+cell[4],10);
	tmp.addFrame(grid[0] +cell[6],grid[3]+cell[4],10);
	tmp.addFrame(grid[0] +cell[7],grid[3]+cell[4],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Orange Gem
	tmp.addFrame(grid[0] +cell[0],grid[3]+cell[5],10);
	tmp.addFrame(grid[0] +cell[1],grid[3]+cell[5],10);
	tmp.addFrame(grid[0] +cell[2],grid[3]+cell[5],10);
	tmp.addFrame(grid[0] +cell[3],grid[3]+cell[5],10);
	tmp.addFrame(grid[0] +cell[4],grid[3]+cell[5],10);
	tmp.addFrame(grid[0] +cell[5],grid[3]+cell[5],10);
	tmp.addFrame(grid[0] +cell[6],grid[3]+cell[5],10);
	tmp.addFrame(grid[0] +cell[7],grid[3]+cell[5],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Mucus Egg
	tmp.addFrame(grid[0] +cell[8],grid[3]+cell[0],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Purple Egg
	tmp.addFrame(grid[0] +cell[9],grid[3]+cell[1],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Spotted Egg
	tmp.addFrame(grid[0] +cell[8],grid[3]+cell[0],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Mountain Egg
	tmp.addFrame(grid[0] +cell[9],grid[3]+cell[1],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Striped Egg
	tmp.addFrame(grid[1] +cell[0],grid[3]+cell[0],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Green Yellow Egg
	tmp.addFrame(grid[1] +cell[1],grid[3]+cell[0],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Yellw Egg
	tmp.addFrame(grid[1] +cell[2],grid[3]+cell[0],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Wavy Egg
	tmp.addFrame(grid[1] +cell[3],grid[3]+cell[0],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Magma Egg
	tmp.addFrame(grid[1] +cell[0],grid[3]+cell[1],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: RedEgg
	tmp.addFrame(grid[1] +cell[1],grid[3]+cell[1],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Blue Egg
	tmp.addFrame(grid[1] +cell[2],grid[3]+cell[1],10);
	
	var tmp = engine.createSprite("entity"+ game.uniqueEntityIDs++,"everything",32,32); //Sprite: Pink Egg
	tmp.addFrame(grid[1] +cell[3],grid[3]+cell[1],10);
	
	
	
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
		game.player = new Entity(pid,1,-100,-100);	//Type 1: player
		game.entities[pid] = game.player;
		console.log("Player " + game.player.id + ": " + game.player.x + " " + game.player.y);
		
		/* GET MAP */
		game.map.rows = buffer.getInt();
		game.map.cols = buffer.getInt();
		game.map.tile = []
		for (var r = 0; r < game.map.rows; r++) {
			game.map.tile[r] = [];
			for (var c = 0; c < game.map.cols; c++) {
				game.map.tile[r][c] = 5;		//Init to tile 5 (water)
			}
		}
		console.log("Loaded map with " + game.map.rows + " rows and " + game.map.cols + " cols");
		
		/* GET UNPASSABLE TILES */
		var count = buffer.getInt();
		for (var i = 0; i < count; i++) {
			game.collidableTiles[buffer.getInt()] = true;
		}
		
		/* GET UNPASSABLE ENTITIES */
		var count = buffer.getInt();
		for (var i = 0; i < count; i++) {
			game.collidableEntities[buffer.getInt()] = true;
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
	var message = "entity=" + game.player.id + "|" + game.player.x + "|" + game.player.y;
	
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
				var attributeLen = buffer.getInt();	//Not used yet
				
				//Read attributes
				var attributes = [];
				for (var j = 0; j < attributeLen; j++) {
					attributes[j] = buffer.getInt();
				}
				
				var pathLen = buffer.getInt();
				var path = [];
				for (var j = 0; j < pathLen; j++) {
					path[j] = buffer.getByte();
				}
				
				var timeElapsed = buffer.getInt();
				
				if (x == y && y == -1) {
					//He dead
					delete game.entities[eid];
					continue;
				}
				
				var e = game.entities[eid];
				if (!e) {
					console.log("Creating new entity");
					game.entities[eid] = new Entity(eid,type,x,y);
					e = game.entities[eid];
					//console.log("Entity created [type:" + game.entities[eid].type + " id:" + eid + " at (" + x + "," + y + ")]");
				}
				e.path = path;
				e.timeElapsedOnServer = timeElapsed;
				e.lastUpdate = new Date().getTime();
				e.x = x;
				e.y = y;
				e.type = type;
			}
		}
		
		//Response 2: Map update
		if (responseType == 2) {
			var count = buffer.getInt();
			for (var i = 0; i < count; i++) {
				var row = buffer.getInt();
				var col = buffer.getInt();
				var type = buffer.getInt();
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
		game.debug.brushSize = 0;
		console.log("Set debug mode to " + game.debug.enabled);
	}
	
	if (engine.isKeyPressed("Space")) {
		//TEMP: Check around you for a sign then call it!
		for (var dCol = -1; dCol <=1; dCol++) {
			for (var dRow = -1; dRow <=1; dRow++) {
				var row = game.player.y + dRow;
				var col = game.player.x + dCol;
				
				//Search for an entity here
				for (var key in game.entities) {
					var e = game.entities[key];
					if (e.type == 2 && e.x == col && e.y == row) {	//EntityType.Sign is #2
						game.appendMessage += "&sign=" + e.id;
					}
				}
			}
		}
	}
	
	game.player.attacking = engine.isKeyDown("Space");
	
	//Update the positions of all the entities based on elapsed time
	//WARNING: tilesPerSecond MUST BE EXACTLY THE SAME ON THE SERVER
	var tilesPerSecond = 6;	//Speed of entities
	var millisecondsPerTile = 1000/tilesPerSecond;
	for (var eid in game.entities) {
		var e = game.entities[eid];
		if (e.path.length == 0) { 
			e.tweenX = e.x;
			e.tweenY = e.y;
			continue;
		}
		//What is the total elapsed time since this path was started
		//(server offset + time since it told us that)
		var timeElapsed = e.timeElapsedOnServer + (new Date().getTime()-e.lastUpdate);
		//How many tiles have we traveled since the beginning of the path
		var tilesTraveled = timeElapsed / millisecondsPerTile;
		
		//If we've traveled past our last tile, truncate it
		if (tilesTraveled > e.path.length) { tilesTraveled = e.path.length; }
		
		var mostRecentTile = Math.floor(tilesTraveled);	//Most recent tile fully reached
		var tween = tilesTraveled - mostRecentTile;	//time since this tile (note that last tile is too far back)
		
		//Calculate where we're supposed to be by looping through the path
		var dX = 0; var dY = 0;
		var startX = e.x; var startY = e.y;	//This must be initialized if path.length = 1
		var endX = 0; var endY = 0;
		for (var i = 0; i <= mostRecentTile; i++) {
			if (e.path[i] == 0) { dY--; }
			else if (e.path[i] == 1) { dY++; }
			else if (e.path[i] == 2) { dX--; }
			else if (e.path[i] == 3) { dX++; }
			
			//Set the poisitin when we're ready
			if (i == mostRecentTile-1) {
				startX = dX+e.x;	startY = dY+e.y;
			}
			if (i == mostRecentTile) {
				endX = dX+e.x;	endY = dY+e.y;
			}
		}
		
		//Now get the next tile position
		e.tweenX = (endX * tween) + (startX*(1-tween));
		e.tweenY = (endY * tween) + (startY*(1-tween));
		
		//If we're completely done, update x and allow movement again
		if (tilesTraveled == e.path.length) {
			e.x = endX;
			e.y = endY;
			e.path = [];
		}
		
	}
	////////////////
	////DEBUG STUFF
	////////////////
	if (game.debug.enabled) {
		if (engine.isKeyPressed("Digit1")) {
			game.debug.selected--;
		}
		if (engine.isKeyPressed("Digit2")) {
			game.debug.selected++;
		}
		if (engine.isKeyPressed("Digit4")) {
			if (game.debug.brushSize > 0) {
				game.debug.brushSize--;
			}
		}
		if (engine.isKeyPressed("Digit5")) {
			if (game.debug.brushSize < 10) {
				game.debug.brushSize++;
			}
		}
		if (engine.isKeyPressed("Digit6")) {
			game.debug.row++;
			game.debug.row %= 2;
			game.selected = 0;
		}
		if (engine.isKeyPressed("Digit9")) {
			game.debug.circleFill = !game.debug.circleFill;
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
		if (game.debug.row == 1) {
			if (engine.isKeyPressed("Digit3")) {	//Used pressed so we don't make extra entities
				//Tell server to make a new entity
				game.appendMessage += "&createEntity=" + selectedID + "|" + game.player.x + "|" + game.player.y;
			}
		} else {	
			if (engine.isKeyDown("Digit3")) {			
				for (var xOffset = -game.debug.brushSize; xOffset <= game.debug.brushSize; xOffset++) {
					for (var yOffset = -game.debug.brushSize; yOffset <= game.debug.brushSize; yOffset++) {
						var row = game.player.y + yOffset;
						var col = game.player.x + xOffset;
						
						if (row < 0 || row >= engine.height || col < 0 || col >= engine.width) { continue; }
						
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

function paintGame() {
	//Offset everything by the player's position
	var offsetX = Math.floor((game.player.tweenX - 12) * 32);
	var offsetY = Math.floor((game.player.tweenY - 9) * 32);
	
	for (var r = 0; r < game.map.rows; r++) {
		for (var c = 0; c < game.map.cols; c++) {
			//context.drawImage(images.tiles, 0,32*game.map.tile[r][c],32,32,32 * (c-offsetX), 32 * (r-offsetY),32,32);		
			//console.log(sprites.tiles[game.map.tile[r][c]].__frames[0].y);
			//engine.__sprites[game.map.tile[r][c]].draw(context,,);
			engine.drawSprite("tile" + game.map.tile[r][c],(32 * c) - offsetX,(32 * r)-offsetY);
		}
	}
	
	for (var id in game.entities) {
		var e = game.entities[id];
		var x = e.tweenX;
		var y = e.tweenY;
		
		engine.drawSprite(getSpriteTag(e),(x*32)-offsetX,(y*32)-offsetY);	
	}
	
	if (game.player.attacking) {
		engine.drawSprite("attack0",((game.player.x+1)*32)-offsetX,(game.player.y*32)-offsetY);
	}

	engine.recordKeyboard(true);
	engine.__context.fillStyle = "#FFF";
	engine.__context.fillText(engine.keyboardBuffer,10,engine.height - 30);
	
	//////////DEBUG MODE
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
			for (var i in e.path) {
				if (e.path[i] == 0) {y--;}
				if (e.path[i] == 1) {y++;}
				if (e.path[i] == 2) {x--;}
				if (e.path[i] == 3) {x++;}
				engine.__context.lineTo((x*32)-offsetX+16,(y*32)-offsetY+16);
			}
			engine.__context.stroke();
		}
		
		
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
		engine.__context.fillRect(0,42*2,150,42);
		engine.__context.fillStyle  = "#fff";
		engine.__context.fillText("Ping: "+game.ping + "ms",10,42*3-10);
	}
}


function move(xMovement, yMovement){
	if (game.player.path.length > 0) { return; }
	var moved = false;
	var playerX = game.player.x;
	var playerY = game.player.y;
	
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
		if (playerY < game.player.y) { direction = 0;}
		else if (playerY > game.player.y) { direction = 1;}
		else if (playerX < game.player.x) { direction = 2;}
		else if (playerX > game.player.x) { direction = 3;}
		
		//add this as our path
		game.player.path = [];
		game.player.path[0] = direction;
		game.player.direction = direction;
		game.player.timeElapsedOnServer = 0;
		game.player.lastUpdate = new Date().getTime();
		//game.player.set("x",playerX);
		//game.player.set("y",playerY);
	}
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
	if(col < 0 || row < 0 || col >= game.map.cols || row >= game.map.rows){
		//Do not pass go, do not collect $200
		return false;
	}
	
	//Loop over entities to see if there's anything on this tile
	for (var eid in game.entities) {
		var e = game.entities[eid];
		if (e.x == col && e.y == row && game.collidableEntities[e.type]) {
			return false;
		}
	}

	return !game.collidableTiles[game.map.tile[row][col]];
}

/*******************************************************************************
* Data Structures and Util                                                     *
*******************************************************************************/

//GAME STUFF
Entity.prototype = {
	x: 0,
	y: 0,
	id: 0,
	type: 0,
	tweenX: 0,
	tweenY: 0,		//Calculated by path
	direction: 1,	//0/1/2/3 = up/dn/lf/rt respectively (default down)
	attacking: false,
	path: [],
	timeElapsedOnServer: 0,
	lastUpdate: 0,		//Time in javascript time since last server update
	idle: false,		//if false, animation stops
	name: "unnamed",
	delta: [],
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