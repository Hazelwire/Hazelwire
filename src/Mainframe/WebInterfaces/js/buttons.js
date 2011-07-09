//Notifications

var notifyboxHeight = 0;

//Type is a classname representing the kind of feedback given. Use "notifybad" for a warning/negative feedback, "notifyneutral" for a neutral message and "notifygood" for positive feedback.
function addNotification(content, type) {
	$("#notifybox").append('<div class="notification ' + type + '">' + content + '</div>');
	$(".notification:last-child").click(function(event){
		notifyboxHeight = notifyboxHeight - $(event.target).outerHeight(true);
		$("#notifybox").animate({
			"height": notifyboxHeight
		}, "slow");
		$(event.target).slideToggle("slow", function() {
			$(event.target).remove();
		});
	});
	notifyboxHeight = notifyboxHeight + $(".notification:last-child").outerHeight(true);
	$("#notifybox").animate({
		"height": notifyboxHeight
	}, "slow");
}

//SETTING UP OUR POPUP
//0 means disabled; 1 means enabled;
var popupStatus = 0;

//loading popup with jQuery magic!
function loadPopup() {
	//loads popup only if it is disabled
	if(popupStatus==0) {
		$("#backgroundPopup").fadeIn("fast");
		$("#popup").fadeIn("fast");
		popupStatus = 1;
		bindDefaultText();
                loadFormButtons();
	}
}

//disabling popup with jQuery magic!
function disablePopup(){
	//disables popup only if it is enabled
	if(popupStatus==1){
		$("#backgroundPopup").fadeOut("fast");
		$("#popup").fadeOut("fast");
		popupStatus = 0;
	}
}

//centering popup
function centerPopup(duration) {
	//request data for centering
	var windowWidth = document.documentElement.clientWidth;
	var windowHeight = document.documentElement.clientHeight;
	var popupHeight = $("#popup").height();
	var popupWidth = $("#popup").width();
	//centering
	$("#popup").animate({
                "top": (($(window).height() - $("#popup").outerHeight()) / 2) + $(window).scrollTop() + "px",
                "left":(($(window).width() - $("#popup").outerWidth()) / 2) + $(window).scrollLeft() + "px"
	}, duration);
	//only need force for IE6

	$("#backgroundPopup").css({
		"height": windowHeight
	});
}

/**
 * @param location String The URL to request
 * @param payload String The data to send to server
 * @param expectedAnswer String The expected answer type
 * @param callback Callable Callback function which takes an object as parameter
 * @type jqXHR
 */
function ajaxReq(location,payload, expectedAnswer,callback){
    return $.ajax({
        url: location,
        type: 'POST',
        dataType: 'json',
        data: payload,
        success: function(data) {
            if(data == null){
                addNotification("Something went wrong when trying to contact the server: the response is null.", "notifybad");
            }else{
                if(data.action==expectedAnswer){
                    if($.isFunction(callback))
                    {
                        callback(data);
                    }
                }else{
                    addNotification("Something went wrong when trying to contact the server: the server returned an unexpected result.", "notifybad");
                }
            }
        },
        error: function(jqXHR, textStatus, errorThrown) {
            addNotification("Something went wrong when trying to contact the server: the server returned '" + textStatus + "'", "notifybad");
        }
    }).complete(function(){
            $("#popupClose").click(function(){
                disablePopup();
        });});
}


function updateClist(){
    ajaxReq("index.php?aaction=getcs", "", "getcs", function(data){
	$('#cform').html(data.reply);
	setCCollapseHandlers();
        for ( var i=0, len=openContestants.length; i<len; ++i ){
          $('label[for="teamid_'+openContestants[i]+ '"]').find('.cextrainfo').show();
        }
    });
}

function updateClistAuto(){
    updateClist();
    setTimeout("updateClistAuto()", 30000);
}

function updateAlist(){
    ajaxReq("index.php?aaction=getas", "", "getas", function(data){
	$('#announcementdisplay').html(data.reply);
	setACollapseHandlers();
    });
}

