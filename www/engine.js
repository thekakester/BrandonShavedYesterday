/*******************************************************************************
********************************************************************************
**                                                                            **
**                 ENGINE API DECLARATIONS AND DOCUMENTATION                  **
**                                                                            **
********************************************************************************
*******************************************************************************/

/*Notes to developer:
Please keep lines under 100 characters*/
var engine = {};
var game = {};
game.init = function(){};	//Override this: Executes at beginning
game.update = function(){};	//Override this: Executes before draw each frame
game.draw = function(){};	//Override this: Executes at the end of each frame
game.sendMessage = function(){};	//Override: Send a message to the server
game.onServerRespond = function(response){};	//Override: Called when server responds

/***************************
* ENGINE SETTINGS          *
***************************/
//Change these to fit your game
engine.fps = 30;
engine.width = 800;
engine.height = 600;

//How often to comminicate with the server (time in ms)
engine.serverMessageDelay = 100;

/***************************
* ENGINE API FUNCTIONS     *
***************************/

/*Starts the game!
* This must be called after everything is done in game.init()
* The engine will then begin calling game.update() and game.draw()
* at appropriate times
*/
engine.enterGameLoop = function() {};

/*Start loading an image with a given path.
* This image can be referenced by the tag provided.
*
* WARNING: Make sure to use engine.onImagesLoaded()
* before using your images to make sure they are loaded
*
* Example:
* engine.preloadImage("assets/tiles.png","tiles");
*
* Example (Drawing at position 100,50 ):
* engine.drawImage("tiles",100,50);
*/
engine.preloadImage = function(path,tag){};

/*Set the callback function that will run when all images
* finish loading.  This only needs to be called once.
*
* Example:
* engine.preloadImage("assets/tiles.png","tiles");
* engine.preloadImage("assets/characters.png","characters");
* engine.onImagesLoaded(function() { alert("Done loading!"); }
*
* Technical Note: Only the last callback passed in will be used!
* The previous callback will be overwritten!
*/
engine.onImagesLoaded = function(callback){};

/****TODO****/
engine.drawImage = function(imageTag,x,y) {};

/*Create and store a sprite object from a preloaded image
* This can be accessed later using its tag.
* The width/height is of the SPRITE, not the source image.
*
* Example:
* var tmp = engine.createSprite("mySprite","myImage",32,32);
* var xSrc = 32;
* var ySrc = 64;
* var durationInFrames = 15;
* tmp.addFrame(xSrc,ySrc,durationInFrames);
*
* Example (Drawing at position 100,50 ):
* engine.drawSprite("mySprite",100,50);
*/
engine.createSprite = function(spriteTag,imageTag,width,height){};

/****TODO*****/
engine.drawSprite = function(spriteTag,x,y){};

/*Returns true if a key is pressed.  False otherwise
* keycode is the default javascript keycode convention.
* Example:
* if (engine.isKeyDown("ArrowUp")) { movePlayerUp(); }
*/
engine.isKeyDown = function(keycode){};

/**TODO DOCUMENT**/
engine.sendMessage = function(message,callback){}

/*This represents the time in milliseconds that the game is killing time
between game.paint() calls.
As long as this number is greater than 0, your game is running faster than
the time between frames.  If it is 0, consider optimizing your code to increase
the framerate!
Note: This value should only be read from, never written to*/
engine.leftoverTimePerFrame = 0;

/*******************************************************************************
********************************************************************************
**                                                                            **
**                  IMPLEMENTATIONS ONLY BEYOND THIS POINT                    **
**                (And private variables.  They're fine too)                  **
**                                                                            **
********************************************************************************
*******************************************************************************/

/*Notes to developer: Please follow style conventions.  Don't be lazy
Group appropriate sections and functions for readability */

/*******************************************************************************
* Section: HTML DOM Object and initialization                                  *
*******************************************************************************/
//Add our little bit of style
document.write("<style>.hidden{display:none;}</style>");

window.onload = function() {
	engine.canvas = document.createElement("canvas");
	engine.canvas.setAttribute("width",engine.width + "px");
	engine.canvas.setAttribute("height",engine.height + "px");
	document.body.appendChild(engine.canvas);
	engine.__context = engine.canvas.getContext('2d');
	game.init();
}

