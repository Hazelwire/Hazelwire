$(document).ready(function(){
        $("#okadel").click(function(){
		$('form').attr({action: "index.php?aaction=adel"});
		$('form').submit();
	});
        $("#okaedit").click(function(){
		$('form').attr({action: "index.php?aaction=aedit"});
		$('form').submit();
	});
        $("#okaadd").click(function(){
		$('form').attr({action: "index.php?aaction=aadd"});
		$('form').submit();
	});
        $("#okcdel").click(function(){
		$('form').attr({action: "index.php?aaction=cdel"});
		$('form').submit();
	});
        $("#okcban").click(function(){
		$('form').attr({action: "index.php?aaction=cban"});
		$('form').submit();
	});
	$("#caddok").click(function(){
		$('form').attr({action: "index.php?aaction=cadd"});
		$('form').submit();
	});
        $("#okcedit").click(function(){
		$('form').attr({action: "index.php?aaction=cedit"});
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
		$('form').attr({action: "download.php?team="+$('form input[name="cid"]').val()});
		$('form').submit();
	});
	$("#sanity").click(function(){
		$('form').attr({action: "sanityshitgoeshere"});
		$('form').submit();
	});

        // background: none repeat scroll 0pt 0pt rgb(170, 255, 170); padding: 3px; border: 1px solid rgb(0, 255, 0); text-align: center; position: relative; margin-left: auto; margin-right: auto; min-width: 30em; margin-bottom: 3px;
        var timeout;
        $("#vpnstart").click(function(){
            if(timeout != undefined)
	    	clearTimeout(timeout);
            $("#vpnstart").attr("disabled", true);

            $.ajax({
                url: 'index.php?aaction=startvpn',
                dataType: 'json',
                success: function(data) {
                    $('#response').remove();
                    if(data == null){
                        $('#gamebuttons .content').prepend('<div id="response" style="display:none; position: absolute; left: 25%; right: 25%;height:auto;"><div style="background: none repeat scroll 0pt 0pt rgb(255, 170, 170); padding: 3px; border: 1px solid red; text-align: center; position: relative; margin-left: auto; margin-right: auto; min-width: 30em; margin-bottom: 3px;">An error ocurred while trying to perfom the specief action.</div></div>');
                    }else{
                        if(data.action=='startvpn'){
                            $('#gamebuttons .content').prepend(data.reply);
                        }else{
                            $('#gamebuttons .content').prepend('<div id="response" style="display:none; position: absolute; left: 25%; right: 25%;height:auto;"><div style="background: none repeat scroll 0pt 0pt rgb(255, 170, 170); padding: 3px; border: 1px solid red; text-align: center; position: relative; margin-left: auto; margin-right: auto; min-width: 30em; margin-bottom: 3px;">(#2) An error ocurred while trying to perfom the specief action.</div></div>');
                        }
                    }

                    $('#gamebuttons').animate({heigth: ($('#gamebuttons').height()+$('#response').outerHeight())+'px'});
                    var pos = $('#contestants').position();
                    $('#contestants').animate({top: (pos.top +$('#response').outerHeight())+'px'});
                    $('#announcements').animate({top: (pos.top +$('#response').outerHeight())+'px'});
                    $('#response').slideDown();

                    timeout = setTimeout(function(){
                        $('#response').fadeOut(1000,function(){$('#response').remove()});
                        $('#gamebuttons').animate({height: '3.5em'});
                        $('#contestants').animate({top: '3.5em'});
                        $('#announcements').animate({top: '3.5em'});
                    },15000);
                },
                error: function() {
                    $('#response').remove();
                    $('#gamebuttons .content').prepend('<div id="response" style="display:none; position: absolute; left: 25%; right: 25%;height:auto;"><div style="background: none repeat scroll 0pt 0pt rgb(255, 170, 170); padding: 3px; border: 1px solid red; text-align: center; position: relative; margin-left: auto; margin-right: auto; min-width: 30em; margin-bottom: 3px;">An error ocurred while trying to perfom the specief action.</div></div>');
                    
                    $('#gamebuttons').animate({heigth: ($('#gamebuttons').height()+$('#response').outerHeight())+'px'});
                    var pos = $('#contestants').position();
                    $('#contestants').animate({top: (pos.top +$('#response').outerHeight())+'px'});
                    $('#announcements').animate({top: (pos.top +$('#response').outerHeight())+'px'});
                    $('#response').slideDown();

                    timeout = setTimeout(function(){
                        $('#response').fadeOut(1000,function(){$('#response').remove()});
                        $('#gamebuttons').animate({height: '3.5em'});
                        $('#contestants').animate({top: '3.5em'});
                        $('#announcements').animate({top: '3.5em'});
                    },15000);
                },
                complete:  function(){
                    $("#vpnstart").attr("disabled", false);
                }
            });
		
	});
        $("#vpnstop").click(function(){
		$('form').attr({action: "index.php?aaction=stopvpn"});
		$('form').submit();
	});
        $("#gstart").click(function(){
		$('form').attr({action: "index.php?aaction=stopvpn"});
		$('form').submit();
	});
        $("#gstop").click(function(){
		$('form').attr({action: "index.php?aaction=stopvpn"});
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
			action: "index.php?aaction=cdel",
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
			action: "index.php?aaction=aadd",
			method: "post",
			target: "apostw"
		});
		$('form#aform').submit();
	});
	$("#aedit").click(function(){
		window.open('', 'aeditw', 'width=600,height=400,status=no,resizable=no,scrollbars=no');
		$('form#aform').attr({
			action: "index.php?aaction=aedit",
			method: "post",
			target: "aeditw"
		});
		$('form#aform').submit();
	});
	$("#adelete").click(function(){
		window.open('', 'adeletew', 'width=400,height=100,status=no,resizable=no,scrollbars=no');
		$('form#aform').attr({
			action: "index.php?aaction=adel",
			method: "post",
			target: "adeletew"
		});
		$('form#aform').submit();
	});
});