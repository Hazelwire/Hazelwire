$(document).ready(function() {

	$("div#timeline").toggle(function(e) {
		var $target = $(e.target);
		if( $target.is("div#timeline") ){
			$('#chart1').slideUp();
			$target.delay(400).animate({top: "50%"}, {duration: "fast"});
			$('#chart1').delay(600).show(1,function(){plot1.replot();$('#chart1').hide();});

			$('#chart1').delay(100).slideDown();

		}
	},function(e) {
		var $target = $(e.target);
		if( $target.is("div#timeline") ) {
     			$('#chart1').slideUp();
			$target.delay(400).animate({top: "0%"}, {duration: "fast"});
			$('#chart1').delay(600).show(1,function(){plot1.replot();$('#chart1').hide();});

			$('#chart1').delay(100).slideDown();

		}
	},function(e) {
		var $target = $(e.target);
		if( $target.is("div#timeline") ) {
			$('#chart1').slideUp();
			$target.delay(400).animate({top: "80%"}, {duration: "fast"});
			$('#chart1').delay(600).show(1,function(){plot1.replot();$('#chart1').hide();});
			$('#chart1').delay(100).slideDown();

		}
	});

});
