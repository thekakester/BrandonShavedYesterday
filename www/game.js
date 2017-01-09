var context;	//The context we draw to
var game = {};  //The main game object!

var images = {}

function begin() {
	var canvas = document.getElementById("game-canvas");
	game.width = canvas.width;
	game.height = canvas.height;
	
	images.tiles = PreloadImage("assets/tiles.png");
	images.tiles = PreloadImage("assets/characters.png");
	PreloadImage.wait();	//Wait for all images to load
	
    context = canvas.getContext('2d');
	begin_loadMap();	//Next step
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
		
		begin_enterGameLoop();	//Next step
	});
}

function begin_enterGameLoop() {
	renderLooper();	//Starts the render Looper
}

function renderLooper() {
	render();
	setTimeout(30,function() { renderLooper(); });
}

function render() {
	context.clearRect(0,0,game.width,game.height);
	for (var r = 0; r < game.map.rows; r++) {
		for (var c = 0; c < game.map.cols; c++) {
			context.drawImage(images.tiles, 0,32*game.map.tile[r][c],32,32,32 * c, 32 * r,32,32);		
		}
	}
}

//GAME STUFF
Entity.prototype = {
	x: 0,
	y: 0,
	name: "unnamed",
}

function Entity(x,y) {
	this.x = x;
	this.y = y;
}