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

            ajaxReq('index.php?aaction=startvpn','startvpn').complete(function(){
                    $("#vpnstart").attr("disabled", false);
                    $('#vpnstop').attr("disabled",false);
                });
		
	});

        /**
         * @param location String The URL to request
         * @param expectedAnswer String The expected answer type
         * @type jqXHR
         */
        function ajaxReq(location, expectedAnswer){
            return $.ajax({
                url: location,
                type: 'POST',
                dataType: 'json',
                success: function(data) {
                    $('#response').remove();
                    if(data == null){
                        $('#gamebuttons .content').prepend('<div id="response" style="display:none; position: absolute; left: 25%; right: 25%;height:auto;"><div style="background: none repeat scroll 0pt 0pt rgb(255, 170, 170); padding: 3px; border: 1px solid red; text-align: center; position: relative; margin-left: auto; margin-right: auto; min-width: 30em; margin-bottom: 3px;">An error ocurred while trying to perfom the specief action.</div></div>');
                    }else{
                        if(data.action==expectedAnswer){
                            $('#gamebuttons .content').prepend(data.reply);
                        }else{
                            $('#gamebuttons .content').prepend('<div id="response" style="display:none; position: absolute; left: 25%; right: 25%;height:auto;"><div style="background: none repeat scroll 0pt 0pt rgb(255, 170, 170); padding: 3px; border: 1px solid red; text-align: center; position: relative; margin-left: auto; margin-right: auto; min-width: 30em; margin-bottom: 3px;">(#2) An error ocurred while trying to perfom the specief action.</div></div>');
                        }
                    }

                    $('#gamebuttons').animate({heigth: ($('#gamebuttons').height()+8+$('#response').outerHeight())+'px'});
                    var pos = $('#contestants').position();
                    $('#contestants').animate({top: (pos.top +8+$('#response').outerHeight())+'px'});
                    $('#announcements').animate({top: (pos.top +8+$('#response').outerHeight())+'px'});
                    $('#response').slideDown();

                    timeout = setTimeout(function(){
                        $('#response').fadeOut(1000,function(){$('#response').remove()});
                        $('#gamebuttons').delay(1000).animate({height: '3.5em'});
                        $('#contestants').delay(1000).animate({top: '3.5em'});
                        $('#announcements').delay(1000).animate({top: '3.5em'});
                    },15000);
                },
                error: function() {
                    $('#response').remove();
                    $('#gamebuttons .content').prepend('<div id="response" style="display:none; position: absolute; left: 25%; right: 25%;height:auto;"><div style="background: none repeat scroll 0pt 0pt rgb(255, 170, 170); padding: 3px; border: 1px solid red; text-align: center; position: relative; margin-left: auto; margin-right: auto; min-width: 30em; margin-bottom: 3px;">An error ocurred while trying to perfom the specief action.</div></div>');

                    $('#gamebuttons').animate({heigth: ($('#gamebuttons').height()+8+$('#response').outerHeight())+'px'});
                    var pos = $('#contestants').position();
                    $('#contestants').animate({top: (pos.top +8+$('#response').outerHeight())+'px'});
                    $('#announcements').animate({top: (pos.top +8+$('#response').outerHeight())+'px'});
                    $('#response').slideDown();

                    timeout = setTimeout(function(){
                        $('#response').fadeOut(1000,function(){$('#response').remove()});
                        $('#gamebuttons').delay(1000).animate({height: '3.5em'});
                        $('#contestants').delay(1000).animate({top: '3.5em'});
                        $('#announcements').delay(1000).animate({top: '3.5em'});
                    },15000);
                }
            });
        }


        $("#vpnstop").click(function(){
            if(timeout != undefined)
                clearTimeout(timeout);
            $("#vpnstop").attr("disabled", true);

            ajaxReq('index.php?aaction=stopvpn', 'stopvpn').complete(function(){
                $("#vpnstop").attr("disabled", false);
                $("#vpnstart").attr("disabled", false);
            });
	});
        $("#gstart").click(function(){
            if(timeout != undefined)
                clearTimeout(timeout);
            $("#gstart").attr("disabled", true);

            ajaxReq('index.php?aaction=startgame','startgame').complete(function(){
                $("#gstart").attr("disabled", false);
                $("#gstop").attr("disabled", false);
            });
	});
        $("#gstop").click(function(){
            if(timeout != undefined)
                clearTimeout(timeout);
            $("#gstop").attr("disabled", true);

            ajaxReq('index.php?aaction=endgame','endgame').complete(function(){
                $("#gstop").attr("disabled", false);
            }).success(function()
                {$("#vpnstop").attr("disabled", true);
                    $("#vpnstart").attr("disabled", true);
                    $("#gstart").attr("disabled", true);
                });
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