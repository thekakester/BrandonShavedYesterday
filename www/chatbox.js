$(document).ready(function(){
	$("body").append("<div id=\"mrskeltal\"></div>");
	$.get("test.html",function(data){
		$("#mrskeltal").html(data);
	});
	reformatCss();
});


function escapeHtml(text) {
  var map = {
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#039;'
  };

  return text.replace(/[&<>"']/g, function(m) { return map[m]; });
}

function reformatCss(){
	
	console.log($("canvas").prop("height") );
	console.log($(".scrollcontainer") );
	$(".scrollcontainer").prop("height", $("canvas").prop("height"));
}