engine.enterGameLoop = function() {
	engine.__renderLoop();			//Starts the render Looper
	engine.__communicationLoop();	//Start the communication looping process
}

engine.__lastPaintTimeInMillis = 0;	//Must be initialized

/*This needs some explanation:
Render goes before paint.  Lets say our FPS calls for 30ms between frames.
This means that paint() needs to be called every 30ms, regardless of how
long update() takes and the previous paint() took.
Therefore, we must measure time taken between successive paint() calls*/
engine.__renderLoop = function() {
	//Update part
	game.update();
	engine.__updateSprites();
	
	//How long has it been since the last time we called paint?
	var timePast = new Date().getTime()-engine.__lastPaintTimeInMillis;
	var sleepTime = (1000/engine.fps) - timePast;
	
	//Snap time remaining between 0 and 1 second.
	sleepTime = (sleepTime < 0 || sleepTime > 1000) ? 0 : sleepTime;
	
	//Store this and make it accessable to the user
	engine.leftoverTimePerFrame = sleepTime;
	
	//Wait this much longer before calling paint
	setTimeout(function() { 
		engine.__lastPaintTimeInMillis = new Date().getTime();
		engine.__context.clearRect(0, 0, engine.width, engine.height);
		game.paint();
		
		//Re-loop (no stack overflow because this is a delegate!!!)
		engine.__renderLoop();
	},sleepTime);
}

engine.__communicationLoop = function() {
	var message = game.sendMessage();
	engine.sendMessage(message,engine.__onServerRespond);
	
}

engine.__onServerRespond = function(response) {
	game.onServerRespond(response);
	setTimeout(function() {engine.__communicationLoop()},engine.serverMessageDelay);
}

engine.__updateSprites = function () {
	for (var key in engine.__sprites) {
		engine.__sprites[key].nextFrame();
	}
}

engine.sendMessage = function(message,callback) {
	var url = "g?" + message;
	engine.__ajax(url,
		function(result) {callback(result);},
		function() { console.log("ERROR COMMUNICATING WITH: " + url); }
	);
}

/*******************************************************************************
* Section: Private Objects                                                     *
* Author: Mitch
*******************************************************************************/
engine.__images = [];	//Usage: engine.__images[tag];
engine.__sprites = [];	//Usage: engine.__sprites[tag];
engine.__keyboard = [];	//Usage: engine.__keyboard[keyCode]; (using js keycode)

/*******************************************************************************
* Section: Image Preloader                                                     *
* Author: Mitch                                                                *
*******************************************************************************/
engine.__imagesLoading = 0;
engine.__preloadImageCallback = function() {};

engine.preloadImage = function(path,tag) {
	engine.__imagesLoading++;
	var img = document.createElement("img");
	img.setAttribute("src",path);
	img.className = "hidden";
	img.onload = function() {engine.__preloadImageFinished();}
	document.body.appendChild(img);
	engine.__images[tag] = img;
}

engine.__preloadImageFinished = function() {
	engine.__imagesLoading--;
	if (engine.__imagesLoading == 0) {
		engine.__preloadImageCallback();
	}
}

engine.onImagesLoaded = function(callback) {
	if (engine.__isFunction(callback)) {
		engine.__preloadImageCallback = callback;
	} else {
		console.log("Warning: engine.onImagesLoaded() expects a function, got " + callback);
	}
}

engine.drawImage = function (imageTag) {
	arguments[0] = engine.__images[imageTag];
	engine.__context.drawImage.apply(engine.__context,arguments);
}

/*******************************************************************************
* Section: Sprites                                                             *
* Author: Mitch                                                                *
*******************************************************************************/

engine.createSprite = function(spriteTag, imageTag, width, height) {
	var sprite = new engine.__Sprite(spriteTag,imageTag,width,height);
	engine.__sprites[spriteTag] = sprite;
	return sprite;	//Return a reference for the user's convenience
}

engine.__Sprite = function(spriteTag, imageTag, width, height) {
	this.__image = engine.__images[imageTag];
	this.__width = width;
	this.__height = height;
	this.__frames = [];
	this.__tag = spriteTag;
	
	//Check to make sure our image is real
	if (this.__image == null || this.__image == undefined) {
		console.log("ERROR: Sprite creation failed for " + spriteTag +
		".  First use engine.__preloadImage(path,imageTag) with this tag: " +
		imageTag);
	}
}

