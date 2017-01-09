var context;	//The context we draw to
var game = {};  //The main game object!

var images = {}
var entities = [];

function begin() {
	var canvas = document.getElementById("game-canvas");
	game.width = canvas.width;
	game.height = canvas.height;
	context = canvas.getContext('2d');
	
	images.tiles = PreloadImage("assets/tiles.png");
	images.characters = PreloadImage("assets/characters.png");
	PreloadImage.wait(function() {
		begin_loadMap();	//Wait for images to load, then move to the next step
	});
	
    
	
}

function begin_loadMap() {
	//Async Load map
	query("init",function(x) {
		var buffer = ByteBuffer.wrap(x);
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
	query("getpid",function(x) {
		game.pid = ByteBuffer.toInt(x);
		
		//Set the player entity
		entities[game.pid] = new Entity(3,4);
		console.log("Player " + game.pid + ": " + entities[game.pid].x + " " + entities[game.pid].y);
		
		begin_enterGameLoop();	//Next step
	});
}

function begin_enterGameLoop() {
	renderLooper();	//Starts the render Looper
}

function renderLooper() {
	render();
	setTimeout(function() {renderLooper()},30);
}

function render() {
	context.clearRect(0,0,game.width,game.height);
	for (var r = 0; r < game.map.rows; r++) {
		for (var c = 0; c < game.map.cols; c++) {
			context.drawImage(images.tiles, 0,32*game.map.tile[r][c],32,32,32 * c, 32 * r,32,32);		
		}
	}
	
	for (var e in entities) {
		context.drawImage(images.characters, 0,0,32,32,32 * entities[e].x, 32 * entities[e].y,32,32);	
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
	switch (e.code) {
		case "ArrowUp":
			entities[game.pid].set("y",entities[game.pid].y -1);
			break;
		case "ArrowDown":
			entities[game.pid].set("y",entities[game.pid].y +1);
			break;
		case "ArrowLeft":
			entities[game.pid].set("x",entities[game.pid].x -1);
			break;
		case "ArrowRight":
			entities[game.pid].set("x",entities[game.pid].x +1);
			break;
		default:
			alert("Much Wow. Such.")
		
	}
	render();
}


//GAME STUFF
Entity.prototype = {
	x: 0,
	y: 0,
	name: "unnamed",
	delta: [],
	set: function(key,value) { this[key] = value; this.delta[key] = value;}
}

function Entity(x,y) {
	this.x = x;
	this.y = y;
}