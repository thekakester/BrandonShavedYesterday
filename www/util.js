/*Sends a string query to the server then executes callback
* callback takes one argument, which is the response from the query in a byte buffer
*/
function query(query, callback) {
	//query = escape(query);
	$.ajax("g?" + query,{
		complete: function(result) {
			callback(ByteBuffer.wrap(result.responseText));
		}
	});
}

function tween(oldVal,newVal,tweenAmount) {
	tweenAmount = tweenAmount > 1 ? 1 : tweenAmount;
	tweenAmount = tweenAmount < 0 ? 0 : tweenAmount;
	return ((1-tweenAmount)*oldVal) + (tweenAmount * newVal);
}


/******************************************************
* BYTE BUFFER 
* -----------
* Author: Mitch
* Description: Made to mimic the Java byte buffer.
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
******************************************************/

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


/******************************************************
* IMAGE PRELOADER 
* -----------
* Author: Mitch
* Description: Images must be loaded before they can be drawn by the canvas
* This method will create a DOM object, load the image, and return a reference to ut
* 
* Usage:
* var image1 = PreloadImage("assets/myImage.png");
* var image2 = PreloadImage("assets/myOtherImage.png");
* PreloadImage.wait(function() { alert('done!'); });	//Wait until both images above load
*
******************************************************/
PreloadImage.__remaining = 0;
function PreloadImage(path) {
	PreloadImage.__remaining++;
	var img = document.createElement("img");
	img.setAttribute("src",path);
	img.className = "hidden";
	img.onload = function() {PreloadImage.__remaining--;}
	document.body.appendChild(img);
	return img;
}

PreloadImage.wait = function(callback) {
	if (PreloadImage.__remaining == 0) {
		callback();
	} else {
		setTimeout(function() {PreloadImage.wait(callback)},30);
	}
}