var context;	//The context we draw to
var game = {
	entities: [],		//Usage: game.entities[entityID]
	player: null,		//Usage: game.player  (reference to player entity)
	width: 0,
	height: 0,
	pid: 0,
	map: {},
}

function begin() {
	var canvas = document.getElementById("game-canvas");
	game.width = canvas.width;
	game.height = canvas.height;
	context = canvas.getContext('2d');
	
	//Init keyboard
	//TODO add isKeyDown() method in engine
	engine.__keyboard["ArrowUp"] = false;
	engine.__keyboard["ArrowDown"] = false;
	engine.__keyboard["ArrowLeft"] = false;
	engine.__keyboard["ArrowRight"] = false;
	
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
	query("init",function(buffer) {
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
	query("getpid",function(buffer) {
		game.pid = buffer.getInt();
		
		//Set the player entity
		game.player = new Entity(10,10);
		game.entities[game.pid] = game.player;
		console.log("Player " + game.pid + ": " + game.player.x + " " + game.player.y);
		
		begin_enterGameLoop();	//Next step
	});
}

function begin_enterGameLoop() {
	renderLooper();	//Starts the render Looper
	communicationLooper();	//Start the communication looping process
}

function communicationLooper() {
	//Tell the server where we are
	query("update&" + game.pid + "&" + game.player.x + "&" + game.player.y, function(buffer){
		var count = buffer.getInt();
		
		for (var i = 0; i < count; i++) {
			var eid = buffer.getInt();
			var x = buffer.getInt();
			var y = buffer.getInt();
			
			if (eid == game.pid) { continue; }
			var e = game.entities[eid];
			if (!e) { game.entities[eid] = new Entity(x,y); e = game.entities[eid];}
			e.oldX = tween(e.oldX,e.x,e.tween);
			e.oldY = tween(e.oldY,e.y,e.tween);
			e.tween = 0;
			e.x = x;
			e.y = y;
		}
		
		setTimeout(function() { communicationLooper(); },100);
	});
	
}

function renderLooper() {
	render();
	updateSprites();
	setTimeout(function() {renderLooper()},30);
}

function updateSprites() {
	for (var key in engine.__sprites) {
		engine.__sprites[key].nextFrame();
	}
}

function render() {
	//Handle movement
	var dX = 0;
	var dY = 0;
	if (engine.__keyboard["ArrowUp"]) {dY--;}
	if (engine.__keyboard["ArrowDown"]) {dY++;}
	if (engine.__keyboard["ArrowLeft"]) {dX--;}
	if (engine.__keyboard["ArrowRight"]) {dX++;}
	move(dX,dY);
	
	
	//Offset everything by the player's position
	var offsetX = Math.floor((tween(game.player.oldX,game.player.x,game.player.tween) - 7) * 32);
	var offsetY = Math.floor((tween(game.player.oldY,game.player.y,game.player.tween) - 7) * 32);
	
	context.clearRect(0,0,game.width,game.height);
	for (var r = 0; r < game.map.rows; r++) {
		for (var c = 0; c < game.map.cols; c++) {
			//context.drawImage(images.tiles, 0,32*game.map.tile[r][c],32,32,32 * (c-offsetX), 32 * (r-offsetY),32,32);		
			//console.log(sprites.tiles[game.map.tile[r][c]].__frames[0].y);
			engine.__sprites[game.map.tile[r][c]].draw(context,(32 * c) - offsetX,(32 * r)-offsetY);
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
		
		context.drawImage(engine.__images.characters, srcX,srcY,32,32,(32 * x)-offsetX, (32 * y)-offsetY,32,32);	
		e.tween+=0.2;
		if (e.tween > 1) {e.tween = 1;}
	}
	
	
}

//INPUT STUFF

/* e.code = 
 * 	ArrowUp
 *  Numpad0
 *  KeyA
 *  Digit1
 *  ShiftRight
 *  Space
 *  CapsLock
 *  MetalLeft (Windows key)
 *  ContextMenu (square key with lines in it?)
 */
 
window.onkeydown = function(e) {
	engine.__keyboard[e.code] = true;
}

window.onkeyup = function(e) {
	engine.__keyboard[e.code] = false;
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


//GAME STUFF
Entity.prototype = {
	x: 0,
	y: 0,
	tween: 1,
	oldX: 0,
	oldY: 0,
	name: "unnamed",
	delta: [],
	set: function(key,value) { this[key] = value; this.delta[key] = value;}
}

function Entity(x,y) {
	this.x = x;
	this.y = y;
}