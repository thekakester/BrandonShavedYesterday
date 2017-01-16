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
game.type = "menu";
game.debug = {};
game.debug.enabled = false;
game.debug.selectedTile = 0;
game.debug.brushSize = 1;

/*******************************************************************************
* INITIALIZATION                                                               *
*******************************************************************************/
game.init = function () {
	engine.preloadImage("assets/tiles.png","tiles");
	engine.preloadImage("assets/characters.png","characters");
	engine.onImagesLoaded(function() { begin_loadSprites(); });

}

function begin_loadSprites() {
	game.uniqueTileIDs = 0;
	var tmp = engine.createSprite(game.uniqueTileIDs++,"tiles",32,32);	//Sprite: Plain grass
	tmp.addFrame(32,0,15);
	
	var tmp = engine.createSprite(game.uniqueTileIDs++,"tiles",32,32);	//Sprite: Flowers
	tmp.addFrame(64,0,15);
	tmp.addFrame(96,0,15);
	tmp.addFrame(128,0,15);
	tmp.addFrame(160,0,15);
	
	var tmp = engine.createSprite(game.uniqueTileIDs++,"tiles",32,32);	//Sprite: Med Grass
	tmp.addFrame(0,32,15);
	
	var tmp = engine.createSprite(game.uniqueTileIDs++,"tiles",32,32);	//Sprite: Long Grass
	tmp.addFrame(32,32,15);
	
	var tmp = engine.createSprite(game.uniqueTileIDs++,"tiles",32,32);	//Sprite: Sandstone
	tmp.addFrame(64,32,15);
	
	var tmp = engine.createSprite(game.uniqueTileIDs++,"tiles",32,32);	//Sprite: Water
	game.collidableTiles[game.uniqueTileIDs-1] = true;
	tmp.addFrame(96,32,15);
	tmp.addFrame(128,32,15);
	
	var tmp = engine.createSprite(game.uniqueTileIDs++,"tiles",32,32);	//Sprite: Vert Bridge
	tmp.addFrame(160,32,15);
	
	var tmp = engine.createSprite(game.uniqueTileIDs++,"tiles",32,32);	//Sprite: Horiz Bridge
	tmp.addFrame(0,64,15);
	
	var tmp = engine.createSprite(game.uniqueTileIDs++,"tiles",32,32);	//Sprite: Stone
	tmp.addFrame(32,64,15);
	
	var tmp = engine.createSprite(game.uniqueTileIDs++,"tiles",32,32);	//Sprite: Lava
	game.collidableTiles[game.uniqueTileIDs-1] = true;
	tmp.addFrame(64,64,10);
	tmp.addFrame(96,64,10);
	tmp.addFrame(128,64,10);
	tmp.addFrame(160,64,10);
	tmp.addFrame(128,64,10);
	tmp.addFrame(96,64,10);
	
	
	begin_serverInit();
}

