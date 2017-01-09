Sprite = function(image, width, height) {
		this.__image = image;
		this.__width = width;
		this.__height = height;
		this.__frames = [];
}

Sprite.prototype = {
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
	addFrame: function(newFrame) {
		console.log("Adding new frame with y " + newFrame.y);
		this.__frames.push(newFrame);
		for (var s in this.__frames) {
			console.log("---" + s);
		}
		console.log("pos 0 = " + this.__frames[0].y)
	},
}

Frame = function(x,y,duration) {
	this.x = x;
	this.y = y;
	this.duration = duration;
}

Frame.prototype = {
	x: 0,
	y: 0,
	duration: 0,
}