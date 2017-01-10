/**An explicit definition of properties of the game object**/
game.entities = [];		//Usage: game.entities[entityID]
game.player = null;		//Usage: game.player  (reference to player entity)
game.map = {};

/*******************************************************************************
* INITIALIZATION                                                               *
*******************************************************************************/
game.init = function () {
	engine.preloadImage("assets/tiles.png","tiles");
	engine.preloadImage("assets/characters.png","characters");
	engine.onImagesLoaded(function() { begin_loadSprites(); });

}

function begin_loadSprites() {
	var tmp = engine.createSprite("0","tiles",32,32);	//Create sprite "0"
	tmp.addFrame(160,96,15);
	tmp.addFrame(160,128,15);
	
	var tmp = engine.createSprite("1","tiles",32,32);	//Create sprite "1"
	tmp.addFrame(64,320,15);
	tmp.addFrame(96,320,15);
	tmp.addFrame(128,320,15);
	tmp.addFrame(160,320,15);
	
	var tmp = engine.createSprite("2","tiles",32,32);	//Create sprite "2"
	tmp.addFrame(96,32,1);
	
	begin_loadMap();
}

function begin_loadMap() {
	//Async Load map
	console.log("Loading map");
	engine.sendMessage("init",function(response) {
		var buffer = ByteBuffer.wrap(response);
		game.map.rows = buffer.getInt();
		game.map.cols = buffer.getInt();
		game.map.tile = []
		for (var r = 0; r < game.map.rows; r++) {
			game.map.tile[r] = [];
			for (var c = 0; c < game.map.cols; c++) {
				game.map.tile[r][c] = buffer.getInt();
			}
		}
		
		begin_getPid();	//Next step
	});
}

function begin_getPid() {
	//Async Get unique player ID
	console.log("Getting player ID");
	engine.sendMessage("getpid",function(response) {
		var buffer = ByteBuffer.wrap(response);
		
		var pid = buffer.getInt();
		
		//Set the player entity
		game.player = new Entity(pid,10,10);
		game.entities[pid] = game.player;
		console.log("Player " + game.player.id + ": " + game.player.x + " " + game.player.y);
		
		engine.enterGameLoop();	//Next step
	});
}

/*******************************************************************************
* Server Communication                                                         *
*******************************************************************************/
game.sendMessage = function() {
	//Return the message to send to the server
	return "update&" + game.player.id + "&" + game.player.x + "&" + game.player.y;
}

game.onServerRespond = function(response) {
	var buffer = ByteBuffer.wrap(response);
	var count = buffer.getInt();
		
	for (var i = 0; i < count; i++) {
		var eid = buffer.getInt();
		var x = buffer.getInt();
		var y = buffer.getInt();
		
		if (eid == game.player.id) { continue; }
		var e = game.entities[eid];
		if (!e) { game.entities[eid] = new Entity(x,y); e = game.entities[eid];}
		e.oldX = tween(e.oldX,e.x,e.tween);
		e.oldY = tween(e.oldY,e.y,e.tween);
		e.tween = 0;
		e.x = x;
		e.y = y;
	}
}

/*******************************************************************************
* Update and Paint                                                             *
*******************************************************************************/

game.update = function() {}
game.paint = function () {
	//Handle movement
	var dX = 0;
	var dY = 0;
	if (engine.isKeyDown("ArrowUp")) {dY--;}
	if (engine.isKeyDown("ArrowDown")) {dY++;}
	if (engine.isKeyDown("ArrowLeft")) {dX--;}
	if (engine.isKeyDown("ArrowRight")) {dX++;}
	move(dX,dY);
	
	
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
	
	
}


function move(column, row){
	if (game.player.tween < 1) { return; }
	
	column = game.player.x + column;
	row = game.player.y + row;
	
	if(column < 0 || row < 0 || column >= game.map.cols || row >= game.map.rows){
		//Do not pass go, do not collect $200
		return;
	}
	
	if(game.map.tile[row][column]!==0){
		game.player.oldX = game.player.x;
		game.player.oldY = game.player.y;
		game.player.tween = 0;
		
		game.player.set("x",column);
		game.player.set("y",row);
	}
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