engine.__Sprite.prototype = {
	__image: null,
	__width: 0,
	__height: 0,
	__frames: null,
	__frame: 0,
	__subFrame: 0,
	draw: function(x, y) {
		var f = this.__frames[this.__frame];
		engine.__context.drawImage(this.__image,f.x,f.y,this.__width,this.__height,x,y,this.__width,this.__height);
	},
	nextFrame: function() {
		this.__subFrame++;
		if (this.__subFrame > this.__frames[this.__frame].duration) {
			this.__subFrame = 0;
			this.__frame++;
			if(this.__frame >= this.__frames.length) {
				this.__frame = 0;
			}
		}
	},
	addFrame: function(x,y,duration) {
		var newFrame = new engine.__Frame(x,y,duration);
		this.__frames.push(newFrame);
	},
}

//Added for convenience
engine.__Frame = function(x,y,duration) {
	this.x = x;
	this.y = y;
	this.duration = duration;
}

engine.__Frame.prototype = {
	x: 0,
	y: 0,
	duration: 0,
}

engine.drawSprite = function (spriteTag,x,y) {
	engine.__sprites[spriteTag].draw(x,y);
}

/*******************************************************************************
* Section: Keyboard input                                                      *
* Author: Mitch                                                                *
*******************************************************************************/
window.onkeydown = function(e) {
	engine.__keyboard[e.code] = true;
}

window.onkeyup = function(e) {
	engine.__keyboard[e.code] = false;
}

engine.isKeyDown = function(keycode) {
	return !!engine.__keyboard[keycode];
}

/*******************************************************************************
* Section: ByteBuffer                                                          *
* Author: Mitch                                                                *
*******************************************************************************/

/*Description: Made to mimic the Java byte buffer.
* 
* Wrap (read) example:
* var buff = ByteBuffer.wrap(someByteArray);
* alert(buff.getInt());
*
*
* (BELOW NOT YET IMPLEMENTED)
* Allocate (write) example:
* var buff = new ByteBuffer();
* buff.putInt(10);
* buff.putInt(64);
* alert(buff.array());
*/

//Byte buffer written by mitch to match Java library
ByteBuffer = function() {}

//Byte Buffer Static Methods
ByteBuffer.wrap = function(stringData) {
	//Create a byte buffer to return
	var buffer = new ByteBuffer();
	for (var i = 0; i < stringData.length; i++) {
		buffer.__data[i] = stringData.charCodeAt(i);
	}
	return buffer;
}

ByteBuffer.toInt = function(fourByteArray) {
	return ByteBuffer.wrap(fourByteArray).getInt();
}

//Byte Buffer Class Values
ByteBuffer.prototype = {
	__data: [],
	__index: 0,
	getInt: function() {
		//Convert 4 bytes to an int and return it
		return (this.__data[this.__index++] << 24) + (this.__data[this.__index++] << 16) + (this.__data[this.__index++] << 6) + (this.__data[this.__index++]);
	},
	getByte: function() {
		return this.__data[this.__index++];
	},
}


/*******************************************************************************
* Section: 3rd party misc.                                                     *
* Author: 3rd Party Authors                                                    *
*******************************************************************************/

//Derived from underscore.js
//Much faster than other options
engine.__isFunction = function(obj) {
  return !!(obj && obj.constructor && obj.call && obj.apply);
};


engine.__ajax = function (url,successCallback,failureCallback) {
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState == XMLHttpRequest.DONE ) {
			if (xmlhttp.status == 200) {
				successCallback(xmlhttp.responseText);
			} else { failureCallback(); }
		}
    };
    xmlhttp.open("GET", url, true);
    xmlhttp.send();
}

/*******************************************************************************
********************************************************************************
**                                                                            **
**                              Scratch Zone                                  **
**                                                                            **
**            (I get it, it's not fun to document on the fly.                 **
**               Put WIP code and undocumented stuff below)                   **
**                                                                            **
********************************************************************************
*******************************************************************************/
