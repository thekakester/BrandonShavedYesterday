$(document).ready(function(){
	setTimeout(function() {
		$("#scrollspan").append("<div id=\"mrskeltal\"></div>");
		$.get("test.html",function(data){
			$("#mrskeltal").html(data,setTimeout(function(){formatChatBox()},100));
			
		});
	},100);
});

function formatChatBox(){
	var canvasHeight = $("canvas").prop("height");
	var topHeight = canvasHeight / 10;
	var topWidth = topHeight * 8.34;
	var centerHeight = canvasHeight - (2 * topHeight);
	var marginHeight = (topHeight / 6 ) * -1;
	centerHeight = centerHeight + (-2 * marginHeight);
	
	$("#ScrollContainer").width(topWidth);
	
	$("#ScrollTop").width(topWidth);
	$("#ScrollTop").height(topHeight);
	
	$("#ScrollContent").css('margin',marginHeight +'px auto');
	$("#ScrollContent").height(centerHeight);
	$("#ScrollContent").width(topWidth);
	
	$("#ScrollBottom").width(topWidth);
	$("#ScrollBottom").height(topHeight);
	
	$("#ScrollContainer").removeClass("nodisp");
}

function appendMessage(value){
	var message = '<p ><span class="message">'+value+'</span></p>';
	$("#ChatBox").append(message)
}