function begin_serverInit() {
	//Async Load map
	console.log("Getting player ID and map");
	engine.sendMessage("init=1",function(response) {
		var buffer = ByteBuffer.wrap(response);
		
		/* GET PLAYER ID */
		var pid = buffer.getInt();
		
		//Set the player entity
		game.player = new Entity(pid,10,10);
		game.entities[pid] = game.player;
		console.log("Player " + game.player.id + ": " + game.player.x + " " + game.player.y);
		
		/* GET MAP */
		game.map.rows = buffer.getInt();
		game.map.cols = buffer.getInt();
		game.map.tile = []
		for (var r = 0; r < game.map.rows; r++) {
			game.map.tile[r] = [];
			for (var c = 0; c < game.map.cols; c++) {
				game.map.tile[r][c] = buffer.getInt();
			}
		}
		console.log("Loaded map with " + game.map.rows + " rows and " + game.map.cols + " cols");
		
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
	
	/*---------CHAT-----------*/
	//TODO put chat code here
	
	
	//Last call is always update.  This asks for what has changed
	message += "&update=" + game.player.id;
	return message;
	
}

game.onServerRespond = function(response) {
	var buffer = ByteBuffer.wrap(response);
	while (true) {
		var responseType = buffer.getInt();
		if (responseType == 0) { break; }	//0 means end
		
		//Response 1: Entity Info Update
		if (responseType == 1) {
			var count = buffer.getInt();
			
			for (var i = 0; i < count; i++) {
				var eid = buffer.getInt();
				var x = buffer.getInt();
				var y = buffer.getInt();
				
				if (eid == game.player.id) { continue; }
				var e = game.entities[eid];
				if (!e) { game.entities[eid] = new Entity(eid,x,y); e = game.entities[eid];}
				e.oldX = tween(e.oldX,e.x,e.tween);
				e.oldY = tween(e.oldY,e.y,e.tween);
				e.tween = 0;
				e.x = x;
				e.y = y;
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
		tPrint(engine.keyboardBuffer)
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
		game.debug.selectedTile = 0;
		game.debug.brushSize = 1;
		console.log("Set debug mode to " + game.debug.enabled);
	}
	
	if (game.debug.enabled) {
		if (engine.isKeyPressed("Digit1")) {
			game.debug.selectedTile--;
		}
		if (engine.isKeyPressed("Digit2")) {
			game.debug.selectedTile++;
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
		
		//Assume X tiles.  add by X then mod by X.  Solves + and - changes
		game.debug.selectedTile += game.uniqueTileIDs;
		game.debug.selectedTile %= game.uniqueTileIDs;
		
		if (engine.isKeyDown("Digit3")) {
			var tile = game.debug.selectedTile;
			
			for (var xOffset = -game.debug.brushSize; xOffset <= game.debug.brushSize; xOffset++) {
				for (var yOffset = -game.debug.brushSize; yOffset <= game.debug.brushSize; yOffset++) {
					var row = game.player.y + yOffset;
					var col = game.player.x + xOffset;
					
					//Avoid out of bounds
					if (row < 0 || row >= engine.height-1 || col < 0 || col >= engine.width-1) { continue; }
					game.map.tile[row][col] = tile;
					game.map.delta[row + "|" + col + "|" + tile] = true;
				}
			}
		}
	}
}

function paintGame() {
	//Offset everything by the player's position
	var offsetX = Math.floor((tween(game.player.oldX,game.player.x,game.player.tween) - 7) * 32);
	var offsetY = Math.floor((tween(game.player.oldY,game.player.y,game.player.tween) - 7) * 32);
	
	for (var r = 0; r < game.map.rows; r++) {
		for (var c = 0; c < game.map.cols; c++) {
			//context.drawImage(images.tiles, 0,32*game.map.tile[r][c],32,32,32 * (c-offsetX), 32 * (r-offsetY),32,32);		
			//console.log(sprites.tiles[game.map.tile[r][c]].__frames[0].y);
			//engine.__sprites[game.map.tile[r][c]].draw(context,,);
			engine.drawSprite(game.map.tile[r][c],(32 * c) - offsetX,(32 * r)-offsetY);
		}
	}
	
	for (var id in game.entities) {
		var e = game.entities[id];
		var x = tween(e.oldX,e.x,e.tween);
		var y = tween(e.oldY,e.y,e.tween);
		
		var character = id % 8;
		var srcX = character % 4;
		srcX *= (32*3);
		srcX += 32;
		var srcY = Math.floor(character / 4);
		srcY *= (32 * 4);
		
		engine.drawImage("characters", srcX,srcY,32,32,(32 * x)-offsetX, (32 * y)-offsetY,32,32);	
		e.tween+=0.2;
		if (e.tween > 1) {e.tween = 1;}
	}
	
	//////////DEBUG MODE
	if (game.debug.enabled) {
		engine.__context.fillStyle = "#000";
		engine.__context.fillRect(0,0,engine.width,42);
		engine.__context.fillStyle = "#fff";
		engine.__context.fillRect(game.debug.selectedTile * (32+5), 0,32+10,32+10);
		
		//Draw percent of brushSize
		engine.__context.fillStyle = "#f00";
		var height = game.debug.brushSize * 4.2;	//0-10 * 42 is a size of 0 to 42
		engine.__context.fillRect(game.debug.selectedTile * (32+5), 42-height,32+10,height);
		
		//Draw tiles at the top for level editor
		for (var i =0 ; i < game.uniqueTileIDs; i++) {
			engine.drawSprite(i,(i*(32+5) + 5),5);
		}
	}
}

function move(xMovement, yMovement){
	if (game.player.tween < 1) { return; }
	
	var moved = false;
	var playerX = game.player.x;
	var playerY = game.player.y;
	
	//Player can walk anywhere in debug mode
	if (game.debug.enabled) {
		playerX += xMovement;
		playerY += yMovement;
		moved = true;
	} else {
		//Try moving x first
		if (xMovement != 0 && isPassable(playerY, playerX + xMovement)) {
			playerX += xMovement; moved = true;
		} else if (yMovement != 0 && isPassable(playerY + yMovement,playerX)) {
			playerY += yMovement; moved = true;
		}
	}
	
	if (moved) {
		game.player.oldX = game.player.x;
		game.player.oldY = game.player.y;
		game.player.tween = 0;
		
		game.player.set("x",playerX);
		game.player.set("y",playerY);
	}
}

//Returns true if there is a tile at position [row][col] and it is a passable tile
function isPassable(row,col) {
	if(col < 0 || row < 0 || col >= game.map.cols || row >= game.map.rows){
		//Do not pass go, do not collect $200
		return false;
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
	tween: 1,
	oldX: 0,
	oldY: 0,
	name: "unnamed",
	delta: [],
	set: function(key,value) { this[key] = value; this.delta[key] = value;}
}

function Entity(id,x,y) {
	this.id = id;
	this.x = x;
	this.y = y;
}

function tween(oldVal,newVal,tweenAmount) {
	tweenAmount = tweenAmount > 1 ? 1 : tweenAmount;
	tweenAmount = tweenAmount < 0 ? 0 : tweenAmount;
	return ((1-tweenAmount)*oldVal) + (tweenAmount * newVal);
}


function findPath(startX,startY,endX,endY,maxRadius) {
	
}