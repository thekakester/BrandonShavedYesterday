/********************************************
* Basic game template
* Author: <yourname>
********************************************/

var x = -1;
var direction = 0;

//Called once at the beginning
game.init = function () {
	engine.preloadImage("assets/ball.png","ball");
	engine.onImagesLoaded(function() { engine.enterGameLoop(); });
}

//Called once per frame before paint
game.update = function() {
	x += direction;
	if (x > engine.width) { direction = -10; }
	if (x < 0) { direction = 10;}
}

//Called after update, once each frame
game.paint = function () {
	engine.drawImage("ball",x,100);
}

//Called every few milliseconds (usually 100ms) to talk to the server
game.sendMessage = function() {
	//Return the message to send to the server
	return "greet";
}

//Called when the server responds to the message we sent
game.onServerRespond = function(response) {
	console.log(response);
}