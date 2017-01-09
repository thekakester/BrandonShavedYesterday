var context;	//The context we draw to
var game = {};  //The main game object!

var images = {}
var entities = [];

var keyboard = [];

function begin() {
	var canvas = document.getElementById("game-canvas");
	game.width = canvas.width;
	game.height = canvas.height;
	context = canvas.getContext('2d');
	
	//Init keyboard
	keyboard["ArrowUp"] = false;
	keyboard["ArrowDown"] = false;
	keyboard["ArrowLeft"] = false;
	keyboard["ArrowRight"] = false;
	
	images.tiles = PreloadImage("assets/tiles.png");
	images.characters = PreloadImage("assets/characters.png");
	PreloadImage.wait(function() {
		begin_loadMap();	//Wait for images to load, then move to the next step
	});
	
    
	
}

function begin_loadMap() {
	//Async Load map
	query("init",function(buffer) {
		game.map = {}
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
		entities[game.pid] = new Entity(3,4);
		console.log("Player " + game.pid + ": " + entities[game.pid].x + " " + entities[game.pid].y);
		
		begin_enterGameLoop();	//Next step
	});
}

function begin_enterGameLoop() {
	renderLooper();	//Starts the render Looper
	communicationLooper();	//Start the communication looping process
}

function communicationLooper() {
	//Tell the server where we are
	var player = entities[game.pid];
	query("update&" + game.pid + "&" + player.x + "&" + player.y, function(buffer){
		var count = buffer.getInt();
		
		for (var i = 0; i < count; i++) {
			var eid = buffer.getInt();
			var x = buffer.getInt();
			var y = buffer.getInt();
			
			if (eid == game.pid) { continue; }
			var e = entities[eid];
			if (!e) { entities[eid] = new Entity(x,y); e = entities[eid];}
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
	setTimeout(function() {renderLooper()},30);
}

function render() {
	//Handle movement
	var dX = 0;
	var dY = 0;
	if (keyboard["ArrowUp"]) {dY--;}
	if (keyboard["ArrowDown"]) {dY++;}
	if (keyboard["ArrowLeft"]) {dX--;}
	if (keyboard["ArrowRight"]) {dX++;}
	move(dX,dY);
	
	
	
	context.clearRect(0,0,game.width,game.height);
	for (var r = 0; r < game.map.rows; r++) {
		for (var c = 0; c < game.map.cols; c++) {
			context.drawImage(images.tiles, 0,32*game.map.tile[r][c],32,32,32 * c, 32 * r,32,32);		
		}
	}
	
	for (var id in entities) {
		var e = entities[id];
		var x = tween(e.oldX,e.x,e.tween);
		var y = tween(e.oldY,e.y,e.tween);
		
		var character = id % 8;
		var srcX = character % 4;
		srcX *= (32*3);
		srcX += 32;
		var srcY = Math.floor(character / 4);
		srcY *= (32 * 4);
		
		context.drawImage(images.characters, srcX,srcY,32,32,32 * x, 32 * y,32,32);	
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
	keyboard[e.code] = true;
}

window.onkeyup = function(e) {
	keyboard[e.code] = false;
}

function move(column, row){
	if (entities[game.pid].tween < 1) { return; }
	
	column = entities[game.pid].x + column;
	row = entities[game.pid].y + row;
	
	if(column < 0 || row < 0 || column >= game.map.cols || row >= game.map.rows){
		//Do not pass go, do not collect $200
		return;
	}
	if(game.map.tile[row][column]!==0){
		entities[game.pid].oldX = entities[game.pid].x;
		entities[game.pid].oldY = entities[game.pid].y;
		entities[game.pid].tween = 0;
		
		entities[game.pid].set("x",column);
		entities[game.pid].set("y",row);
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