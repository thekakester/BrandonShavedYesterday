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

/***************************
* ENGINE API FUNCTIONS     *
***************************/

/*Start loading an image with a given path.
* This image will be stored as
* game.engine.images.<tag>
* WARNING: Make sure to use engine.onImagesLoaded()
* before using your images to make sure they are loaded
*
* Example:
* engine.preloadImage("assets/tiles.png","tiles");
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

/*Returns true if a key is pressed.  False otherwise
* keycode is the default javascript keycode convention.
* Example:
* if (engine.isKeyDown("ArrowUp")) { movePlayerUp(); }
*/
engine.isKeyDown = function(keycode){};

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
	draw: function(context, x, y) {
		var f = this.__frames[this.__frame];
		context.drawImage(this.__image,f.x,f.y,this.__width,this.__height,x,y,this.__width,this.__height);
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
* Section: 3rd party misc.                                                     *
* Author: 3rd Party Authors                                                    *
*******************************************************************************/

//Derived from underscore.js
//Much faster than other options
engine.__isFunction = function(obj) {
  return !!(obj && obj.constructor && obj.call && obj.apply);
};

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
