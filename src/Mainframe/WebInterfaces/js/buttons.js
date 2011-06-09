$(document).ready(function(){
        $("#okcban").click(function(){
		$('form').attr({action: "index.php?aaction=cban"});
		$('form').submit();
	});
	$("#caddok").click(function(){
		$('form').attr({action: "index.php?aaction=caddsub"});
		$('form').submit();
	});
        $("#okcedit").click(function(){
		$('form').attr({action: "index.php?aaction=ceditsub"});
		$('form').submit();
	});
	$("#ok").click(function(){
		$('form').attr({action: "submitshitgoeshere"});
		$('form').submit();
	});
	$("#cancel").click(function(){
		window.close();
	});
	$("#keys").click(function(){
		$('form').attr({action: "keyshitgoeshere"});
		$('form').submit();
	});
	$("#sanity").click(function(){
		$('form').attr({action: "sanityshitgoeshere"});
		$('form').submit();
	});

	$("#cadd").click(function(){
		window.open('www.google.com', 'caddw', 'width=350,height=175,status=no,resizable=no,scrollbars=no');
		$('form#cform').attr({
			action: "index.php?aaction=cadd",
			method: "post",
			target: "caddw"
		});
		$('form#cform').submit();
	});
	$("#cedit").click(function(){
		window.open('', 'ceditw', 'width=375,height=175,status=no,resizable=no,scrollbars=no');
		$('form#cform').attr({
			action: "index.php?aaction=cedit",
			method: "post",
			target: "ceditw"
		});
		$('form#cform').submit();
	});
	$("#cban").click(function(){
		window.open('', 'cbanw', 'width=300,height=150,status=no,resizable=no,scrollbars=no');
		$('form#cform').attr({
			action: "index.php?aaction=cban",
			method: "post",
			target: "cbanw"
		});
		$('form#cform').submit();
	});
	$("#cdelete").click(function(){
		window.open('', 'cdeletew', 'width=300,height=100,status=no,resizable=no,scrollbars=no');
		$('form#cform').attr({
			action: "admincdel.html",
			method: "post",
			target: "cdeletew"
		});
		$('form#cform').submit();
	});
	$("#cpoints").click(function(){
		window.open('', 'cpointsw', 'width=600,height=400,status=no,resizable=no,scrollbars=no');
		$('form#cform').attr({
			action: "adminpoints.html",
			method: "post",
			target: "cpointsw"
		});
		$('form#cform').submit();
	});

	$("#apost").click(function(){
		window.open('', 'apostw', 'width=600,height=400,status=no,resizable=no,scrollbars=no');
		$('form#aform').attr({
			action: "adminapost.html",
			method: "post",
			target: "apostw"
		});
		$('form#aform').submit();
	});
	$("#aedit").click(function(){
		window.open('', 'aeditw', 'width=600,height=400,status=no,resizable=no,scrollbars=no');
		$('form#aform').attr({
			action: "adminaedit.html",
			method: "post",
			target: "aeditw"
		});
		$('form#aform').submit();
	});
	$("#adelete").click(function(){
		window.open('', 'adeletew', 'width=400,height=100,status=no,resizable=no,scrollbars=no');
		$('form#aform').attr({
			action: "adminadel.html",
			method: "post",
			target: "adeletew"
		});
		$('form#aform').submit();
	});
});