$(document).ready(function() {
	
	$("div#timeline").toggle(function(e) {
		var $target = $(e.target);
		if( $target.is("div#timeline") ){
			$target.animate({top: "50%"}, {duration: "fast"});
			setTimeout("plot1.replot()",150);
		}
	},function(e) {
		var $target = $(e.target);
		if( $target.is("div#timeline") ) {
			$target.animate({top: "0%"}, {duration: "fast"});
			setTimeout("plot1.replot()",150);
		}
	},function(e) {
		var $target = $(e.target);
		if( $target.is("div#timeline") ) {
			$target.animate({top: "80%"}, {duration: "fast"});
			setTimeout("plot1.replot()",150);
		}
	});
	
});
