$(document).ready(function() {
	
	function toggledata(target) {
		target.children().slideToggle("fast");
		target.toggleClass("open");
	}
	
	$("ul.collapsible").click(function(e) {
		var $target = $(e.target);
		if( $target.is("label") ) {
			toggledata($target);
		}
		else {
			$target = $target.parent();
			if( $target.is("label") ) {
				toggledata($target);
			}
		}
	}).find("div").hide();
});