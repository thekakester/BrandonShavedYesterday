$(document).ready(function(){
	$("body").append("<div id=\"mrskeltal\"></div>");
	$.get("test.html",function(data){
		$("#mrskeltal").html(data);
	});
	reformatCss();
});




function reformatCss(){
	
	console.log($("canvas").prop("height") );
	console.log($(".scrollcontainer") );
	$(".scrollcontainer").prop("height", $("canvas").prop("height"));
}