//buttons
function loadFormButtons(){
    $("#okaadd").click(function(){
            var data = "submitted=true&atitle=" + $('#atitle [name="atitle"]').val() + "&abody=" + $('#abody [name="abody"]').val();
            ajaxReq("index.php?aaction=aadd", data, "aaddReply",
                function(data){
                        if(data.success == true && data.errorcount == 0)
                            addNotification("Announcement added.", "notifygood");
                        else if(data.success == true)
                            addNotification("Announcement added, but with errors.", "notifyneutral");

                        for ( var i=0, len=data.errors.length; i<len; ++i ){
                          addNotification(data.errors[i], "notifybad");
                        }

                        if(data.success == true)
                            disablePopup();
                        updateAlist();
            });
    });
    $("#okadel").click(function(){
            var data = "submitted=true&aid=" + $('#acform [name="aid"]').val();
            ajaxReq("index.php?aaction=adel", data, "adelReply",
                function(data){
                        if(data.success == true && data.errorcount == 0)
                            addNotification("Announcement deleted.", "notifygood");
                        else if(data.success == true)
                            addNotification("Announcement deleted, but with errors.", "notifyneutral");

                        for ( var i=0, len=data.errors.length; i<len; ++i ){
                          addNotification(data.errors[i], "notifybad");
                        }

                        if(data.success == true)
                            disablePopup();
                        updateAlist();
            });
    });
    $("#okaedit").click(function(){
            var data = "submitted=true&atitle=" + $('#atitle [name="atitle"]').val() + "&abody=" + $('#abody [name="abody"]').val() +
                        "&aid=" + $('#acform [name="aid"]').val();
            ajaxReq("index.php?aaction=aedit", data, "aeditReply",
                function(data){
                        if(data.success == true && data.errorcount == 0)
                            addNotification("Announcement edited.", "notifygood");
                        else if(data.success == true)
                            addNotification("Announcement edited, but with errors.", "notifyneutral");

                        for ( var i=0, len=data.errors.length; i<len; ++i ){
                          addNotification(data.errors[i], "notifybad");
                        }

                        if(data.success == true)
                            disablePopup();
                        updateAlist();
            });
    });
    $("#okcdel").click(function(){
            var data = "contestant=" + $('#acform [name="cid"]').val() + "&cid=" + $('#acform [name="cid"]').val();
            ajaxReq("index.php?aaction=cdel", data, "cdelReply",
                function(data){
                        if(data.success == true && data.errorcount == 0)
                            addNotification("Contestant deleted.", "notifygood");
                        else if(data.success == true)
                            addNotification("Contestant deleted, but with errors.", "notifyneutral");

                        for ( var i=0, len=data.errors.length; i<len; ++i ){
                          addNotification(data.errors[i], "notifybad");
                        }

                        if(data.success == true)
                            disablePopup();
                        updateClist();
            });
    });
    $("#okcban").click(function(){
            var data = "cid=" + $('#acform [name="cid"]').val() + "&contestant=" + $('#acform [name="cid"]').val() + "&cbantime=" + $('#acform [name="cbantime"]').val();
            ajaxReq("index.php?aaction=cban", data, "cbanReply",
                function(data){
                        if(data.success == true && data.errorcount == 0)
                            addNotification("Contestant banned.", "notifygood");
                        else if(data.success == true)
                            addNotification("Contestant banned, but with errors.", "notifyneutral");

                        for ( var i=0, len=data.errors.length; i<len; ++i ){
                          addNotification(data.errors[i], "notifybad");
                        }

                        if(data.success == true)
                            disablePopup();
                        updateClist();

            });
    });
    $("#caddok").click(function(){
            var data = "cname=" + $('.cformfield [name="cname"]').val() + "&csubnet=" + $('.cformfield [name="csubnet"]').val()
                        + "&cvmip=" + $('.cformfield [name="cvmip"]').val() + "&cadd=add";
            ajaxReq("index.php?aaction=cadd", data, "caddReply",
                function(data){
                        if(data.success == true && data.errorcount == 0)
                            addNotification("Contestant added.", "notifygood");
                        else if(data.success == true)
                            addNotification("Contestant added, but with errors.", "notifyneutral");

                        for ( var i=0, len=data.errors.length; i<len; ++i ){
                          addNotification(data.errors[i], "notifybad");
                        }

                        if(data.success == true)
                            disablePopup();
                        updateClist();

            });
    });
    $("#okcedit").click(function(){
            var data = "cname=" + $('.cformfield [name="cname"]').val() + "&csubnet=" + $('.cformfield [name="csubnet"]').val()
                        + "&cvmip=" + $('.cformfield [name="cvmip"]').val() +"&cid="+$('#acform [name="cid"]').val() +"&cedit=save";
            ajaxReq("index.php?aaction=cedit", data, "ceditReply",
                function(data){
                        if(data.success == true && data.errorcount == 0)
                            addNotification("Contestant edited.", "notifygood");
                        else if(data.success == true)
                            addNotification("Contestant edited, but with errors.", "notifyneutral");

                        for ( var i=0, len=data.errors.length; i<len; ++i ){
                          addNotification(data.errors[i], "notifybad");
                        }

                        if(data.success == true)
                            disablePopup();
                        updateClist();

            });
    });
    $("#cancel").click(function(){
            disablePopup();
    });
    $("#keys").click(function(){
            window.location = "download.php?team="+$('#acform input[name="cid"]').val();
    });
    $("#sanity").click(function(){
            $('form').attr({action: "index.php?aaction=cedit"});
            $('input[name="cedit"]').val("forcesancheck");
            $('form').submit();
    });
}

function bindDefaultText() {
	$(".defaultText").focus(function(srcc)
	{
		if ($(this).val() == $(this)[0].title)
		{
			$(this).removeClass("defaultTextActive");
			$(this).val("");
		}
	});

	$(".defaultText").blur(function()
	{
		if ($(this).val() == "")
		{
			$(this).addClass("defaultTextActive");
			$(this).val($(this)[0].title);
		}
	});
	$(".defaultText").blur();
}

$(document).ready(function(){
        
        //CLOSING POPUP
	//Click the x event!
	$("#popupClose").click(function(){
		disablePopup();
	});
	//Click out event!
	$("#backgroundPopup").click(function(){
		disablePopup();
	});
	//Press Escape event!
	$(document).keypress(function(e){
		if(e.keyCode==27 && popupStatus==1){
			disablePopup();
		}
	});

	$(window).resize(function() {
		centerPopup(0);
	});

        $('#csanity').click(function(){
                 buildSanityTable();
        });
        $('#confok').click(function(){
                 $('form').submit();
        });
        // background: none repeat scroll 0pt 0pt rgb(170, 255, 170); padding: 3px; border: 1px solid rgb(0, 255, 0); text-align: center; position: relative; margin-left: auto; margin-right: auto; min-width: 30em; margin-bottom: 3px;
        $("#vpnstart").click(function(){
            $("#vpnstart").attr("disabled", true);

            ajaxReq('index.php?aaction=startvpn','','startvpn',function(data){
                    if(data.success == true && data.errorcount == 0)
                        addNotification(data.reply, "notifygood");
                    else if(data.success == true)
                        addNotification(data.reply, "notifyneutral");

                    for ( var i=0, len=data.errors.length; i<len; ++i ){
                                  addNotification(data.errors[i], "notifybad");
                                }

                }
                ).complete(function(){
                    $("#vpnstart").attr("disabled", false);
                    $('#vpnstop').attr("disabled",false);
                });

	});

        $("#vpnstop").click(function(){
            $("#vpnstop").attr("disabled", true);

            ajaxReq('index.php?aaction=stopvpn','', 'stopvpn',function(data){
                    if(data.success == true && data.errorcount == 0)
                        addNotification(data.reply, "notifygood");
                    else if(data.success == true)
                        addNotification(data.reply, "notifyneutral");

                    for ( var i=0, len=data.errors.length; i<len; ++i ){
                                  addNotification(data.errors[i], "notifybad");
                                }

                }
                ).complete(function(){
                $("#vpnstop").attr("disabled", false);
                $("#vpnstart").attr("disabled", false);
            });
	});

        $("#gstart").click(function(){
            $("#gstart").attr("disabled", true);

            ajaxReq('index.php?aaction=startgame','','startgame',function(data){
                    if(data.success == true && data.errorcount == 0)
                        addNotification(data.reply, "notifygood");
                    else if(data.success == true)
                        addNotification(data.reply, "notifyneutral");

                    for ( var i=0, len=data.errors.length; i<len; ++i ){
                                  addNotification(data.errors[i], "notifybad");
                    }

                }
                ).complete(function(){
                $("#gstart").attr("disabled", false);
                $("#gstop").attr("disabled", false);
            });
	});
        $("#gstop").click(function(){
            $("#gstop").attr("disabled", true);

            ajaxReq('index.php?aaction=endgame','','endgame',function(data){
                    if(data.success == true && data.errorcount == 0)
                        addNotification(data.reply, "notifygood");
                    else if(data.success == true)
                        addNotification(data.reply, "notifyneutral");

                    for ( var i=0, len=data.errors.length; i<len; ++i ){
                                  addNotification(data.errors[i], "notifybad");
                                }

                }
                ).complete(function(){
                $("#gstop").attr("disabled", true);
            }).success(function()
                {$("#vpnstop").attr("disabled", true);
                    $("#vpnstart").attr("disabled", true);
                    $("#gstart").attr("disabled", true);
                });
	});

	$("#cadd").click(function(){
                ajaxReq("index.php?aaction=cadd","","cadd",function(data){$("#popup").html(data.reply);}).complete(function(){
                    $("#popupcontent").css({
			"width": "300px",
			"height": "100px"
		});
		centerPopup(0);
		loadPopup();
                });
	});
	$("#cedit").click(function(){
                if($('#cform input:radio[name="contestant"]:checked').length == 0){
                    addNotification("Please select the contestant you wish to edit.", "notifyneutral");
                    return;
                }
		ajaxReq("index.php?aaction=cedit","contestant="+$('#cform input:radio[name="contestant"]:checked').val(),"cedit",function(data){$("#popup").html(data.reply);}).complete(function(){
			$("#popupcontent").css({
				"width": "300px",
				"height": "100px"
				});
				centerPopup(0);
				loadPopup();
		});
	});
	$("#cban").click(function(){
                if($('#cform input:radio[name="contestant"]:checked').length == 0){
                    addNotification("Please select the contestant you wish to ban.", "notifyneutral");
                    return;
                }
                ajaxReq("index.php?aaction=cban","contestant="+$('#cform input:radio[name="contestant"]:checked').val(),"cban",function(data){$("#popup").html(data.reply);}).complete(function(){
                    $("#popupcontent").css({
			"width": "135px",
			"height": "75px"
                    });
                    centerPopup(0);
                    loadPopup();
                });

	});
	$("#cdelete").click(function(){
                if($('#cform input:radio[name="contestant"]:checked').length == 0){
                    addNotification("Please select the contestant you wish to delete.", "notifyneutral");
                    return;
                }
                ajaxReq("index.php?aaction=cdel","contestant="+$('#cform input:radio[name="contestant"]:checked').val(),"cdel",function(data){$("#popup").html(data.reply);}).complete(function(){
                    $("#popupcontent").css({
                            "width": "125px",
                            "height": "2em"
                    });
                    centerPopup(0);
                    loadPopup();
                });

	});
	$("#cpoints").click(function(){

	});

	$("#apost").click(function(){
                ajaxReq("index.php?aaction=aadd","","aadd",function(data){$("#popup").html(data.reply);}).complete(function(){
                    $("#popupcontent").css({
                            "width": "600px",
                            "height": "400px"
                    });
                    centerPopup(0);
                    loadPopup();
                });

	});
	$("#aedit").click(function(){
                if($('#aform input:radio[name="announcement"]:checked').length == 0){
                    addNotification("Please select the announcement you wish to edit.", "notifyneutral");
                    return;
                }
                ajaxReq("index.php?aaction=aedit","announcement="+$('#aform input:radio[name="announcement"]:checked').val(),"aedit",function(data){$("#popup").html(data.reply);}).complete(function(){
                    $("#popupcontent").css({
                            "width": "600px",
                            "height": "400px"
                    });
                    centerPopup(0);
                    loadPopup();
                });
	});
	$("#adelete").click(function(){
                if($('#aform input:radio[name="announcement"]:checked').length == 0){
                    addNotification("Please select the announcement you wish to delete.", "notifyneutral");
                    return;
                }
                ajaxReq("index.php?aaction=adel","announcement="+$('#aform input:radio[name="announcement"]:checked').val(),"adel",function(data){$("#popup").html(data.reply);}).complete(function(){
                    $("#popupcontent").css({
                            "width": "125px",
                            "height": "2em"
                    });
                    centerPopup(0);
                    loadPopup();
                });

	});

});
